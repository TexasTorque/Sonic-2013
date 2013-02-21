package org.TexasTorque.TorqueLib.util;

import com.sun.squawk.io.BufferedWriter;
import com.sun.squawk.microedition.io.FileConnection;
import java.io.IOException;
import java.io.OutputStreamWriter;
import javax.microedition.io.Connector;

public class TorqueLogging2
{
    
    private static TorqueLogging2 instance;
    
    private FileConnection fileConnection = null;
    private BufferedWriter fileIO = null;
    
    private String fileName = "TorqueLog.csv";
    private String filePath = "file:///ni-rt/startup/";
    private String keyNames;
    private String logString;
    
    public static TorqueLogging2 getInstance()
    {
        return (instance == null) ? instance = new TorqueLogging2() : instance;
    }
    
    private TorqueLogging2()
    {
        try
        {
            fileConnection = (FileConnection) Connector.open(filePath + fileName);
            if(!fileConnection.exists())
            {
               fileConnection.create();
            }
            fileIO = new BufferedWriter(new OutputStreamWriter(fileConnection.openOutputStream()));
        }
        catch(IOException e)
        {
            System.err.println("Error creating file in TorqueLogging.");
        }
        
        keyNames = null;
        logString = null;
    }
    
    public void logKeyNames(String names)
    {
        keyNames = names;
        log(keyNames);
    }
    
    public void logData(String data)
    {
        logString = data;
        log(logString);
    }
    
    private void log(String str)
    {
        try
        {
            fileIO.write(str);
            fileIO.newLine();
            fileIO.flush();
        }
        catch(IOException e)
        {
            System.err.println("Error logging some data.");
        } 
    }
    
}
