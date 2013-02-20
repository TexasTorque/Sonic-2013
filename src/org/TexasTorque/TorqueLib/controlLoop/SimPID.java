package org.TexasTorque.TorqueLib.controlLoop;

public class SimPID
{
    private double pConst;
    private double iConst;
    private double dConst;
    
    private double desiredVal;
    private double previousVal;
    private double errorSum;
    private double errorIncrement;
    private double errorEpsilon;
    private double doneRange;
    
    private boolean firstCycle;
    private double maxOutput;
    private int minCycleCount;
    private int cycleCount;
    
    public SimPID()
    {
        this(0.0, 0.0, 0.0, 0.0);
    }
    
    public SimPID(double p, double i, double d, double eps)
    {
        this.pConst = p;
        this.iConst = i;
        this.dConst = d;
        this.errorEpsilon = eps;
        this.doneRange = eps;
        
        this.desiredVal = 0.0;
        this.firstCycle = true;
        this.maxOutput = 1.0;
        this.errorIncrement = 1.0;
        
        this.cycleCount = 0;
        this.minCycleCount = 0;
    }
    
    public SimPID(double p, double i, double d)
    {
        this(p, i, d, 1.0);
    }
    
    public void setConstants (double p, double i, double d)
    {
        this.pConst = p;
        this.iConst = i;
        this.dConst = d;
    }
    
    public void setDoneRange(double range)
    {
        this.doneRange = range;
    }
    
    public void setErrorEpsilon (double eps)
    {
        this.errorEpsilon = eps;
    }
    
    public void setDesiredValue(double val)
    {
        this.desiredVal = val;
    }
    
    public void setMaxOutput(double max)
    {
        if(max < 0.0)
        {
            this.maxOutput = 0.0;
        }
        else if(max > 1.0)
        {
            this.maxOutput = 1.0;
        }
        else
        {
            this.maxOutput = max;
        }
    }
    
    public void setMinDoneCycles(int num)
    {
        this.minCycleCount = num;
    }
    
    public void resetErrorSum()
    {
        this.errorSum = 0.0;
    }
    
    public double getDesiredVal()
    {
        return desiredVal;
    }
    
    public double getPreviousVal()
    {
        return previousVal;
    }
    
    public double calcPID(double currentVal)
    {
        double pVal = 0.0;
        double iVal = 0.0;
        double dVal = 0.0;
        
        if(this.firstCycle)
        {
            this.previousVal = currentVal;
            this.firstCycle = false;
        }
        
        ////////////// P Calculation //////////////
        double error = this.desiredVal - currentVal;
        pVal = this.pConst * error;
        
        ///////////// I Calculation  ////////////////
        
        //positive error outside of acceptable range
        if (error > this.errorEpsilon)
        {
            //Check if error sum was in the wrong direction
            if (this.errorSum < 0.0)
            {
                this.errorSum = 0.0;
            }
            //only allow up to the max contribution per cycle
            this.errorSum += Math.min(error, this.errorIncrement);
        }
        // error is negative and outside the epsilon range
        else if(error < -1.0 * this.errorEpsilon)
        {
            // error sum was in the wrong direction
            if (this.errorSum > 0.0)
            {
                this.errorSum = 0.0;
            }
            // add either the full error or the max allowwable amount to sum
            this.errorSum += Math.max(error, -1.0 * this.errorIncrement);
        }
        //within the allowable epsilon
        else
        {
            //reset the error sum
            this.errorSum = 0.0;
        }
        
        //i contribution (final) calculation
        iVal = this.iConst * this.errorSum;
        
        /////////////// D Calculation ////////////////
        double deriv = currentVal - this.previousVal;
        dVal = this.dConst * deriv;
        
        //overall PID calculation
        double output = pVal + iVal - dVal;
        
        //limit the output
        //output = SimLib.limitValue(output, this.maxOutput);
        if(output > maxOutput)
        {
            output = maxOutput;
        }
        else if(output < -maxOutput)
        {
            output = -maxOutput;
        }
        
        //store current value as previous for next cycle
        this.previousVal = currentVal;
        
        return output;
        
    }
    
    public boolean isDone()
    {
        double currError = Math.abs(this.desiredVal - this.previousVal);
        
        // close enough to target
        if(currError <= this.doneRange)
        {
            this.cycleCount ++;
        }
        // not close enough to target
        else
        {
            this.cycleCount = 0;
        }
        
        return this.cycleCount > this.minCycleCount;
    }
    
    public void resetPreviousVal()
    {
        this.firstCycle = true;
    }
}

/*public class SimPID
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
    
    public SimPID(double pValue, double iValue, double dValue, double epsilon)
    {
        p = pValue;
        i = iValue;
        d = dValue;
        errorEpsilon = (int)(epsilon * 1000);
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
    
    public void setErrorEpsilon(double epsilon)
    {
        errorEpsilon = (int)limitValue(epsilon * 1000);
    }
    
    public void setErrorIncrement(int inc)
    {
        errorIncrement = inc;
    }
    
    public void setDesiredValue(int val)
    {
        desiredValue = val;
    }
    
    public void setDesiredValue(double val)
    {
        setDesiredValue((int)(val * 1000));
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
    
    public double calcPID(double currentValue)
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
 
}*/
