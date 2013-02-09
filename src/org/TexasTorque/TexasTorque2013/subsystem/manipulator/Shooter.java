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
    private int desiredTiltPosition;
    private int desiredFrontShooterRate;
    private int desiredRearShooterRate;
    
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
        
        double p = params.getAsDouble("S_FrontShooterP", 0.0);
        double i = params.getAsDouble("S_FrontShooterI", 0.0);
        double d = params.getAsDouble("S_FrontShooterD", 0.0);
        int e = params.getAsInt("S_FrontShooterEpsilon", 0);
        
        frontShooterPID = new SimPID(p, i, d, e);
        
        p = params.getAsDouble("S_RearShooterP", 0.0);
        i = params.getAsDouble("S_RearShooterI", 0.0);
        d = params.getAsDouble("S_RearShooterD", 0.0);
        e = params.getAsInt("S_RearShooterEpsilon", 0);
        
        rearShooterPID = new SimPID(p, i, d, e);
        
        p = params.getAsDouble("S_TiltP", 0.0);
        i = params.getAsDouble("S_TiltI", 0.0);
        d = params.getAsDouble("S_TiltD", 0.0);
        e = params.getAsInt("S_TiltEpsilon", 0);
        
        tiltPID = new SimPID(p, i, d, e);
        
        frontMotorSpeed = Constants.MOTOR_STOPPED;
        rearMotorSpeed = Constants.MOTOR_STOPPED;
        tiltMotorSpeed = Constants.MOTOR_STOPPED;
        desiredTiltPosition = Constants.TILT_PARALLEL_POSITION;
        desiredFrontShooterRate = Constants.SHOOTER_STOPPED_RATE;
        desiredRearShooterRate = Constants.SHOOTER_STOPPED_RATE;
    }
    
    public void run()
    {
        double frontSpeed = frontShooterPID.calcPID(sensorInput.getFrontShooterRate());
        double rearSpeed = rearShooterPID.calcPID(sensorInput.getRearShooterRate());
        frontMotorSpeed = limitShooterSpeed(frontSpeed);
        rearMotorSpeed = limitShooterSpeed(rearSpeed);
        robotOutput.setShooterMotors(frontMotorSpeed, rearMotorSpeed);
        tiltMotorSpeed = tiltPID.calcPID((int)sensorInput.getTiltAngle());
        robotOutput.setShooterTiltMotor(tiltMotorSpeed);
    }
    
    public synchronized void setShooterRates(int frontRate, int rearRate)
    {
        desiredFrontShooterRate = frontRate;
        desiredRearShooterRate = rearRate;
        frontShooterPID.setDesiredValue(desiredFrontShooterRate);
        rearShooterPID.setDesiredValue(desiredRearShooterRate);
    }
    
    public synchronized void setTiltAngle(int degrees)
    {
        desiredTiltPosition = degrees;
        tiltPID.setDesiredValue(desiredTiltPosition);
    }
    
    public synchronized void loadFrontShooterPID()
    {
        double p = params.getAsDouble("S_FrontShooterP", 0.0);
        double i = params.getAsDouble("S_FrontShooterI", 0.0);
        double d = params.getAsDouble("S_FrontShooterD", 0.0);
        int e = params.getAsInt("S_FrontShooterEpsilon", 0);
        
        frontShooterPID.setConstants(p, i, d);
        frontShooterPID.setErrorEpsilon(e);
    }
    
    public synchronized void loadRearShooterPID()
    {
        double p = params.getAsDouble("S_RearShooterP", 0.0);
        double i = params.getAsDouble("S_RearShooterI", 0.0);
        double d = params.getAsDouble("S_RearShooterD", 0.0);
        int e = params.getAsInt("S_RearShooterEpsilon", 0);
        
        rearShooterPID.setConstants(p, i, d);
        rearShooterPID.setErrorEpsilon(e);
    }
    
    public synchronized void loadTiltPID()
    {
        double p = params.getAsDouble("S_TiltP", 0.0);
        double i = params.getAsDouble("S_TiltI", 0.0);
        double d = params.getAsDouble("S_TiltD", 0.0);
        int e = params.getAsInt("S_TiltEpsilon", 0);
        
        tiltPID.setConstants(p, i, d);
        tiltPID.setErrorEpsilon(e);
    }
    
    public synchronized boolean isVerticallyLocked()
    {
        return tiltPID.isDone();
    }
    
    public synchronized boolean isParallel()
    {
        return (isVerticallyLocked() && desiredTiltPosition == Constants.TILT_PARALLEL_POSITION);
    }
    
    public synchronized boolean isReadyToFire()
    {
        return (isVerticallyLocked() && frontShooterPID.isDone() && rearShooterPID.isDone());
    }
    
    private double limitShooterSpeed(double shooterSpeed)
    {
        if(shooterSpeed < 0.0)
        {
            return Constants.MOTOR_STOPPED;
        }
        else
        {
            return shooterSpeed;
        }
    }
    
}
