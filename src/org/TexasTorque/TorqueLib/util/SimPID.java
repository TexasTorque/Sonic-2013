package org.TexasTorque.TorqueLib.util;

public class SimPID
{
    private double p;
    private double i;
    private double d;
    private int desiredValue;
    private int errorSum;
    private int errorIncrement;
    private int errorEpsilon;
    private boolean firstCycle;
    private double maxOutput;
    private int minCycleCount;
    private int cycleCount;
    private int previousValue;
    
    public SimPID(double pValue, double iValue, double dValue, int epsilon)
    {
        p = pValue;
        i = iValue;
        d = dValue;
        errorEpsilon = epsilon;
        desiredValue = 0;
        firstCycle = true;
        maxOutput = 1.0;
        errorIncrement = 1;
        cycleCount = 0;
        minCycleCount = 0;
    }
    
    public void setConstants(double pValue, double iValue, double dValue)
    {
        p = limitValue(pValue);
        i = limitValue(iValue);
        d = limitValue(dValue);
    }
    
    public void setErrorEpsilon(int epsilon)
    {
        errorEpsilon = (int)limitValue(epsilon);
    }
    
    public void setErrorIncrement(int inc)
    {
        errorIncrement = inc;
    }
    public void setDesiredValue(int val)
    {
        desiredValue = val;
    }
    
    public void setMaxOutput(float max)
    {
        if(max >= 0.0 && max <= 1.0)
        {
            maxOutput = max;
        }
    }
    
    public void resetErrorSum()
    {
        errorSum = 0;
    }
    
    public double calcPID(int currentValue)
    {
        double pVal = 0.0;
        double iVal = 0.0;
        double dVal = 0.0;
        if(firstCycle)
        {
            previousValue = currentValue;
            firstCycle = false;
        }
        int error = desiredValue - currentValue;
        pVal = p * (double)error;
        if(error >= errorEpsilon)
        {
            if(errorSum < 0)
            {
                errorSum = 0;
            }
            if(error < errorIncrement)
            {
                errorSum += error;
            }
            else
            {
                errorSum += errorIncrement;
            }
        }
        else if(error <= -errorEpsilon)
        {
            if(errorSum > 0)
            {
                errorSum = 0;
            }
            if(error > -errorIncrement)
            {
                errorSum += error;
            }
            else
            {
                errorSum -= errorIncrement;
            }
        }
        else
        {
            errorSum = 0;
        }
        iVal = i * (double)errorSum;
        int velocity = currentValue - previousValue;
        dVal = d * (double)velocity;
        double output = pVal + iVal - dVal;
        if(output > maxOutput)
        {
            output = maxOutput;
        }
        else if(output < -maxOutput)
        {
            output = -maxOutput;
        }
        previousValue = currentValue;
        return output;
    }
    
    public boolean isDone()
    {
        if(previousValue <= desiredValue + errorEpsilon
                && previousValue >= desiredValue - errorEpsilon
                && !firstCycle)
        {
            if(cycleCount >= minCycleCount)
            {
                return true;
            }
            else
            {
                cycleCount++;
            }
        }
        cycleCount = 0;
        return false;
    }
    
    public void setMinDoneCycles(int n)
    {
        minCycleCount = n;
    }
    
    private double limitValue(double value)
    {
        if(value < 0.0)
        {
            return 0.0;
        }
        return value;
    }
 
}
