package org.TexasTorque.TorqueLib.sensor;

import edu.wpi.first.wpilibj.AnalogChannel;

public class TorquePotentiometer
{
    private AnalogChannel pot;
    
    public TorquePotentiometer(int port)
    {
        pot = new AnalogChannel(port);
    }
    
    public TorquePotentiometer(int sidecar, int port)
    {
        pot = new AnalogChannel(sidecar, port);
    }
    
    
    
}
