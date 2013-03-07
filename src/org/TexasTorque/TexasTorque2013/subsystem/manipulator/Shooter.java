package org.TexasTorque.TexasTorque2013.subsystem.manipulator;

import org.TexasTorque.TexasTorque2013.TorqueSubsystem;
import org.TexasTorque.TexasTorque2013.constants.Constants;
import org.TexasTorque.TorqueLib.controlLoop.SimPID;

public class Shooter extends TorqueSubsystem
{   
    private static Shooter instance;
    
    private SimPID frontShooterPID;
    private SimPID middleShooterPID;
    private SimPID rearShooterPID;
    
    private double frontShooterMotorSpeed;
    private double middleShooterMotorSpeed;
    private double rearShooterMotorSpeed;
    
    private double desiredFrontShooterRate;
    private double desiredMiddleShooterRate;
    private double desiredRearShooterRate;
    
    public static double frontShooterRate;
    public static double middleShooterRate;
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
        
        frontShooterMotorSpeed = Constants.MOTOR_STOPPED;
        middleShooterMotorSpeed = Constants.MOTOR_STOPPED;
        rearShooterMotorSpeed = Constants.MOTOR_STOPPED;
        desiredFrontShooterRate = Constants.SHOOTER_STOPPED_RATE;
        desiredRearShooterRate = Constants.SHOOTER_STOPPED_RATE;
    }
    
    public void run()
    {   
        double frontSpeed = frontShooterPID.calcPID(sensorInput.getFrontShooterRate());
        double middleSpeed = middleShooterPID.calcPID(sensorInput.getMiddleShooterRate());
        double rearSpeed = rearShooterPID.calcPID(sensorInput.getRearShooterRate());
        
        frontShooterMotorSpeed = limitShooterSpeed(frontSpeed);
        middleShooterMotorSpeed = limitShooterSpeed(middleSpeed);
        rearShooterMotorSpeed = limitShooterSpeed(rearSpeed);
        
        robotOutput.setShooterMotors(frontShooterMotorSpeed, middleShooterMotorSpeed, rearShooterMotorSpeed);
    }
    
    public void setShooterRates(double frontRate, double middleRate, double rearRate)
    {
        if(frontRate != desiredFrontShooterRate)
        {
            desiredFrontShooterRate = frontRate;
            frontShooterPID.setDesiredValue(desiredFrontShooterRate);
        }
        
        if(middleRate != desiredMiddleShooterRate)
        {
            desiredMiddleShooterRate = middleRate;
            middleShooterPID.setDesiredValue(desiredMiddleShooterRate);
        }
        
        if(rearRate != desiredRearShooterRate)
        {
            desiredRearShooterRate = rearRate;
            rearShooterPID.setDesiredValue(desiredRearShooterRate);
        }
    }
    
    public boolean isSpunUp()
    {
        return (frontShooterPID.isDone() && middleShooterPID.isDone() && rearShooterPID.isDone());
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
    
    public String getKeyNames()
    {
        String names = "DesiredFrontShooterRate,FrontShooterMotorSpeed,ActualFrontShooterRate,"
                + "DesiredMiddleShooterRate,MiddleShooterMotorSpeed,ActualMiddleShooterRate,"
                + "DesiredRearShooterRate,RearShooterMotorSpeed,ActualRearShooterRate,";
        
        return names;
    }
    
    public String logData()
    {
        String data = desiredFrontShooterRate + ",";
        data += frontShooterMotorSpeed + ",";
        data += sensorInput.getFrontShooterRate() + ",";
        
        data += desiredMiddleShooterRate + ",";
        data += middleShooterMotorSpeed + ",";
        data += sensorInput.getMiddleShooterRate();
        
        data += desiredRearShooterRate + ",";
        data += middleShooterMotorSpeed + ",";
        data += sensorInput.getRearShooterRate() + ",";
        
        return data;
    }
    
    public void loadParameters()
    {   
        frontShooterRate = params.getAsDouble("S_FrontShooterRate", Constants.DEFAULT_FRONT_SHOOTER_RATE);
        middleShooterRate = params.getAsDouble("S_MiddleShooterRate", Constants.DEFAULT_MIDDLE_SHOOTER_RATE);
        rearShooterRate = params.getAsDouble("S_RearShooterRate", Constants.DEFAULT_REAR_SHOOTER_RATE);
        
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
        
        p = params.getAsDouble("S_MiddleShooterP", 0.0);
        i = params.getAsDouble("S_MiddleShooterI", 0.0);
        d = params.getAsDouble("S_MiddleShooterD", 0.0);
        e = params.getAsDouble("S_MiddleShooterEpsilon", 0.0);
        r = params.getAsDouble("S_MiddleShooterDoneRange", 0.0);
        
        middleShooterPID.setConstants(p, i, d);
        middleShooterPID.setErrorEpsilon(e);
        middleShooterPID.setDoneRange(r);
        middleShooterPID.resetErrorSum();
        middleShooterPID.resetPreviousVal();
        
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
    }
    
}
