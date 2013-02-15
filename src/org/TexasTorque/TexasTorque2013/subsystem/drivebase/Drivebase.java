package org.TexasTorque.TexasTorque2013.subsystem.drivebase;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.TexasTorque.TexasTorque2013.constants.Constants;
import org.TexasTorque.TexasTorque2013.io.*;
import org.TexasTorque.TorqueLib.util.DashboardManager;
import org.TexasTorque.TorqueLib.util.Parameters;
import org.TexasTorque.TorqueLib.util.SimPID;
import org.TexasTorque.TorqueLib.util.TorqueLogging;

public class Drivebase 
{
    
    private static Drivebase instance;
    private DashboardManager dashboard;
    private RobotOutput robotOutput;
    private DriverInput driverInput;
    private SensorInput sensorInput;
    private Parameters params;
    private TorqueLogging logging;
    private SimPID gyroPID;
    private SimPID leftLockPID;
    private SimPID rightLockPID;
    
    private double leftDriveSpeed;
    private double rightDriveSpeed;
    private double desiredGyroAngle;
    private boolean baseLockSaved;
    private int baseLockLeftPosition;
    private int baseLockRightPosition;
    
    public static Drivebase getInstance()
    {
        return (instance == null) ? instance = new Drivebase() : instance;
    }
            
    public Drivebase()
    {
        dashboard = DashboardManager.getInstance();
        robotOutput = RobotOutput.getInstance();
        driverInput = DriverInput.getInstance();
        sensorInput = SensorInput.getInstance();
        params = Parameters.getInstance();
        logging = TorqueLogging.getInstance();
        
        double p = params.getAsDouble("D_GyroP", 0.0);
        double i = params.getAsDouble("D_GyroI", 0.0);
        double d = params.getAsDouble("D_GyroD", 0.0);
        double e = params.getAsDouble("D_GyroEpsilon", 0.0);
        
        gyroPID = new SimPID(p, i, d, e);
        
        p = params.getAsDouble("D_LeftLockP", 0.0);
        i = params.getAsDouble("D_LeftLockI", 0.0);
        d = params.getAsDouble("D_LeftLockD", 0.0);
        e = params.getAsDouble("D_LeftLockEpsilon", 0.0);
        
        leftLockPID = new SimPID(p, i, d, e);
        
        p = params.getAsDouble("D_RightLockP", 0.0);
        i = params.getAsDouble("D_RightLockI", 0.0);
        d = params.getAsDouble("D_RightLockD", 0.0);
        e = params.getAsDouble("D_RightLockEpsilon", 0.0);
        
        rightLockPID = new SimPID(p, i, d, e);
        
        leftDriveSpeed = Constants.MOTOR_STOPPED;
        rightDriveSpeed = Constants.MOTOR_STOPPED;
        desiredGyroAngle = 0.0;
        baseLockSaved = false;
        baseLockLeftPosition = 0;
        baseLockRightPosition = 0;
    }
    
    public void run()
    {
        if(!dashboard.getDS().isAutonomous())
        {
           mixChannels(driverInput.getThrottle(), driverInput.getTurn());
           if(driverInput.shootVisionHigh() && SmartDashboard.getBoolean("found", false))
           {
               horizontallyTrack();
           }
           else
           {
               baseLockSaved = false;
           }
           if(driverInput.shiftHighGear())
           {
               robotOutput.setShifters(true);
           }
        }
        robotOutput.setDriveMotors(leftDriveSpeed, rightDriveSpeed);
    }
    
    public synchronized void logData()
    {
        logging.logValue("LeftDriveSpeed", leftDriveSpeed);
        logging.logValue("LeftDriveEncoderPosition", sensorInput.getLeftDriveEncoder());
        logging.logValue("LeftDriveEncoderVelocity", sensorInput.getLeftDriveEncoderRate());
        logging.logValue("RightDriveSpeed", rightDriveSpeed);
        logging.logValue("RightDriveEncoderPosition", sensorInput.getRightDriveEncoder());
        logging.logValue("RightDriveEncoderVelocity", sensorInput.getRightDriveEncoderRate());
        logging.logValue("GyroAngle", sensorInput.getGyroAngle());
    }
    
    private synchronized void calcGyroPID()
    {
        desiredGyroAngle = (sensorInput.getGyroAngle() + SmartDashboard.getNumber("azimuth", 0.0));
        gyroPID.setDesiredValue(desiredGyroAngle);
        double motorOutput = gyroPID.calcPID(sensorInput.getGyroAngle());
        leftDriveSpeed = motorOutput;
        rightDriveSpeed = -motorOutput;
    }
    
    private synchronized void calcBaseLockPID()
    {
        if(!baseLockSaved)
        {
            baseLockSaved = true;
            baseLockLeftPosition = sensorInput.getLeftDriveEncoder();
            baseLockRightPosition = sensorInput.getRightDriveEncoder();
            leftLockPID.setDesiredValue(baseLockLeftPosition);
            rightLockPID.setDesiredValue(baseLockRightPosition);
        }
        leftDriveSpeed = leftLockPID.calcPID(sensorInput.getLeftDriveEncoder());
        rightDriveSpeed = rightLockPID.calcPID(sensorInput.getRightDriveEncoder());
    }
    
    public synchronized void horizontallyTrack()
    {
        if(isHorizontallyLocked())
        {
            calcBaseLockPID();
        }
        else
        {
            baseLockSaved = false;
            calcGyroPID();
        }
    }
    
    public synchronized void loadGyroPID()
    {
        double p = params.getAsDouble("D_GyroP", 0.0);
        double i = params.getAsDouble("D_GyroI", 0.0);
        double d =  params.getAsDouble("D_GyroD", 0.0);
        double e = params.getAsDouble("D_GyroEpsilon", 0.0);
        
        gyroPID.setConstants(p, i, d);
        gyroPID.setErrorEpsilon(e);
    }
    
    public synchronized void loadLockPID()
    {
        double p = params.getAsDouble("D_LeftLockP", 0.0);
        double i = params.getAsDouble("D_LeftLockI", 0.0);
        double d =  params.getAsDouble("D_LeftLockD", 0.0);
        double e = params.getAsDouble("D_LeftLockEpsilon", 0.0);
        
        leftLockPID.setConstants(p, i, d);
        leftLockPID.setErrorEpsilon(e);
        
        p = params.getAsDouble("D_RightLockP", 0.0);
        i = params.getAsDouble("D_RightLockI", 0.0);
        d = params.getAsDouble("D_RightLockD", 0.0);
        e = params.getAsDouble("D_RightLockEpsilon", 0.0);
        
        rightLockPID.setConstants(p, i, d);
        rightLockPID.setErrorEpsilon(e);
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
        
        //simpleDrive(yAxis, xAxis);
        cheesyDrive(yAxis, xAxis);
    }
    
    private void simpleDrive(double yAxis, double xAxis)
    {
        yAxis = applySqrtCurve(yAxis);
        xAxis = applySqrtCurve(yAxis);
        
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
            turn = turn * params.getAsDouble("D_LowSensitivity", Constants.DEFAULT_LOW_SENSITIVITY);
        }
        else
        {
            turn = turn * params.getAsDouble("D_HighSensitivity", Constants.DEFAULT_HIGH_SENSITIVITY);
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
