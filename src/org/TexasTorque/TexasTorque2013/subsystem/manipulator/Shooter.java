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
    
    private double frontShooterKV;
    private double middleShooterKV;
    private double rearShooterKV;
    
    public static Shooter getInstance()
    {
        return (instance == null) ? instance = new Shooter() : instance;
    }
    
    private Shooter()
    {
        super();
        
        frontShooterPID = new SimPID();
        middleShooterPID = new SimPID();
        rearShooterPID = new SimPID();
        
        frontShooterMotorSpeed = Constants.MOTOR_STOPPED;
        middleShooterMotorSpeed = Constants.MOTOR_STOPPED;
        rearShooterMotorSpeed = Constants.MOTOR_STOPPED;
        desiredFrontShooterRate = Constants.SHOOTER_STOPPED_RATE;
        desiredRearShooterRate = Constants.SHOOTER_STOPPED_RATE;
    }
    
    public void run()
    {
        double frontSpeed = desiredFrontShooterRate * frontShooterKV + frontShooterPID.calcPID(sensorInput.getFrontShooterRate());
        double middleSpeed = desiredMiddleShooterRate * middleShooterKV + middleShooterPID.calcPID(sensorInput.getMiddleShooterRate());
        double rearSpeed = desiredRearShooterRate * rearShooterKV + rearShooterPID.calcPID(sensorInput.getRearShooterRate());
        
        frontShooterMotorSpeed = limitShooterSpeed(frontSpeed);
        middleShooterMotorSpeed = limitShooterSpeed(middleSpeed);
        rearShooterMotorSpeed = limitShooterSpeed(rearSpeed);
        
        if(desiredFrontShooterRate == Constants.SHOOTER_STOPPED_RATE)
        {
            frontShooterMotorSpeed = Constants.MOTOR_STOPPED;
        }
        if(desiredMiddleShooterRate == Constants.SHOOTER_STOPPED_RATE)
        {
            middleShooterMotorSpeed = Constants.MOTOR_STOPPED;
        }
        if(desiredRearShooterRate == Constants.SHOOTER_STOPPED_RATE)
        {
            rearShooterMotorSpeed = Constants.MOTOR_STOPPED;
        }
    }
    
    public void setToRobot()
    {
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
    
    public void stopShooter()
    {
        setShooterRates(Constants.SHOOTER_STOPPED_RATE, Constants.SHOOTER_STOPPED_RATE, Constants.SHOOTER_STOPPED_RATE);
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
        double r = params.getAsDouble("S_FrontShooterDoneRange", 0.0);
        frontShooterKV = params.getAsDouble("S_FrontShooterKV", 0.0);
        
        frontShooterPID.setConstants(p, 0.0, 0.0);
        frontShooterPID.setDoneRange(r);
        frontShooterPID.resetErrorSum();
        frontShooterPID.resetPreviousVal();
        
        p = params.getAsDouble("S_MiddleShooterP", 0.0);
        r = params.getAsDouble("S_MiddleShooterDoneRange", 0.0);
        middleShooterKV = params.getAsDouble("S_MiddleShooterKV", 0.0);
        
        middleShooterPID.setConstants(p, 0.0, 0.0);
        middleShooterPID.setDoneRange(r);
        middleShooterPID.resetErrorSum();
        middleShooterPID.resetPreviousVal();
        
        p = params.getAsDouble("S_RearShooterP", 0.0);
        r = params.getAsDouble("S_RearShooterDoneRange", 0.0);
        rearShooterKV = params.getAsDouble("S_RearShooterKV", 0.0);
        
        rearShooterPID.setConstants(p, 0.0, 0.0);
        rearShooterPID.setDoneRange(r);
        rearShooterPID.resetErrorSum();
        rearShooterPID.resetPreviousVal();
    }
    
}
