package org.TexasTorque.TexasTorque2013.io;

public class DriverInput
{
    private static DriverInput instance;
    
    public DriverInput()
    {
        
    }
    
    public static DriverInput getInstance()
    {
        return (instance == null) ? instance = new DriverInput() : instance;
    }
}
