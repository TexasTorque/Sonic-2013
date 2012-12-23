package org.TexasTorque.TexasTorque2013.subsystem;

import org.TexasTorque.TexasTorque2013.io.*;
import org.TexasTorque.TexasTorque2013.constants.Constants;

public class Drivebase 
{
    
    private RobotOutput robotOutput;
    private DriverInput driverInput;
    private SensorInput sensorInput;
    
    private double leftDriveSpeed;
    private double rightDriveSpeed;
            
    public Drivebase()
    {
        robotOutput = RobotOutput.getInstance();
        driverInput = DriverInput.getInstance();
        sensorInput = SensorInput.getInstance();
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
        int ySign = (yAxis > 0) ? 1 : -1;
        int xSign = (xAxis > 0) ? 1 : -1;
        yAxis = Math.sqrt(Math.abs(yAxis)) * ySign;
        xAxis = Math.sqrt(Math.abs(xAxis)) * xSign;
        leftDriveSpeed = yAxis + xAxis;
        rightDriveSpeed = yAxis - xAxis;
    }
}
