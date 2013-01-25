package org.TexasTorque.TorqueLib.sensor;

import edu.wpi.first.wpilibj.AnalogChannel;

public class TorquePotentiometer
{
    private AnalogChannel pot;
    private double maxVoltage;
    private double minVoltage;
    
    public TorquePotentiometer(int port)
    {
        pot = new AnalogChannel(port);
    }
    
    public TorquePotentiometer(int sidecar, int port)
    {
        pot = new AnalogChannel(sidecar, port);
    }
    
    public void setRange(double max, double min)
    {
        maxVoltage = max;
        minVoltage = min;
    }
    
    public double get()
    {
        return (pot.getVoltage() - minVoltage) / (maxVoltage - minVoltage);
    }
    
    public double getRaw()
    {
        return pot.getVoltage();
    }
    
}
