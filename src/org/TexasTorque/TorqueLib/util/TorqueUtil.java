package org.TexasTorque.TorqueLib.util;

public class TorqueUtil
{
    public static double convertToRMP(double unitsPerSecond, double unitsPerRevolution)
    {
        return (unitsPerSecond * 60) / unitsPerRevolution;
    }
    
    public static double applyDeadband(double value, double deadband)
    {
        if(Math.abs(value) <= deadband)
        {
            return 0.0;
        }
        else
        {
            return value;
        }
    }
    
    public static double sqrtHoldSign(double val)
    {
        int sign = (val > 0) ? 1 : -1;
        val = Math.sqrt(Math.abs(val)) * sign;
        return val;
    }
}
