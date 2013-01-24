package org.TexasTorque.TexasTorque2013.subsystem.manipulator;

import org.TexasTorque.TexasTorque2013.io.DriverInput;
import org.TexasTorque.TexasTorque2013.io.RobotOutput;
import org.TexasTorque.TexasTorque2013.io.SensorInput;

public class Shooter
{
    
    private static Shooter instance;
    private RobotOutput robotOutput;
    private DriverInput driverInput;
    private SensorInput sensorInput;
    
    private double frontSpeed;
    private double rearSpeed;
    private double tiltSpeed;
    
    public static Shooter getInstance()
    {
        return (instance == null) ? instance = new Shooter() : instance;
    }
    
    public Shooter()
    {
        robotOutput = RobotOutput.getInstance();
        driverInput = DriverInput.getInstance();
        sensorInput = SensorInput.getInstance();
        rearSpeed = 0;
        tiltSpeed = 0;
        frontSpeed = 0;
    }
    
    public void run()
    {
        robotOutput.setShooterMotors(frontSpeed, rearSpeed);
        robotOutput.setShooterTiltMotor(tiltSpeed);
    }
    
}
