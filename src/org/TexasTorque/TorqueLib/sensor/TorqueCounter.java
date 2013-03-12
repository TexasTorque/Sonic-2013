package org.TexasTorque.TorqueLib.sensor;

import edu.wpi.first.wpilibj.Counter;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.DigitalSource;
import edu.wpi.first.wpilibj.Timer;

public class TorqueCounter
{
    
    private Counter counter;
    
    private double previousTime;
    private double prevoiusPosition;
    private double previousRate;
    
    private double rate;
    private double acceleration;
    
    public TorqueCounter(int sidecar, int port)
    {
        counter = new Counter(sidecar, port);
    }
    
    public TorqueCounter(EncodingType encodingType, DigitalSource upSource, DigitalSource downSource, boolean reverse)
    {
        counter = new Counter(encodingType, upSource, downSource, reverse);
    }
    
    public void start()
    {
        counter.start();
    }
    
    public void reset()
    {
        counter.reset();
    }
    
    public void calc()
    {
        double currentTime = Timer.getFPGATimestamp();
        double currentPosition = counter.get();
        
        double dx = currentPosition - prevoiusPosition;
        double dt = currentTime - previousTime;
        
        rate = dx / dt;
        
        double dr = rate - previousRate;
        
        acceleration = dr / dt;
        
        previousTime = currentTime;
        prevoiusPosition = currentPosition;
        previousRate = rate;
    }
    
    public int get()
    {
        return counter.get();
    }
    
    public double getRate()
    {
        return rate;
    }
    
    public double getAcceleration()
    {
        return acceleration;
    }
}
