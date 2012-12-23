package org.TexasTorque.TexasTorque2013.io;

public class SensorInput
{
    public static SensorInput instance;
    
    public SensorInput()
    {
        
    }
    
    public static SensorInput getInstance()
    {
        return (instance == null) ? instance = new SensorInput() : instance;
    }
}
