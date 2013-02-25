package org.TexasTorque.TexasTorque2013.subsystem.manipulator;

import edu.wpi.first.wpilibj.Timer;
import org.TexasTorque.TexasTorque2013.TorqueSubsystem;
import org.TexasTorque.TexasTorque2013.constants.Constants;
import org.TexasTorque.TorqueLib.controlLoop.FeedforwardPIV;
import org.TexasTorque.TorqueLib.controlLoop.SimPID;
import org.TexasTorque.TorqueLib.controlLoop.TrajectorySmoother;

public class Shooter extends TorqueSubsystem
{   
    private static Shooter instance;
    
    private SimPID frontShooterPID;
    private SimPID rearShooterPID;
    
    public TrajectorySmoother trajectory;
    private FeedforwardPIV feedForward;
    
    private double frontShooterMotorSpeed;
    private double rearShooterMotorSpeed;
    private double desiredFrontShooterRate;
    private double desiredRearShooterRate;
    
    public double tiltMotorSpeed;
    private double desiredTiltPosition;
    
    private double previousTime;
    private double tiltEpsilon;
    private double previousAngle;
    
    public double tempVelocity;
    
    public static double tiltOverrideSpeed;
    public static double standardTiltPosition;
    public static double frontShooterOverrideSpeed;
    public static double rearShooterOverrideSpeed;
    public static double frontShooterRate;
    public static double rearShooterRate;
    public static double maxTiltVelocity;
    public static double maxTiltAcceleration;
    
    public static Shooter getInstance()
    {
        return (instance == null) ? instance = new Shooter() : instance;
    }
    
    private Shooter()
    {
        super();
        
        frontShooterPID = new SimPID();
        rearShooterPID = new SimPID();
        
        feedForward = new FeedforwardPIV();
        
        loadNewTrajectory();
        
        previousTime = 0.0;
        
        frontShooterMotorSpeed = Constants.MOTOR_STOPPED;
        rearShooterMotorSpeed = Constants.MOTOR_STOPPED;
        desiredFrontShooterRate = Constants.SHOOTER_STOPPED_RATE;
        desiredRearShooterRate = Constants.SHOOTER_STOPPED_RATE;
        
        tiltMotorSpeed = Constants.MOTOR_STOPPED;
        desiredTiltPosition = Constants.DEFAULT_STANDARD_TILT_POSITION;
        
        previousAngle = 0.0;
    }
    
    public void run()
    {   
        double currentTime = Timer.getFPGATimestamp();
        double dt = currentTime - previousTime;
        previousTime = currentTime;
        
        double velocity = (sensorInput.getTiltAngle() - previousAngle) / dt;
        tempVelocity = velocity;
        previousAngle = sensorInput.getTiltAngle();
        double error = desiredTiltPosition - sensorInput.getTiltAngle();
        
        trajectory.update(error, velocity, 0.0, dt);
        
        tiltMotorSpeed = feedForward.calculate(trajectory, error, velocity, dt);
        
        double frontSpeed = frontShooterPID.calcPID(sensorInput.getFrontShooterRate());
        double rearSpeed = rearShooterPID.calcPID(sensorInput.getRearShooterRate());
        
        frontShooterMotorSpeed = limitShooterSpeed(frontSpeed);
        rearShooterMotorSpeed = limitShooterSpeed(rearSpeed);
        
        robotOutput.setTiltMotor(tiltMotorSpeed);
        robotOutput.setShooterMotors(frontShooterMotorSpeed, rearShooterMotorSpeed);
    }
    
    public synchronized String logData()
    {
        String data = desiredTiltPosition + ",";
        data += tiltMotorSpeed + ",";
        data += sensorInput.getTiltAngle() + ",";
        
        data += desiredFrontShooterRate + ",";
        data += frontShooterMotorSpeed + ",";
        data += sensorInput.getFrontShooterRate() + ",";
        
        data += desiredRearShooterRate + ",";
        data += rearShooterMotorSpeed + ",";
        data += sensorInput.getRearShooterRate() + ",";
        
        return data;
    }
    
    public synchronized String getKeyNames()
    {
        String names = "DesiredTiltAngle,TiltMotorSpeed,ActualTiltAngle,"
                + "DesiredFrontShooterRate,FrontShooterMotorSpeed,ActualFrontShooterRate,"
                + "DesiredRearShooterRate,RearShooterMotorSpeed,ActualRearShooterRate";
        
        return names;
    }
    
    public synchronized void setShooterRates(double frontRate, double rearRate)
    {
        if(frontRate != desiredFrontShooterRate)
        {
            desiredFrontShooterRate = frontRate;
            frontShooterPID.setDesiredValue(desiredFrontShooterRate);
        }
        if( rearRate != desiredRearShooterRate)
        {
            desiredRearShooterRate = rearRate;
            rearShooterPID.setDesiredValue(desiredRearShooterRate);
        }
    }
    
    public synchronized void setTiltAngle(double degrees)
    {
        if(degrees != desiredTiltPosition)
        {
            desiredTiltPosition = degrees;
            feedForward.setSetpoint(desiredTiltPosition);
            loadNewTrajectory();
        }
    }
    
    public synchronized void loadParameters()
    {
        tiltOverrideSpeed = params.getAsDouble("S_TiltOverrideSpeed", 0.5);
        standardTiltPosition = params.getAsDouble("S_TiltStandardAngle", Constants.DEFAULT_STANDARD_TILT_POSITION);
        frontShooterOverrideSpeed = params.getAsDouble("S_FrontShooterOverrideSpeed", 0.7);
        rearShooterOverrideSpeed = params.getAsDouble("S_RearShooterOverrideSpeed", 0.5);
        frontShooterRate = params.getAsDouble("S_FrontShooterRate", Constants.DEFAULT_FRONT_SHOOTER_RATE);
        rearShooterRate = params.getAsDouble("S_RearShooterRate", Constants.DEFAULT_REAR_SHOOTER_RATE);
        maxTiltVelocity = params.getAsDouble("S_TiltMaxVelocity", 0.0);
        maxTiltAcceleration = params.getAsDouble("S_TiltMaxAcceleration", 0.0);
        
        double p = params.getAsDouble("S_FrontShooterP", 0.0);
        double i = params.getAsDouble("S_FrontShooterI", 0.0);
        double d = params.getAsDouble("S_FrontShooterD", 0.0);
        double e = params.getAsDouble("S_FrontShooterEpsilon", 0.0);
        double r = params.getAsDouble("S_FrontShooterDoneRange", 0.0);
        
        frontShooterPID.setConstants(p, i, d);
        frontShooterPID.setErrorEpsilon(e);
        frontShooterPID.setDoneRange(r);
        frontShooterPID.resetErrorSum();
        frontShooterPID.resetPreviousVal();
        
        p = params.getAsDouble("S_RearShooterP", 0.0);
        i = params.getAsDouble("S_RearShooterI", 0.0);
        d = params.getAsDouble("S_RearShooterD", 0.0);
        e = params.getAsDouble("S_RearShooterEpsilon", 0.0);
        r = params.getAsDouble("S_RearShooterDoneRange", 0.0);
        
        rearShooterPID.setConstants(p, i, d);
        rearShooterPID.setErrorEpsilon(e);
        rearShooterPID.setDoneRange(r);
        rearShooterPID.resetErrorSum();
        rearShooterPID.resetPreviousVal();
        
        p = params.getAsDouble("S_TiltP", 0.0);
        i = params.getAsDouble("S_TiltI", 0.0);
        double v = params.getAsDouble("S_TiltV", 0.0);
        e = params.getAsDouble("S_TiltEpsilon", 0.0);
        double ffv = params.getAsDouble("S_TiltFFV", 0.0);
        double ffa = params.getAsDouble("S_TiltFFA", 0.0);
        
        feedForward.setParams(p, i, v, ffv, ffa);
        
        tiltEpsilon = e;
        
        loadNewTrajectory();
    }
    
    private synchronized void loadNewTrajectory()
    {
        //trajectory = new TrajectorySmoother(maxTiltAcceleration, maxTiltVelocity);
        trajectory = new TrajectorySmoother(0.0, 6);
    }
    
    public synchronized boolean isVerticallyLocked()
    {
        return feedForward.onTarget(tiltEpsilon);
    }
    
    public synchronized boolean isAtStandardPosition()
    {
        return (isVerticallyLocked() && desiredTiltPosition == Constants.DEFAULT_STANDARD_TILT_POSITION);
    }
    
    public synchronized boolean isReadyToFire()
    {
        return (/*isVerticallyLocked() && */frontShooterPID.isDone() && rearShooterPID.isDone());
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
    
    public static double convertToRPM(double clicksPerSec)
    {
        return (clicksPerSec * 60) / 100;
    }
    
}
