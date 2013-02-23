package org.TexasTorque.TexasTorque2013.subsystem.manipulator;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.TexasTorque.TexasTorque2013.TorqueSubsystem;
import org.TexasTorque.TexasTorque2013.constants.Constants;
import org.TexasTorque.TorqueLib.controlLoop.SimPID;

public class Shooter extends TorqueSubsystem
{   
    private static Shooter instance;
    
    private SimPID frontShooterPID;
    private SimPID rearShooterPID;
    private SimPID tiltPID;
    
    private double frontShooterMotorSpeed;
    private double rearShooterMotorSpeed;
    private double desiredFrontShooterRate;
    private double desiredRearShooterRate;
    
    private double tiltMotorSpeed;
    private double desiredTiltPosition;
    
    public static double tiltOverrideSpeed;
    public static double standardTiltPosition;
    public static double frontShooterOverrideSpeed;
    public static double rearShooterOverrideSpeed;
    public static double frontShooterRate;
    public static double rearShooterRate;
    
    public static Shooter getInstance()
    {
        return (instance == null) ? instance = new Shooter() : instance;
    }
    
    private Shooter()
    {
        super();
        
        frontShooterPID = new SimPID();
        rearShooterPID = new SimPID();
        tiltPID = new SimPID();
        
        loadParameters();
        
        frontShooterMotorSpeed = Constants.MOTOR_STOPPED;
        rearShooterMotorSpeed = Constants.MOTOR_STOPPED;
        desiredFrontShooterRate = Constants.SHOOTER_STOPPED_RATE;
        desiredRearShooterRate = Constants.SHOOTER_STOPPED_RATE;
        
        tiltMotorSpeed = Constants.MOTOR_STOPPED;
        desiredTiltPosition = Constants.DEFAULT_STANDARD_TILT_POSITION;
    }
    
    public void run()
    {   
        double frontSpeed = frontShooterPID.calcPID(sensorInput.getFrontShooterRate());
        double rearSpeed = rearShooterPID.calcPID(sensorInput.getRearShooterRate());
        
        frontShooterMotorSpeed = limitShooterSpeed(frontSpeed);
        rearShooterMotorSpeed = limitShooterSpeed(rearSpeed);
        
        tiltMotorSpeed = tiltPID.calcPID(sensorInput.getTiltAngle());
        
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
        String names = "DesiredTiltAngle,TiltMotorSpeed,ActualtiltAngle,"
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
            tiltPID.setDesiredValue(desiredTiltPosition);
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
        
        double p = params.getAsDouble("S_FrontShooterP", 0.0);
        double i = params.getAsDouble("S_FrontShooterI", 0.0);
        double d = params.getAsDouble("S_FrontShooterD", 0.0);
        double e = params.getAsDouble("S_FrontShooterEpsilon", 0.0);
        
        frontShooterPID.setConstants(p, i, d);
        frontShooterPID.setErrorEpsilon(e);
        frontShooterPID.resetErrorSum();
        frontShooterPID.resetPreviousVal();
        
        p = params.getAsDouble("S_RearShooterP", 0.0);
        i = params.getAsDouble("S_RearShooterI", 0.0);
        d = params.getAsDouble("S_RearShooterD", 0.0);
        e = params.getAsDouble("S_RearShooterEpsilon", 0.0);
        
        rearShooterPID.setConstants(p, i, d);
        rearShooterPID.setErrorEpsilon(e);
        rearShooterPID.resetErrorSum();
        rearShooterPID.resetPreviousVal();
        
        p = params.getAsDouble("S_TiltP", 0.0);
        i = params.getAsDouble("S_TiltI", 0.0);
        d = params.getAsDouble("S_TiltD", 0.0);
        e = params.getAsDouble("S_TiltEpsilon", 0.0);
        
        tiltPID.setConstants(p, i, d);
        tiltPID.setErrorEpsilon(e);
        tiltPID.resetErrorSum();
        tiltPID.resetPreviousVal();
    }
    
    public synchronized boolean isVerticallyLocked()
    {
        return tiltPID.isDone();
    }
    
    public synchronized boolean isAtStandardPosition()
    {
        return (isVerticallyLocked() && desiredTiltPosition == Constants.DEFAULT_STANDARD_TILT_POSITION);
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
    
    public static double convertToRPM(double clicksPerSec)
    {
        return (clicksPerSec * 60) / 100;
    }
    
}
