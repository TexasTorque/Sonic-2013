package org.TexasTorque.TexasTorque2013.subsystem.manipulator;

import org.TexasTorque.TexasTorque2013.TorqueSubsystem;
import org.TexasTorque.TexasTorque2013.constants.Constants;
import org.TexasTorque.TorqueLib.controlLoop.SimPID;

public class Shooter extends TorqueSubsystem
{   
    private static Shooter instance;
    
    private SimPID frontShooterPID;
    private SimPID rearShooterPID;
    
    private double frontShooterMotorSpeed;
    private double rearShooterMotorSpeed;
    private double desiredFrontShooterRate;
    private double desiredRearShooterRate;
    
    public double tiltMotorSpeed;
    private double desiredTiltPosition;
    private boolean inInnerZone;
    
    private double innerZoneSpeed;
    private double middleZoneSpeed;
    private double outerZoneSpeed;
    private double nullZoneSpeed;
    private double zeroSpeed;
    
    private double innerZoneRange;
    private double middleZoneRange;
    private double outerZoneRange;
    
    public static double tiltOverrideSpeed;
    public static double standardTiltPosition;
    public static double frontShooterOverrideSpeed;
    public static double rearShooterOverrideSpeed;
    public static double frontShooterRate;
    public static double rearShooterRate;
    public static double shootHighStandardAngle;
    public static double shootLowStandardAngle;
    public static double feederStationAngle;
    public static double shootLowAdditive;
    
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
        rearShooterMotorSpeed = Constants.MOTOR_STOPPED;
        desiredFrontShooterRate = Constants.SHOOTER_STOPPED_RATE;
        desiredRearShooterRate = Constants.SHOOTER_STOPPED_RATE;
        
        tiltMotorSpeed = Constants.MOTOR_STOPPED;
        desiredTiltPosition = 0.0;
        inInnerZone = false;
        
        innerZoneSpeed = Constants.MOTOR_STOPPED;
        middleZoneSpeed = Constants.MOTOR_STOPPED;
        outerZoneSpeed = Constants.MOTOR_STOPPED;
        nullZoneSpeed = Constants.MOTOR_STOPPED;
        
        innerZoneRange = 0.0;
        middleZoneRange = 0.0;
        outerZoneRange = 0.0;
    }
    
    public void run()
    {   
        double frontSpeed = frontShooterPID.calcPID(sensorInput.getFrontShooterRate());
        double rearSpeed = rearShooterPID.calcPID(sensorInput.getRearShooterRate());
        
        frontShooterMotorSpeed = limitShooterSpeed(frontSpeed);
        rearShooterMotorSpeed = limitShooterSpeed(rearSpeed);
        
        tiltMotorSpeed = scheduledTiltSpeed();
        
        robotOutput.setTiltMotor(tiltMotorSpeed);
        robotOutput.setShooterMotors(frontShooterMotorSpeed, rearShooterMotorSpeed);
    }
    
    private double scheduledTiltSpeed()
    {
        double motorSpeed = 0.0;
        double currentAngle = sensorInput.getTiltAngle();
        double dAngle = Math.abs(desiredTiltPosition - currentAngle);
        
        if(dAngle <= innerZoneRange)
        {
            motorSpeed = innerZoneSpeed;
            inInnerZone = true;
        }
        else if(dAngle <= middleZoneRange)
        {
            motorSpeed = (desiredTiltPosition > currentAngle) ? middleZoneSpeed : -1 * middleZoneSpeed;
            inInnerZone = false;
        }
        else if(dAngle <= outerZoneRange)
        {
            motorSpeed = (desiredTiltPosition  > currentAngle) ? outerZoneSpeed : -1 * outerZoneSpeed;
            inInnerZone = false;
        }
        else
        {
            motorSpeed = (desiredTiltPosition > currentAngle) ? nullZoneSpeed : -1 * nullZoneSpeed;
            inInnerZone = false;
        }
        
        return motorSpeed;
    }
    
    public void setShooterRates(double frontRate, double rearRate)
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
    
    public void setTiltAngle(double degrees)
    {
        if(degrees != desiredTiltPosition)
        {
            desiredTiltPosition = degrees;
        }
    }
    
    public boolean isVerticallyLocked()
    {
        return inInnerZone;
    }
    
    public boolean isAtStandardPosition()
    {
        return (isVerticallyLocked() && (desiredTiltPosition == Constants.DEFAULT_STANDARD_TILT_POSITION || desiredTiltPosition == 0.0));
    }
    
    public boolean isReadyToFire()
    {
        return (isVerticallyLocked() && isSpunUp());
    }
    
    public boolean isSpunUp()
    {
        return (frontShooterPID.isDone());
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
        String names = "DesiredTiltAngle,TiltMotorSpeed,ActualTiltAngle,"
                + "DesiredFrontShooterRate,FrontShooterMotorSpeed,ActualFrontShooterRate,"
                + "DesiredRearShooterRate,RearShooterMotorSpeed,ActualRearShooterRate";
        
        return names;
    }
    
    public String logData()
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
    
    public void loadParameters()
    {
        tiltOverrideSpeed = params.getAsDouble("S_TiltOverrideSpeed", 0.5);
        standardTiltPosition = params.getAsDouble("S_TiltStandardAngle", Constants.DEFAULT_STANDARD_TILT_POSITION);
        frontShooterOverrideSpeed = params.getAsDouble("S_FrontShooterOverrideSpeed", 0.7);
        rearShooterOverrideSpeed = params.getAsDouble("S_RearShooterOverrideSpeed", 0.5);
        frontShooterRate = params.getAsDouble("S_FrontShooterRate", Constants.DEFAULT_FRONT_SHOOTER_RATE);
        rearShooterRate = params.getAsDouble("S_RearShooterRate", Constants.DEFAULT_REAR_SHOOTER_RATE);
        shootHighStandardAngle = params.getAsDouble("S_ShootHighAngle", Constants.DEFAULT_STANDARD_TILT_POSITION);
        shootLowStandardAngle = params.getAsDouble("S_ShootLowAngle", Constants.DEFAULT_STANDARD_TILT_POSITION);
        feederStationAngle = params.getAsDouble("S_FeederStationAngle", Constants.DEFAULT_STANDARD_TILT_POSITION);
        shootLowAdditive = params.getAsDouble("S_ShootLowAdditive", 0.0);
        
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
        
        innerZoneSpeed = params.getAsDouble("S_InnerZoneSpeed", 0.0);
        middleZoneSpeed = params.getAsDouble("S_MiddleZoneSpeed", 0.0);
        outerZoneSpeed = params.getAsDouble("S_OuterZoneSpeed", 0.0);
        nullZoneSpeed = params.getAsDouble("S_NullZoneSpeed", 0.0);
        zeroSpeed = params.getAsDouble("S_ZeroSpeed", -0.1);
        
        innerZoneRange = params.getAsDouble("S_InnerZoneRange", 0.0);
        middleZoneRange = params.getAsDouble("S_MiddleZoneRange", 0.0);
        outerZoneRange = params.getAsDouble("S_OuterZoneRange", 0.0);
    }
    
}
