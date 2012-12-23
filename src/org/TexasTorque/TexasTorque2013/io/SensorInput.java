package org.TexasTorque.TexasTorque2013.io;

import edu.wpi.first.wpilibj.Encoder;
import org.TexasTorque.TexasTorque2013.constants.Ports;

public class SensorInput
{
    private static SensorInput instance;
    
    public SensorInput()
    {
    }
    
    public synchronized static SensorInput getInstance()
    {
        return (instance == null) ? instance = new SensorInput() : instance;
    }
}
