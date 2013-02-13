package org.TexasTorque.TorqueLib.motionProfile;

public abstract class AccelFilterBase
{
    
    protected double currentPosition;
    protected double currentVelocity;
    protected double currentAcceleration;
    
    public AccelFilterBase(double currPos, double currVel, double currAcc)
    {
        currentPosition = currPos;
        currentVelocity = currVel;
        currentAcceleration = currAcc;
    }
    
    public double getCurrPos()
    {
        return currentPosition;
    }
    
    public double getCurrVel()
    {
        return currentVelocity;
    }
    
    public double getCurrAcc()
    {
        return currentAcceleration;
    }
    
    public abstract void calcSystem(double distanceToTarget, double v, double goalV, double maxA, double maxV, double dt);
    
    protected void updateVals(double acc, double dt)
    {
        currentAcceleration = acc;
        currentVelocity += currentAcceleration;
        currentPosition += currentVelocity * dt;
        currentAcceleration /= dt;
    }
    
}