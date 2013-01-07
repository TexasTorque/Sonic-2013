package org.TexasTorque.TorqueLib.util;

import java.util.Enumeration;
import java.util.Hashtable;

public class TorqueLogging extends Thread
{
    
    private static TorqueLogging instance;
    private Hashtable logTable;
    private static String fileName = "TorqueLog.txt";
    private static boolean logToDashboard = false;
    private static boolean writeNow = false;
    
    public static void setFileName(String fileNm)
    {
        fileName = fileNm;
    }
    
    public static void setDashboardLogging(boolean log)
    {
        logToDashboard = log;
    }
    
    public static TorqueLogging getInstance()
    {
        return (instance == null) ? instance = new TorqueLogging() : instance;
    }
    
    public TorqueLogging()
    {
        logTable = new Hashtable();
        
    }
    
    public void start()
    {
        
    }
    
    public void run()
    {
        
    }
    
    public void writeKeys()
    {
        if(!logTable.isEmpty())
        {
            String logLine = "";
            Enumeration keys = logTable.keys();
            while(keys.hasMoreElements())
            {
                String key = (String)keys.nextElement();
                Object value = logTable.get(key);
                
            }
        }
    }
    
    public void writeValues()
    {
        if(!logTable.isEmpty())
        {
            String logLine = "";
            Enumeration keys = logTable.keys();
            while(keys.hasMoreElements())
            {
                String key = (String)keys.nextElement();
                Object value = logTable.get(key);
                
            }
        }
    }
    
    public void writeToDashboard()
    {
        
    }
    
    
}
