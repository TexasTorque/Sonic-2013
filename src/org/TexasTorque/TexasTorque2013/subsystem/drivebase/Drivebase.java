package org.TexasTorque.TexasTorque2013.subsystem.drivebase;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.TexasTorque.TexasTorque2013.TorqueSubsystem;
import org.TexasTorque.TexasTorque2013.constants.Constants;
import org.TexasTorque.TexasTorque2013.subsystem.manipulator.Climber;
import org.TexasTorque.TorqueLib.controlLoop.SimPID;
import org.TexasTorque.TorqueLib.controlLoop.TorquePID;
import org.TexasTorque.TorqueLib.controlLoop.TorquePIDThread;
import org.TexasTorque.TorqueLib.util.TorqueUtil;

public class Drivebase extends TorqueSubsystem
{   
    private static Drivebase instance;
    
    private SimPID encoderPID;
    private SimPID gyroPID;
    private TorquePIDThread visionCorrect;
    
    private Climber climber;
    
    private double leftDriveSpeed;
    private double rightDriveSpeed;
    
    private boolean shiftState;
    
    public static Drivebase getInstance()
    {
        return (instance == null) ? instance = new Drivebase() : instance;
    }
            
    private Drivebase()
    {
        super();
        
        encoderPID = new SimPID();
        gyroPID = new SimPID();
        
        climber = Climber.getInstance();
        
        leftDriveSpeed = Constants.MOTOR_STOPPED;
        rightDriveSpeed = Constants.MOTOR_STOPPED;
        
        shiftState = Constants.LOW_GEAR;
        
        visionCorrect = new TorquePIDThread();
        visionCorrect.setSetpoint(0.0);
    }
    
    public void run()
    {
        if(dashboardManager.getDS().isOperatorControl())
        {
           
            mixChannels(driverInput.getThrottle(), driverInput.getTurn());
           
            if(!driverInput.getAutoTargeting())
            {
                mixChannels(driverInput.driveController.getThrottle(), driverInput.driveController.getTwist());
            }
            else
            {
                double az = SmartDashboard.getNumber("azimuth",0.0);
                if(az>180)
                {
                    az -= 360; //Angle correction Expanded: az = -(360 - az)
                }
                visionCorrect.setCurrentPosition(az);
                mixTurn(visionCorrect.getOutput());
            }
           
           shiftState = driverInput.shiftHighGear();
           
           if(climber.isHanging())
           {
               shiftState = Constants.LOW_GEAR;
               
               leftDriveSpeed *= 0.4;
               rightDriveSpeed *= 0.4;
           }
        }
    }
    public void mixTurn(double t)
    {
        leftDriveSpeed = t;
        rightDriveSpeed = -t;
    }
    
    public void setToRobot()
    {
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
        yAxis = TorqueUtil.applyDeadband(yAxis, Constants.SPEED_AXIS_DEADBAND);
        xAxis = TorqueUtil.applyDeadband(xAxis, Constants.TURN_AXIS_DEADBAND);
        
        simpleDrive(yAxis, xAxis);
    }
    
    private void simpleDrive(double yAxis, double xAxis)
    {
        yAxis = TorqueUtil.sqrtHoldSign(yAxis);
        xAxis = TorqueUtil.sqrtHoldSign(xAxis);
        
        double leftSpeed = yAxis + xAxis;
        double rightSpeed = yAxis - xAxis;
        
        setDriveSpeeds(leftSpeed, rightSpeed);
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
        double p = params.getAsDouble("D_DriveEncoderP", 0.0);
        double i = params.getAsDouble("D_DriveEncoderI", 0.0);
        double d = params.getAsDouble("D_DriveEncoderD", 0.0);
        double e = params.getAsDouble("D_DriveEncoderEpsilon", 0.0);
        double r = params.getAsDouble("D_DriveEncoderDoneRange", 0.0);
        
        encoderPID.setConstants(p, i, d);
        encoderPID.setErrorEpsilon(e);
        encoderPID.setDoneRange(r);
        encoderPID.resetErrorSum();
        encoderPID.resetPreviousVal();
        
        p = params.getAsDouble("D_DriveGyroP", 0.0);
        i = params.getAsDouble("D_DriveGyroI", 0.0);
        d = params.getAsDouble("D_DriveGyroD", 0.0);
        e = params.getAsDouble("D_DriveGyroEpsilon", 0.0);
        r = params.getAsDouble("D_DriveGyroDoneRange", 0.0);
        
        gyroPID.setConstants(p, i, d);
        gyroPID.setErrorEpsilon(e);
        gyroPID.setDoneRange(r);
        gyroPID.resetErrorSum();
        gyroPID.resetPreviousVal();
    }
}
