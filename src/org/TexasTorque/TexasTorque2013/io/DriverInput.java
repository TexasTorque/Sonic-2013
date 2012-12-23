package org.TexasTorque.TexasTorque2013.io;

import org.TexasTorque.TexasTorque2013.constants.Ports;

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
