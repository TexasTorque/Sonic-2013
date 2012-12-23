package org.TexasTorque.TexasTorque2013.io;

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
