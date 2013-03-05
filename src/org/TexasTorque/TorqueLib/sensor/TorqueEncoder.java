package org.TexasTorque.TorqueLib.sensor;

import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Watchdog;

public class TorqueEncoder extends Thread
{
    private Watchdog watchdog;
    
    public Encoder encoder;
    
    private double currentRate;
    private double threadPeriod;
    private boolean resetData;
    private int current;
    private int previous;
    private double[] rateArray;
    private double[] accArray;
    private int arrayIndex;
    private double currentAcceleration;
    private double currentVel;
    private double previousVel;
    private final int filterSize = 1;
    
    public TorqueEncoder(int aSlot, int aChannel, int bSlot, int bChannel, boolean reverseDirection)
    {
        watchdog = Watchdog.getInstance();
        
        encoder = new Encoder(aSlot, aChannel, bSlot, bChannel, reverseDirection);
        
        initEncoder();
    }
    
    public TorqueEncoder(int aSlot, int aChannel, int bSlot, int bChannel, boolean reverseDireciton, EncodingType encodingType)
    {
        watchdog = Watchdog.getInstance();
        
        encoder = new Encoder(aSlot, aChannel, bSlot, bChannel, reverseDireciton, encodingType);
        
        initEncoder();
    }
    
    public void setOptions(double period, boolean reset)
    {
        setThreadPeriod(period);
        setResetData(reset);
    }
    
    public void setThreadPeriod(double period)
    {
        threadPeriod = period;
    }
    
    public void setResetData(boolean reset)
    {
        resetData = reset;
    }
    
    private void initEncoder()
    {
        currentRate = 0.0;
        threadPeriod = 20;
        current = 0;
        previous = 0;
        arrayIndex = 0;
        resetData = false;
        currentAcceleration = 0.0;
        currentVel = 0.0;
        previousVel = 0.0;
        rateArray = new double[filterSize];
        accArray = new double[filterSize];
    }
    
    public void run()
    {
        encoder.reset();
        encoder.start();
        while(true)
        {
            watchdog.feed();
            if(resetData)
            {
                encoder.reset();
            }
            double initialTime = Timer.getFPGATimestamp();
            previous = encoder.get();
            previousVel = currentRate;
            Timer.delay(threadPeriod / 1000);
            current = encoder.get();
            double finalTime = Timer.getFPGATimestamp();
            rateArray[arrayIndex] = ((current - previous)) / ((finalTime - initialTime));
            calcRate();
            currentVel = currentRate;
            accArray[arrayIndex] = (currentVel - previousVel) / (finalTime - initialTime);
            calcAcceleration();
            arrayIndex++;
            if(arrayIndex == filterSize)
            {
                arrayIndex = 0;
            }
        }
    }
    
    private void calcAcceleration()
    {
        double rateSum = 0.0;
        for(int i = 0; i < filterSize; i++)
        {
            rateSum += accArray[i];
        }
        currentAcceleration = rateSum / filterSize;
    }
    
    private void calcRate()
    {
        double rateSum = 0.0;
        for(int i = 0; i < filterSize; i++)
        {
            rateSum += rateArray[i];
        }
        currentRate = rateSum / filterSize;
    }
    
    public int get()
    {
        return encoder.get();
    }
    
    public double getRate()
    {
       return currentRate; 
    }
    
    public double getAcceleration()
    {
        return currentAcceleration;
    }
    
    public void resetEncoder()
    {
        encoder.reset();
    }
    
}
