package org.TexasTorque.TexasTorque2013.subsystem.drivebase;

import org.TexasTorque.TexasTorque2013.TorqueSubsystem;
import org.TexasTorque.TexasTorque2013.constants.Constants;

public class Drivebase extends TorqueSubsystem
{   
    private static Drivebase instance;
    
    private double leftDriveSpeed;
    private double rightDriveSpeed;
    
    private boolean shiftState;
    
    public double highSensitivity;
    public double lowSensitivity;
    
    public static Drivebase getInstance()
    {
        return (instance == null) ? instance = new Drivebase() : instance;
    }
            
    private Drivebase()
    {
        super();
        
        leftDriveSpeed = Constants.MOTOR_STOPPED;
        rightDriveSpeed = Constants.MOTOR_STOPPED;
        
        shiftState = Constants.LOW_GEAR;
    }
    
    public void run()
    {
        if(dashboardManager.getDS().isOperatorControl())
        {
           mixChannels(driverInput.getThrottle(), driverInput.getTurn());
           shiftState = driverInput.shiftHighGear();
        }
        
        robotOutput.setShifters(shiftState);
        robotOutput.setDriveMotors(leftDriveSpeed, rightDriveSpeed);
    }
    
    public void setDriveSpeeds(double leftSpeed, double rightSpeed)
    {
        leftDriveSpeed = leftSpeed;
        rightDriveSpeed = rightSpeed;
    }
    
    public void setShifters(boolean highGear)
    {
        if(highGear != shiftState)
        {
            shiftState = highGear;
        }
    }
    
    private void mixChannels(double yAxis, double xAxis)
    {
        yAxis = driverInput.applyDeadband(yAxis, Constants.SPEED_AXIS_DEADBAND);
        xAxis = driverInput.applyDeadband(xAxis, Constants.TURN_AXIS_DEADBAND);
        
        simpleDrive(yAxis, xAxis);
    }
    
    private void simpleDrive(double yAxis, double xAxis)
    {
        yAxis = applySqrtCurve(yAxis);
        xAxis = applySqrtCurve(xAxis);
        
        double leftSpeed = yAxis + xAxis;
        double rightSpeed = yAxis - xAxis;
        
        setDriveSpeeds(leftSpeed, rightSpeed);
    }
    
    private double applySqrtCurve(double axisValue)
    {
        int sign = (axisValue > 0) ? 1 : -1;
        axisValue = Math.sqrt(Math.abs(axisValue)) * sign;
        return axisValue;
    }
    
    public String getKeyNames()
    {
        String names = "LeftDriveSpeed,LeftDriveEncoderPosition,LeftDriveEncoderVelocity,"
                + "RightDriveSpeed,RightDriveEncoderPosition,RightDriveEncoderVelocity,"
                + "GyroAngle,ShiftState";
        
        return names;
    }
    
    public String logData()
    {
        String data = leftDriveSpeed + ",";
        data += sensorInput.getLeftDriveEncoder() + ",";
        data += sensorInput.getLeftDriveEncoderRate() + ",";
        
        data += rightDriveSpeed + ",";
        data += sensorInput.getRightDriveEncoder() + ",";
        data += sensorInput.getRightDriveEncoderRate() + ",";
        
        data += sensorInput.getGyroAngle() + ",";
        data += shiftState + ",";
        
        return data;
    }
    
    public void loadParameters()
    {
        highSensitivity = params.getAsDouble("D_HighSensitivity", Constants.DEFAULT_HIGH_SENSITIVITY);
        lowSensitivity = params.getAsDouble("D_LowSensitivity", Constants.DEFAULT_LOW_SENSITIVITY);
    }
}
