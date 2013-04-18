package org.TexasTorque.TorqueLib.component;

import edu.wpi.first.wpilibj.Counter;
import edu.wpi.first.wpilibj.CounterBase;
import edu.wpi.first.wpilibj.DigitalSource;
import org.TexasTorque.TorqueLib.util.MovingAverageFilter;

public class TorqueCounter
{
    private Counter counter;
    
    private MovingAverageFilter filter;
    
    public TorqueCounter(int sidecar, int port)
    {
        counter = new Counter(sidecar, port);
        
        filter = new MovingAverageFilter(1);
    }
    
    public TorqueCounter(CounterBase.EncodingType encodingType, DigitalSource upSource, DigitalSource downSource, boolean reverse)
    {
        counter = new Counter(encodingType, upSource, downSource, reverse);
        
        filter = new MovingAverageFilter(1);
        filter.reset();
    }
    
    public void setFilterSize(int size)
    {
        filter = new MovingAverageFilter(size);
        filter.reset();
    }
    
    public void calc()
    {
        double rate = 1.0 / counter.getPeriod();
        filter.setInput(rate);
        filter.run();
    }
    
    public void start()
    {
        filter.reset();
        counter.start();
    }
    
    public void reset()
    {
        filter.reset();
        counter.reset();
    }
    
    public int get()
    {
        return counter.get();
    }
    
    public double getRate()
    {
        return filter.getAverage();
    }
}
