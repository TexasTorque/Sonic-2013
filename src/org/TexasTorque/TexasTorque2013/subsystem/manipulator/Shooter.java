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
    
    private double frontMotorSpeed;
    private double rearMotorSpeed;
    private double tiltMotorSpeed;
    
    public static Shooter getInstance()
    {
        return (instance == null) ? instance = new Shooter() : instance;
    }
    
    public Shooter()
    {
        robotOutput = RobotOutput.getInstance();
        driverInput = DriverInput.getInstance();
        sensorInput = SensorInput.getInstance();
        rearMotorSpeed = 0.0;
        tiltMotorSpeed = 0.0;
        frontMotorSpeed = 0.0;
    }
    
    public void run()
    {
        robotOutput.setShooterMotors(frontMotorSpeed, rearMotorSpeed);
        robotOutput.setShooterTiltMotor(tiltMotorSpeed);
    }
    
}
