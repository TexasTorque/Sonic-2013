package org.TexasTorque.TexasTorque2013.subsystem.manipulator;

import org.TexasTorque.TexasTorque2013.TorqueSubsystem;
import org.TexasTorque.TexasTorque2013.constants.Constants;
import org.TexasTorque.TorqueLib.controlLoop.TorquePID;

public class Shooter extends TorqueSubsystem
{   
    private static Shooter instance;
    
    private TorquePID frontShooterPID;
    private TorquePID middleShooterPID;
    private TorquePID rearShooterPID;
    
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
        
        frontShooterPID = new TorquePID();
        middleShooterPID = new TorquePID();
        rearShooterPID = new TorquePID();
        
        frontShooterMotorSpeed = Constants.MOTOR_STOPPED;
        middleShooterMotorSpeed = Constants.MOTOR_STOPPED;
        rearShooterMotorSpeed = Constants.MOTOR_STOPPED;
        
        desiredFrontShooterRate = Constants.SHOOTER_STOPPED_RATE;
        desiredMiddleShooterRate = Constants.SHOOTER_STOPPED_RATE;
        desiredRearShooterRate = Constants.SHOOTER_STOPPED_RATE;
    }
    
    public void run()
    {
        double frontSpeed = frontShooterPID.calculate(sensorInput.getFrontShooterRate());
        double middleSpeed = middleShooterPID.calculate(sensorInput.getMiddleShooterRate());
        double rearSpeed = rearShooterPID.calculate(sensorInput.getRearShooterRate());
        
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
        
        if(driverInput.backFeed())
        {
            frontShooterMotorSpeed = Constants.MOTOR_STOPPED;
            middleShooterMotorSpeed = Constants.MOTOR_STOPPED;
            rearShooterMotorSpeed = -0.5;
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
            frontShooterPID.setSetpoint(desiredFrontShooterRate);
        }
        
        if(middleRate != desiredMiddleShooterRate)
        {
            desiredMiddleShooterRate = middleRate;
            middleShooterPID.setSetpoint(desiredMiddleShooterRate);
        }
        
        if(rearRate != desiredRearShooterRate)
        {
            desiredRearShooterRate = rearRate;
            rearShooterPID.setSetpoint(desiredRearShooterRate);
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
        double ff = params.getAsDouble("S_FrontShooterKV", 0.0);
        double r = params.getAsDouble("S_FrontShooterDoneRange", 300.0);
        
        frontShooterPID.setPIDGains(p, 0.0, 0.0);
        frontShooterPID.setFeedForward(ff);
        frontShooterPID.setDoneRange(r);
        frontShooterPID.setMinDonecycles(0);
        frontShooterPID.reset();
        
        p = params.getAsDouble("S_MiddleShooterP", 0.0);
        ff = params.getAsDouble("S_MiddleShooterKV", 0.0);
        r = params.getAsDouble("S_MiddleShooterDoneRange", 300.0);
        
        middleShooterPID.setPIDGains(p, 0.0, 0.0);
        middleShooterPID.setFeedForward(ff);
        middleShooterPID.setDoneRange(r);
        middleShooterPID.setMinDonecycles(0);
        middleShooterPID.reset();
        
        p = params.getAsDouble("S_RearShooterP", 0.0);
        ff = params.getAsDouble("S_RearShooterKV", 0.0);
        r = params.getAsDouble("S_RearShooterDoneRange", 300.0);
        
        rearShooterPID.setPIDGains(p, 0.0, 0.0);
        rearShooterPID.setFeedForward(ff);
        rearShooterPID.setDoneRange(r);
        rearShooterPID.setMinDonecycles(0);
        rearShooterPID.reset();
    }
    
}
