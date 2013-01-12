package org.TexasTorque.TexasTorque2013.subsystem.manipulator;

import org.TexasTorque.TexasTorque2013.io.DriverInput;
import org.TexasTorque.TexasTorque2013.io.RobotOutput;
import org.TexasTorque.TexasTorque2013.io.SensorInput;

public class Shooter
{
    private RobotOutput robotOutput;
    private DriverInput driverInput;
    private SensorInput sensorInput;
    
    private double frontSpeed;
    private double rearSpeed;
    
    public Shooter()
    {
        robotOutput = RobotOutput.getInstance();
        driverInput = DriverInput.getInstance();
        sensorInput = SensorInput.getInstance();
    }
    
    public void run()
    {
        robotOutput.setShooterMotors(frontSpeed, rearSpeed);
    }
    
}
