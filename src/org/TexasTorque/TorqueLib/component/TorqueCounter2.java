package org.TexasTorque.TorqueLib.component;

import edu.wpi.first.wpilibj.Counter;
import edu.wpi.first.wpilibj.CounterBase;
import edu.wpi.first.wpilibj.DigitalSource;
import org.TexasTorque.TorqueLib.util.MovingAverageFilter;

public class TorqueCounter2
{
    private Counter counter;
    
    private MovingAverageFilter filter;
    
    public TorqueCounter2(int sidecar, int port)
    {
        counter = new Counter(sidecar, port);
        
        filter = new MovingAverageFilter(1);
    }
    
    public TorqueCounter2(CounterBase.EncodingType encodingType, DigitalSource upSource, DigitalSource downSource, boolean reverse)
    {
        counter = new Counter(encodingType, upSource, downSource, reverse);
        
        filter = new MovingAverageFilter(1);
    }
    
    public void start()
    {
        counter.start();
    }
    
    public void reset()
    {
        counter.reset();
    }
    
    public int get()
    {
        return counter.get();
    }
    
    public double getRate()
    {
        return 1.0 / counter.getPeriod();
    }
}
