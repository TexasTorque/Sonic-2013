package org.TexasTorque.TorqueLib.util;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Watchdog;
import org.TexasTorque.TorqueLib.sensor.TorqueEncoder;

public class TorqueTrapezoidal extends Thread
{
    
    private Watchdog watchdog;
    private SimPID pid;
    private TorqueEncoder encoder;
    
    private final double THREAD_TIME = 20;
    private final int ACCELERATION_STATE = 0;
    private final int CONSTANT_STATE = 1;
    private final int DECELERATION_STATE = 2;
    private final int WAITING_STATE = 3;
    
    private double positionalEpsilon;
    private double maxVelocity; 
    private double maxAcceleration;
    private double velocityFactor;
    private double accelerationFactor;
    private double currentVelocity;
    private double goalDistance;
    private double calculatedVelocity;
    private double previousVelocity;
    private double motorOutput;
    private int currentState;
    
    public TorqueTrapezoidal(TorqueEncoder enc)
    {
        encoder = enc;
        watchdog = Watchdog.getInstance();
        pid = new SimPID();
        
        positionalEpsilon = 0.0;
        maxVelocity = 0.0;
        maxAcceleration = 0.0;
        velocityFactor = 0.0;
        accelerationFactor = 0.0;
        goalDistance = 0.0;
        calculatedVelocity = 0.0;
        previousVelocity = 0.0;
        motorOutput = 0.0;
        
        currentState = ACCELERATION_STATE;
    }
    
    public void setTrapezoidOptions(double xEpsilon, double maxV, double maxA, double vFactor, double aFactor)
    {
        positionalEpsilon = xEpsilon;
        maxVelocity = maxV;
        maxAcceleration = maxA;
        velocityFactor = vFactor;
        accelerationFactor = aFactor;
    }
    
    public void setPIDOptions(double p, double i, double d, double epsilon)
    {
        pid.setConstants(p, i, d);
        pid.setErrorEpsilon(epsilon);
    }
    
    public void setGoalDistance(double distance)
    {
        goalDistance = distance;
        encoder.resetEncoder();
        currentState = ACCELERATION_STATE;
    }
    
    public synchronized double getMotorOutput()
    {
        return motorOutput;
    }
    
    public synchronized double getGoalOutput()
    {
        return calculatedVelocity;
    }
    
    public synchronized int getState()
    {
        return currentState;
    }
    
    public synchronized boolean getFinished()
    {
        return (currentState == WAITING_STATE);
    }
    
    public void run()
    {
        encoder.resetEncoder();
        previousVelocity = 0.0;
        while(true)
        {
            watchdog.feed();
            
            double initTime = Timer.getFPGATimestamp();
            Timer.delay(THREAD_TIME / 1000);
            double finalTime = Timer.getFPGATimestamp();
            double dt = finalTime - initTime;
            
            currentVelocity = encoder.getRate();
            double encoderPosition = encoder.get();
            double positionalError = goalDistance - encoderPosition;
            
            if(encoderPosition >= goalDistance - positionalEpsilon && encoderPosition <= goalDistance + positionalEpsilon)
            {
                currentState = WAITING_STATE;
            }
            
            if(currentState == ACCELERATION_STATE && currentVelocity < (maxVelocity * velocityFactor))
            {
                calculatedVelocity = accelerationFactor * maxAcceleration * dt + previousVelocity;
            }
            if(currentState != DECELERATION_STATE && currentVelocity >= (maxVelocity * velocityFactor))
            {
                calculatedVelocity = velocityFactor * maxVelocity;
                currentState = CONSTANT_STATE;
            }
            if(currentState != WAITING_STATE && positionalError <= ((currentVelocity * currentVelocity) / (2 * accelerationFactor * maxAcceleration)))
            {
                calculatedVelocity = -1 * accelerationFactor * maxAcceleration * dt + previousVelocity;
                currentState = DECELERATION_STATE;
            }
            
            if(currentState != WAITING_STATE)
            {
                pid.setDesiredValue(calculatedVelocity);
                motorOutput = pid.calcPID(currentVelocity);   
            }
            else
            {
                motorOutput = 0.0;
            }
            
            previousVelocity = currentVelocity;
        }
    }
    
}
