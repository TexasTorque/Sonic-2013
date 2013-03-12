package org.TexasTorque.TorqueLib.sensor;

import edu.wpi.first.wpilibj.CounterBase;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Timer;

public class TorqueEncoder
{
    private Encoder encoder;
    
    private double previousTime;
    private double prevoiusPosition;
    private double previousRate;
    
    private double rate;
    private double acceleration;
    
    public TorqueEncoder(int aSlot, int aChannel, int bSlot, int bChannel, boolean reverseDirection)
    {
        encoder = new Encoder(aSlot, aChannel, bSlot, bChannel, reverseDirection);
    }
    
    public TorqueEncoder(int aSlot, int aChannel, int bSlot, int bChannel, boolean reverseDireciton, CounterBase.EncodingType encodingType)
    {
        encoder = new Encoder(aSlot, aChannel, bSlot, bChannel, reverseDireciton, encodingType);
    }
    
    public void start()
    {
        encoder.start();
    }
    
    public void reset()
    {
        encoder.reset();
    }
    
    public void calc()
    {
        double currentTime = Timer.getFPGATimestamp();
        double currentPosition = encoder.get();
        
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
        return encoder.get();
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
