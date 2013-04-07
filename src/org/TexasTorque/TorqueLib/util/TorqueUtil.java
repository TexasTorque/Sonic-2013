package org.TexasTorque.TorqueLib.util;

public class TorqueUtil
{
    public static double convertToRMP(double unitsPerSecond, double unitsPerRevolution)
    {
        return (unitsPerSecond * 60) / unitsPerRevolution;
    }
}
