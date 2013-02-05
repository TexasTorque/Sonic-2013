package org.TexasTorque.TexasTorque2013.subsystem.drivebase;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.TexasTorque.TexasTorque2013.constants.Constants;
import org.TexasTorque.TexasTorque2013.io.*;
import org.TexasTorque.TorqueLib.util.Parameters;
import org.TexasTorque.TorqueLib.util.SimPID;

public class Drivebase 
{
    
    private static Drivebase instance;
    private RobotOutput robotOutput;
    private DriverInput driverInput;
    private SensorInput sensorInput;
    private Parameters params;
    private SimPID gyroPID;
    
    private double leftDriveSpeed;
    private double rightDriveSpeed;
    private int desiredGyroAngle;
    
    public static Drivebase getInstance()
    {
        return (instance == null) ? instance = new Drivebase() : instance;
    }
            
    public Drivebase()
    {
        robotOutput = RobotOutput.getInstance();
        driverInput = DriverInput.getInstance();
        sensorInput = SensorInput.getInstance();
        params = Parameters.getInstance();
        
        double p = params.getAsDouble("D_GyroP", 0.0);
        double i = params.getAsDouble("D_GyroI", 0.0);
        double d =  params.getAsDouble("D_GyroD", 0.0);
        int e =  params.getAsInt("D_GyroEpsilon", 0);
        
        gyroPID = new SimPID(p, i, d, e);
        
        leftDriveSpeed = 0.0;
        rightDriveSpeed = 0.0;
        desiredGyroAngle = 0;
    }
    
    public void run()
    {
        mixChannels(driverInput.driveController.getThrottle(), driverInput.driveController.getWheel());
        if(driverInput.shootVisionHigh())
        {
            desiredGyroAngle = (int)(sensorInput.getGyroAngle() + SmartDashboard.getNumber("azimuth", 0.0));
            gyroPID.setDesiredValue(desiredGyroAngle);
            double motorOutput = gyroPID.calcPID((int)sensorInput.getGyroAngle());
            leftDriveSpeed = motorOutput;
            rightDriveSpeed = -motorOutput;
        }
        if(driverInput.shiftHighGear())
        {
            robotOutput.setShifters(true);
        }
        if(driverInput.extendWheelyBar())
        {
            robotOutput.setWheelyBar(true);
        }
        robotOutput.setDriveMotors(leftDriveSpeed, rightDriveSpeed);
    }
    
    public synchronized void loadGyroPID()
    {
        double p = params.getAsDouble("D_GyroP", 0.0);
        double i = params.getAsDouble("D_GyroI", 0.0);
        double d =  params.getAsDouble("D_GyroD", 0.0);
        
        gyroPID.setConstants(p, i, d);
        
        gyroPID.setErrorEpsilon(params.getAsInt("D_GyroEpsilon", 0));
    }
    
    public synchronized boolean isHorizontallyLocked()
    {
        return gyroPID.isDone();
    }
    
    public void mixChannels(double yAxis, double xAxis)
    {
        yAxis = driverInput.applyDeadband(yAxis, Constants.SPEED_AXIS_DEADBAND);
        xAxis = driverInput.applyDeadband(xAxis, Constants.TURN_AXIS_DEADBAND);
        
        //simpleDrive(yAxis, xAxis);
        cheesyDrive(yAxis, xAxis);
    }
    
    public void simpleDrive(double yAxis, double xAxis)
    {
        int ySign = (yAxis > 0) ? 1 : -1;
        int xSign = (xAxis > 0) ? 1 : -1;
        
        yAxis = Math.sqrt(Math.abs(yAxis)) * ySign;
        xAxis = Math.sqrt(Math.abs(xAxis)) * xSign;
        
        leftDriveSpeed = yAxis + xAxis;
        rightDriveSpeed = yAxis - xAxis;
    }
    
    public void cheesyDrive(double throttle, double turn)
    {
        int ySign = (throttle > 0) ? 1 : -1;
        int xSign = (turn > 0) ? 1 : -1;
        throttle = Math.sqrt(Math.abs(throttle)) * ySign;
        turn = Math.sqrt(Math.abs(turn)) * xSign;
        double power;
        double trueSpeed;
        double RadiusOutter = Constants.MAX_DIAMETER / 2;
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
            SpeedInner = throttle * (RadiusInner / RadiusOutter);
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
