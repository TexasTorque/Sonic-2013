package org.TexasTorque.TorqueLib.sensor;

import edu.wpi.first.wpilibj.AnalogChannel;

public class TorquePotentiometer
{
    private AnalogChannel pot;
    private double maxVoltage;
    private double minVoltage;
    
    private double[] values;
    private final int valuesSize = 4;
    private int valuesIndex = 0;
    
    public TorquePotentiometer(int port)
    {
        pot = new AnalogChannel(port);
        values = new double[valuesSize];
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
        //return 1 - limitValue((pot.getVoltage() - minVoltage) / (maxVoltage - minVoltage));
        
        values[valuesIndex++] = 1 - limitValue((pot.getVoltage() - minVoltage) / (maxVoltage - minVoltage));
        
        if (valuesIndex >= valuesSize) {
            valuesIndex = 0;
        }
        
        double average = 0;
        for (int i = 0; i < valuesSize; i++) {
            average += values[i];
        }
        average /= valuesSize;
        
        return average;
    }
    
    public double getRaw()
    {
        return pot.getVoltage();
    }
    
    private double limitValue(double value)
    {
        if(value > 1.0)
        {
            return 1.0;
        }
        else
        {
            return value;
        }
    }
    
}
