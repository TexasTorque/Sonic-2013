package org.TexasTorque.TexasTorque2013.subsystem.manipulator;

import org.TexasTorque.TexasTorque2013.io.DriverInput;
import org.TexasTorque.TexasTorque2013.io.RobotOutput;
import org.TexasTorque.TexasTorque2013.io.SensorInput;
import org.TexasTorque.TorqueLib.util.Parameters;
import org.TexasTorque.TorqueLib.util.SimPID;

public class Shooter
{
    
    private static Shooter instance;
    private RobotOutput robotOutput;
    private DriverInput driverInput;
    private SensorInput sensorInput;
    private Parameters params;
    private SimPID frontShooterPID;
    private SimPID rearShooterPID;
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
        params = Parameters.getInstance();
        frontShooterPID = new SimPID(params.getAsDouble("FrontShooterP", 0.0)
                , params.getAsDouble("FrontShooterI", 0.0)
                , params.getAsDouble("FrontShooterD", 0.0)
                , params.getAsInt("FrontShooterEpsilon", 0));
        rearShooterPID = new SimPID(params.getAsDouble("RearShooterP", 0.0)
                , params.getAsDouble("RearShooterI", 0.0)
                , params.getAsDouble("RearShooterD", 0.0)
                , params.getAsInt("RearShooterEpsilon", 0));
        frontMotorSpeed = 0.0;
        rearMotorSpeed = 0.0;
        tiltMotorSpeed = 0.0;
    }
    
    public void run()
    {
        robotOutput.setShooterMotors(frontMotorSpeed, rearMotorSpeed);
        robotOutput.setShooterTiltMotor(tiltMotorSpeed);
    }
    
    public synchronized void setFrontShooterPID(double p, double i, double d, int e)
    {
    }
    
}
