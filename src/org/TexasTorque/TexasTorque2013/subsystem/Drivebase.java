package org.TexasTorque.TexasTorque2013.subsystem;

import org.TexasTorque.TexasTorque2013.constants.Constants;
import org.TexasTorque.TexasTorque2013.io.*;
import org.TexasTorque.TorqueLib.util.Parameters;

public class Drivebase 
{
    
    private RobotOutput robotOutput;
    private DriverInput driverInput;
    private SensorInput sensorInput;
    private Parameters params;
    
    private double leftDriveSpeed;
    private double rightDriveSpeed;
            
    public Drivebase()
    {
        robotOutput = RobotOutput.getInstance();
        driverInput = DriverInput.getInstance();
        sensorInput = SensorInput.getInstance();
        params = Parameters.getInstance();
        leftDriveSpeed = 0.0;
        rightDriveSpeed = 0.0;
    }
    
    public void run()
    {
        robotOutput.setLeftDriveMotors(leftDriveSpeed);
        robotOutput.setRightDriveMotors(rightDriveSpeed);
    }
    
    public void mixChannels(double yAxis, double xAxis)
    {
        yAxis = driverInput.applyDeadband(yAxis, Constants.SPEED_AXIS_DEADBAND);
        xAxis = driverInput.applyDeadband(xAxis, Constants.TURN_AXIS_DEADBAND);
        simpleDrive(yAxis, xAxis);
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
        double power;
        double trueSpeed;
        double RadiusOutter = Constants.MAX_DIAMETER / 2;
        double RadiusInner = (Constants.MAX_DIAMETER - Constants.ROBOT_WIDTH) / 2;
        double SpeedInner = 0.0;
        if(driverInput.getLowGear())
        {
            turn = turn * params.getAsDouble("LowSensitivity", 1.25);
        }
        else if(driverInput.getHighGear())
        {
            turn = turn * params.getAsDouble("HighSensitivity", 0.7);
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
        if((throttle == 0) && (turn < -0.52 || (turn > 0.52)))
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
