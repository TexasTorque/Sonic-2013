package org.TexasTorque.TorqueLib.util;

import com.sun.squawk.io.BufferedWriter;
import com.sun.squawk.microedition.io.*;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Watchdog;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Hashtable;
import javax.microedition.io.*;

public class TorqueLogging extends Thread
{
    private static TorqueLogging instance;
    private Watchdog watchdog;
    private FileConnection fileConnection = null;
    private BufferedWriter fileIO = null;
    private static String fileName = "TorqueLog.csv";
    private String filePath = "file:///ni-rt/startup/";
    private static boolean logToDashboard = false;
    private static long threadLoopTime = 2;
    private Hashtable table;
    private String keys;
    private String values;
    private int numLines;
    private boolean logData;
    
    public static void setFileName(String fileNm)
    {
        fileName = fileNm;
    }
    
    public static void setDashboardLogging(boolean log)
    {
        logToDashboard = log;
    }
    
    public static void setLoopTime(int loopTime)
    {
        threadLoopTime = loopTime;
    }
    
    public synchronized static TorqueLogging getInstance()
    {
        return (instance == null) ? instance = new TorqueLogging() : instance;
    }
    
    public TorqueLogging()
    {
        watchdog = Watchdog.getInstance();
        try
        {
            fileConnection = (FileConnection) Connector.open(filePath + fileName);
            if(!fileConnection.exists())
            {
               fileConnection.create();
               fileIO = new BufferedWriter(new OutputStreamWriter(fileConnection.openOutputStream()));
            }
            else
            {
                fileIO = new BufferedWriter(new OutputStreamWriter(fileConnection.openOutputStream()));
            }
        }
        catch(IOException e)
        {
            System.err.println("Error creating file in TorqueLogging.");
        }
        table = new Hashtable();
        keys = "FrameNumber,";
        values = "";
        numLines = 1;
        table.put("FrameNumber", "" + numLines);
    }
    
    public void setLogging(boolean log)
    {
        logData = log;
    }
    
    public void startLogging()
    {
        this.start();
    }
    
    public void init()
    {
        if(logToDashboard)
        {
            SmartDashboard.putString("TorqueLog", keys);
        }
        else
        {
             writeKeysToFile();
        }
    }
    
    public void run()
    {
        init();
        DriverStation ds = DriverStation.getInstance();
        while(true)
        {
            watchdog.feed();
            while(ds.isDisabled() || !logData)
            {
                watchdog.feed();
            }
            
            calculateValueString();
            if(logToDashboard)
            {
                SmartDashboard.putString("TorqueLog", values);
            }
            else
            {
                writeValuesToFile();
            }
            this.logValue("FrameNumber", numLines++);
            try
            {
                Thread.sleep(threadLoopTime);
                try
                {
                    fileIO.flush();
                }
                catch (IOException ex){}
            }
            catch (InterruptedException ex){}
        }
    }
    
    public synchronized void setKeyMapping(String mapping)
    {
        table.clear();
        if(mapping.charAt(mapping.length() - 1) != ',')
        {
            mapping += ",";
        }
        keys = mapping;
        int start = 0;
        int index = 0;
        while(index != -1)
        {
            watchdog.feed();
            index = keys.indexOf(",", start);
            if(index != -1)
            {
                String keyName = keys.substring(start, index);
                table.put(keyName, "0");
                start = index + 1;
            }
        }
    }
    
    public synchronized void logValue(String name, int value)
    {
        if(table.get(name) == null)
        {
            keys += name + ",";
        }
        table.put(name, "" + value);
        SmartDashboard.putNumber(name, value);
    }
    
    public synchronized void logValue(String name, boolean value)
    {
        if(table.get(name) == null)
        {
            keys += name + ",";
        }
        table.put(name, "" + value);
        SmartDashboard.putBoolean(name, value);
    }
    
    public synchronized void logValue(String name, double value)
    {
        if(table.get(name) == null)
        {
            keys += name + ",";
        }
        table.put(name, "" + value);
        SmartDashboard.putNumber(name, value);
    }
    
    public synchronized void logValue(String name, String value)
    {
        if(table.get(name) == null)
        {
            keys += name + ",";
        }
        table.put(name, value);
        SmartDashboard.putString(name, value);        
    }
    
    private void writeKeysToFile()
    {
        try
        {
            fileIO.write(keys.substring(0, keys.length() - 1));
        }
        catch(IOException e){} 
    }
    
    private void calculateValueString()
    {
        values = "";
        int start = 0;
        int index = 0;
        boolean first = true;
        while(index != -1)
        {
            watchdog.feed();
            index = keys.indexOf(",", start);
            if(index != -1)
            {
                String keyName = keys.substring(start, index);
                if(first)
                {
                    values += table.get(keyName);
                    first = false;
                }
                else
                {
                    values += "," + table.get(keyName);
                }
                start = index + 1;
            }
        }
    }
    
    private void writeValuesToFile()
    {
        try
        {
            fileIO.newLine();
            fileIO.write(values);
        }
        catch(IOException e){} 
    }
  
}
