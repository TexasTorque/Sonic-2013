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
        
        double p = params.getAsDouble("FrontShooterP", 0.0);
        double i = params.getAsDouble("FrontShooterI", 0.0);
        double d = params.getAsDouble("FrontShooterD", 0.0);
        int e = params.getAsInt("FrontShooterEpsilon", 0);
        
        frontShooterPID = new SimPID(p, i, d, e);
        
        p = params.getAsDouble("RearShooterP", 0.0);
        i = params.getAsDouble("RearShooterI", 0.0);
        d = params.getAsDouble("RearShooterD", 0.0);
        e = params.getAsInt("RearShooterEpsilon", 0);
        
        rearShooterPID = new SimPID(p, i, d, e);
        
        p = params.getAsDouble("TiltP", 0.0);
        i = params.getAsDouble("TiltI", 0.0);
        d = params.getAsDouble("TiltD", 0.0);
        e = params.getAsInt("TiltEpsilon", 0);
        
        tiltPID = new SimPID(p, i, d, e);
        
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
    
    public synchronized void loadFrontShooterPID()
    {
        double p = params.getAsDouble("FrontShooterP", 0.0);
        double i = params.getAsDouble("FrontShooterI", 0.0);
        double d = params.getAsDouble("FrontShooterD", 0.0);
        
        frontShooterPID.setConstants(p, i, d);
        
        frontShooterPID.setErrorEpsilon(params.getAsInt("FrontShooterEpsilon", 0));
    }
    
    public synchronized void loadRearShooterPID()
    {
        double p = params.getAsDouble("RearShooterP", 0.0);
        double i = params.getAsDouble("RearShooterI", 0.0);
        double d = params.getAsDouble("RearShooterD", 0.0);
        
        rearShooterPID.setConstants(p, i, d);
        
        rearShooterPID.setErrorEpsilon(params.getAsInt("RearShooterEpsilon", 0));
    }
    
    public synchronized void loadTiltPID()
    {
        double p = params.getAsDouble("TiltP", 0.0);
        double i = params.getAsDouble("TiltI", 0.0);
        double d = params.getAsDouble("TiltD", 0.0);
        
        tiltPID.setConstants(p, i, d);
        
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
