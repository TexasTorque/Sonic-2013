package org.TexasTorque.TexasTorque2013.io;

import org.TexasTorque.TexasTorque2013.constants.Ports;

public class SensorInput
{
    private static SensorInput instance;
    
    public SensorInput()
    {
        
    }
    
    public static SensorInput getInstance()
    {
        return (instance == null) ? instance = new SensorInput() : instance;
    }
}
