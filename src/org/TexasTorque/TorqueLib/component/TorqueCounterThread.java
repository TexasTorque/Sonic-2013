package org.TexasTorque.TorqueLib.component;

import edu.wpi.first.wpilibj.Counter;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.DigitalSource;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Watchdog;

public class TorqueCounterThread extends Thread
{
    private Watchdog watchdog;
    
    private Counter counter;
    
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
    private final int filterSize = 10;
    
    public TorqueCounterThread(int sidecar, int port)
    {
        watchdog = Watchdog.getInstance();
        
        counter = new Counter(sidecar, port);
        
        initCounter();
    }
    
    public TorqueCounterThread(EncodingType encodingType, DigitalSource upSource, DigitalSource downSource, boolean reverse)
    {
        watchdog = Watchdog.getInstance();
        
        counter = new Counter(encodingType, upSource, downSource, reverse);
        
        initCounter();
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
    
    private void initCounter()
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
        counter.reset();
        counter.start();
        while(true)
        {
            watchdog.feed();
            if(resetData)
            {
                counter.reset();
            }
            double initialTime = Timer.getFPGATimestamp();
            previous = counter.get();
            previousVel = currentRate;
            Timer.delay(threadPeriod / 1000);
            current = counter.get();
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
        return counter.get();
    }
    
    public double getRate()
    {
       return currentRate; 
    }
    
    public double getAcceleration()
    {
        return currentAcceleration;
    }
    
    public void resetCounter()
    {
        counter.reset();
    }
}
