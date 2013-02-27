package org.TexasTorque.TexasTorque2013.subsystem.drivebase;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.TexasTorque.TexasTorque2013.TorqueSubsystem;
import org.TexasTorque.TexasTorque2013.constants.Constants;
import org.TexasTorque.TorqueLib.controlLoop.SimPID;

public class Drivebase extends TorqueSubsystem
{   
    private static Drivebase instance;
    
    private SimPID gyroPID;
    
    private double leftDriveSpeed;
    private double rightDriveSpeed;
    private double desiredGyroAngle;
    
    public static double highSensitivity;
    public static double lowSensitivity;
    
    public static Drivebase getInstance()
    {
        return (instance == null) ? instance = new Drivebase() : instance;
    }
            
    private Drivebase()
    {
        super();
        
        gyroPID = new SimPID();
        
        leftDriveSpeed = Constants.MOTOR_STOPPED;
        rightDriveSpeed = Constants.MOTOR_STOPPED;
        desiredGyroAngle = 0.0;
    }
    
    public void run()
    {
        if(!dashboardManager.getDS().isAutonomous())
        {
           mixChannels(driverInput.getThrottle(), driverInput.getTurn());
           if(driverInput.shiftHighGear())
           {
               robotOutput.setShifters(true);
           }
           else
           {
               robotOutput.setShifters(false);
           }
        }
        robotOutput.setDriveMotors(leftDriveSpeed, rightDriveSpeed);
    }
    
    public synchronized String logData()
    {
        String data = leftDriveSpeed + ",";
        data += sensorInput.getLeftDriveEncoder() + ",";
        data += sensorInput.getLeftDriveEncoderRate() + ",";
        
        data += rightDriveSpeed + ",";
        data += sensorInput.getRightDriveEncoder() + ",";
        data += sensorInput.getRightDriveEncoderRate() + ",";
        
        data += sensorInput.getGyroAngle() + ",";
        
        return data;
    }
    
    public synchronized String getKeyNames()
    {
        String names = "LeftDriveSpeed,LeftDriveEncoderPosition,LeftDriveEncoderVelocity,"
                + "RightDriveSpeed,RightDriveEncoderPosition,RightDriveEncoderVelocity,"
                + "GyroAngle,";
        
        return names;
    }
    
    public synchronized void loadParameters()
    {
        highSensitivity = params.getAsDouble("D_HighSensitivity", Constants.DEFAULT_HIGH_SENSITIVITY);
        lowSensitivity = params.getAsDouble("D_LowSensitivity", Constants.DEFAULT_LOW_SENSITIVITY);
        
        double p = params.getAsDouble("D_GyroP", 0.0);
        double i = params.getAsDouble("D_GyroI", 0.0);
        double d =  params.getAsDouble("D_GyroD", 0.0);
        double e = params.getAsDouble("D_GyroEpsilon", 0.0);
        
        gyroPID.setConstants(p, i, d);
        gyroPID.setErrorEpsilon(e);
        gyroPID.resetErrorSum();
        gyroPID.resetPreviousVal();
    }
    
    private synchronized void calcGyroPID()
    {
        desiredGyroAngle = (sensorInput.getGyroAngle() + SmartDashboard.getNumber("azimuth", 0.0));
        gyroPID.setDesiredValue(desiredGyroAngle);
        
        double motorOutput = gyroPID.calcPID(sensorInput.getGyroAngle());
        
        leftDriveSpeed = motorOutput;
        rightDriveSpeed = -motorOutput;
    }
    
    public synchronized void horizontallyTrack()
    {
        calcGyroPID();
    }
    
    public synchronized boolean isHorizontallyLocked()
    {
        return gyroPID.isDone();
    }
    
    private double applySqrtCurve(double axisValue)
    {
        int sign = (axisValue > 0) ? 1 : -1;
        axisValue = Math.sqrt(Math.abs(axisValue)) * sign;
        return axisValue;
    }
    
    private void mixChannels(double yAxis, double xAxis)
    {
        yAxis = driverInput.applyDeadband(yAxis, Constants.SPEED_AXIS_DEADBAND);
        xAxis = driverInput.applyDeadband(xAxis, Constants.TURN_AXIS_DEADBAND);
        
        simpleDrive(yAxis, xAxis);
        //cheesyDrive(yAxis, xAxis);
    }
    
    private void simpleDrive(double yAxis, double xAxis)
    {
        yAxis = applySqrtCurve(yAxis);
        xAxis = applySqrtCurve(xAxis);
        
        leftDriveSpeed = yAxis + xAxis;
        rightDriveSpeed = yAxis - xAxis;
    }
    
    private void cheesyDrive(double throttle, double turn)
    {
        throttle = applySqrtCurve(throttle);
        turn = applySqrtCurve(turn);
        
        double power;
        double trueSpeed;
        double RadiusOuter = Constants.MAX_DIAMETER / 2;
        double RadiusInner = (Constants.MAX_DIAMETER - Constants.ROBOT_WIDTH) / 2;
        double SpeedInner = 0.0;
        if(!driverInput.shiftHighGear())
        {
            turn = turn * lowSensitivity;
        }
        else
        {
            turn = turn * highSensitivity;
        }
        if(turn == 0.0)
        {
            SpeedInner = throttle;
        }
        else
        {
            SpeedInner = throttle * (RadiusInner / RadiusOuter);
        }
        power = SpeedInner;
        trueSpeed = throttle;
        if(power > 1.0)
        {
            trueSpeed -= (power - 1.0);
            power = 1.0;
        }
        if(trueSpeed > 1.0)
        {
            power -= (trueSpeed - 1.0);
            trueSpeed = 1.0;
        }
        if(power < -1.0)
        {
            trueSpeed += (1.0 + power);
            power = -1.0;
        }
        if(trueSpeed < -1.0)
        {
            power += (-1.0 - trueSpeed);
            trueSpeed = -1.0;
        }
        if(throttle == 0 && Math.abs(turn) > 0.5)
        {
            power = turn;
            leftDriveSpeed = power;
            rightDriveSpeed = -power;
        }
        else if(throttle > 0)
        {
            if(turn > 0)
            {
                leftDriveSpeed = -power;
                rightDriveSpeed = -trueSpeed;
            }
            else
            {
                leftDriveSpeed = -trueSpeed;
                rightDriveSpeed = -power;
            }
        }
        else
        {
            if(turn > 0)
            {
                leftDriveSpeed = -trueSpeed;
                rightDriveSpeed = -power;
            }
            else
            {
                leftDriveSpeed = -power;
                rightDriveSpeed = -trueSpeed;
            }
        }
        
    }
}
