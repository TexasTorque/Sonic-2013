package org.TexasTorque.TorqueLib.util;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStationLCD;

public class DashboardManager
{
    private static DashboardManager instance;
    private DriverStation ds;
    private DriverStationLCD dslcd;
    
    public DashboardManager()
    {
        ds = DriverStation.getInstance();
        dslcd = DriverStationLCD.getInstance();
    }
    
    public synchronized static DashboardManager getInstance()
    {
        return (instance == null) ? instance = new DashboardManager() : instance;
    }
    
    public synchronized void printToErr(String output)
    {
        System.err.println(output);
    }
    
    public synchronized void printToErr(int output)
    {
        printToErr("" + output);
    }
    
    public synchronized void printToErr(double output)
    {
        printToErr("" + output);
    }
    
    public synchronized void printToErr(boolean output)
    {
        printToErr("" + output);
    }
    
    public synchronized void printToLCD(int line, String output)
    {
        switch(line)
        {
            case 2:
                dslcd.println(DriverStationLCD.Line.kUser2, 1, output);
                break;
            case 3:
                dslcd.println(DriverStationLCD.Line.kUser3, 1, output);
                break;
            case 4:
                dslcd.println(DriverStationLCD.Line.kUser4, 1, output);
                break;
            case 5:
                dslcd.println(DriverStationLCD.Line.kUser5, 1, output);
                break;
            case 6:
                dslcd.println(DriverStationLCD.Line.kUser6, 1, output);
                break;
            default:
                break;
        }
    }
    
    public synchronized void updateLCD()
    {
        dslcd.updateLCD();
    }
    
    public synchronized DriverStation getDS()
    {
        return ds;
    }
    
    public synchronized DriverStationLCD getDSCLD()
    {
        return dslcd;
    }
    
}
