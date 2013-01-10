package org.TexasTorque.TorqueLib.util;

import java.util.Hashtable;

public class TorqueLogging extends Thread
{
    
    private static TorqueLogging instance;
    private static String fileName = "TorqueLog.txt";
    private static boolean logToDashboard = false;
    private Hashtable table;
    private String keys;
    private String values;
    private int numValues;
    
    public static void setFileName(String fileNm)
    {
        fileName = fileNm;
    }
    
    public static void setDashboardLogging(boolean log)
    {
        logToDashboard = log;
    }
    
    public synchronized static TorqueLogging getInstance()
    {
        return (instance == null) ? instance = new TorqueLogging() : instance;
    }
    
    public TorqueLogging()
    {
        table = new Hashtable();
        numValues = 0;
    }
    
    public void start()
    {
        
    }
    
    public void run()
    {
        
    }
    
    public synchronized void logValue(String name, int value)
    {
        if(table.get(name) == null)
        {
            keys += name;
            numValues++;
        }
        table.put(name, "" + value);
    }
    
    public synchronized void logValue(String name, boolean value)
    {
        if(table.get(name) == null)
        {
            keys += name;
            numValues++;
        }
        table.put(name, "" + value);
    }
    
    public synchronized void logValue(String name, double value)
    {
        if(table.get(name) == null)
        {
            keys += name;
            numValues++;
        }
        table.put(name, "" + value);
    }
    
    public synchronized void logValue(String name, String value)
    {
        if(table.get(name) == null)
        {
            keys += name;
            numValues++;
        }
        table.put(name, value);
    }
    
    public void writeKeysToFile()
    {
        
    }
    
    public void calculateValueString()
    {
    }
    
    public void writeValuesToFile()
    {
        
    }
    
    public void writeToDashboard()
    {
        
    }
    
    
}
