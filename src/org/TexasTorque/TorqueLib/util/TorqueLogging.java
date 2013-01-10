package org.TexasTorque.TorqueLib.util;

import com.sun.squawk.io.BufferedWriter;
import com.sun.squawk.microedition.io.*;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Hashtable;
import javax.microedition.io.*;

public class TorqueLogging extends Thread
{
    
    private static TorqueLogging instance;
    private FileConnection fileConnection = null;
    private BufferedWriter fileIO = null;
    private static String fileName = "TorqueLog.txt";
    private String filePath = "file:///ni-rt/startup/";
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
        try
        {
            fileConnection = (FileConnection) Connector.open(filePath + fileName);
            fileIO = new BufferedWriter(new OutputStreamWriter(fileConnection.openOutputStream()));
        }
        catch(IOException e){}
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
            keys += name + ",";
            numValues++;
        }
        table.put(name, "" + value);
    }
    
    public synchronized void logValue(String name, boolean value)
    {
        if(table.get(name) == null)
        {
            keys += name + ",";
            numValues++;
        }
        table.put(name, "" + value);
    }
    
    public synchronized void logValue(String name, double value)
    {
        if(table.get(name) == null)
        {
            keys += name + ",";
            numValues++;
        }
        table.put(name, "" + value);
    }
    
    public synchronized void logValue(String name, String value)
    {
        if(table.get(name) == null)
        {
            keys += name + ",";
            numValues++;
        }
        table.put(name, value);
    }
    
    public void writeKeysToFile()
    {
        try
        {
            fileIO.write(keys);
        }
        catch(IOException e){} 
    }
    
    public void calculateValueString()
    {
        int start = 0;
        int index = 0;
        while(index != -1)
        {
            index = keys.indexOf(",", start);
            if(index != -1)
            {
                String keyName = keys.substring(start, index);
                values += (String)table.get(keyName);
                start = index + 1;
            }
        }
    }
    
    public void writeValuesToFile()
    {
        try
        {
            fileIO.write(values);
        }
        catch(IOException e){} 
    }
    
    public void writeToDashboard()
    {
        
    }
    
    
}
