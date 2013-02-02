package org.TexasTorque.TexasTorque2013.subsystem.manipulator;

import org.TexasTorque.TexasTorque2013.constants.Constants;
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
    private SimPID tiltPID;
    private double frontMotorSpeed;
    private double rearMotorSpeed;
    private double tiltMotorSpeed;
    private double desiredTiltPosition;
    
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
        tiltPID = new SimPID(params.getAsDouble("TiltP", 0.0)
                , params.getAsDouble("TiltI", 0.0)
                , params.getAsDouble("TiltD", 0.0)
                , params.getAsInt("TiltEpsilon", 0));
        frontMotorSpeed = 0.0;
        rearMotorSpeed = 0.0;
        tiltMotorSpeed = 0.0;
        desiredTiltPosition = Constants.TILT_PARALLEL_POSITION;
    }
    
    public void run()
    {
        if(driverInput.getTrackTarget())
        {
            
        }
        else
        {
            desiredTiltPosition = Constants.TILT_PARALLEL_POSITION;
        }
        robotOutput.setShooterMotors(frontMotorSpeed, rearMotorSpeed);
        robotOutput.setShooterTiltMotor(tiltMotorSpeed);
    }
    
    public synchronized void setFrontShooterPID()
    {
        frontShooterPID.setConstants(params.getAsDouble("FrontShooterP", 0.0)
                , params.getAsDouble("FrontShooterI", 0.0)
                , params.getAsDouble("FrontShooterD", 0.0));
        frontShooterPID.setErrorEpsilon(params.getAsInt("FrontShooterEpsilon", 0));
    }
    
    public synchronized void setRearShooterPID()
    {
        rearShooterPID.setConstants(params.getAsDouble("RearShooterP", 0.0)
                , params.getAsDouble("RearShooterI", 0.0)
                , params.getAsDouble("RearShooterD", 0.0));
        rearShooterPID.setErrorEpsilon(params.getAsInt("RearShooterEpsilon", 0));
    }
    
    public synchronized void setTiltPID()
    {
        tiltPID.setConstants(params.getAsDouble("TiltP", 0.0)
                , params.getAsDouble("TiltI", 0.0)
                , params.getAsDouble("TiltD", 0.0));
        tiltPID.setErrorEpsilon(params.getAsInt("TiltEpsilon", 0));
    }
    
    public synchronized boolean isVerticallyLocked()
    {
        return tiltPID.isDone();
    }
    
    public synchronized boolean isParallel()
    {
        return (isVerticallyLocked() && desiredTiltPosition == Constants.TILT_PARALLEL_POSITION);
    }
    
}
