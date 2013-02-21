package org.TexasTorque.TorqueLib.util;

import com.sun.squawk.io.BufferedWriter;
import com.sun.squawk.microedition.io.FileConnection;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import java.io.IOException;
import java.io.OutputStreamWriter;
import javax.microedition.io.Connector;

public class TorqueLogging
{
    
    private static TorqueLogging instance;
    
    private FileConnection fileConnection = null;
    private BufferedWriter fileIO = null;
    
    private String fileName = "TorqueLog.csv";
    private String filePath = "file:///ni-rt/startup/";
    private String keyNames;
    private String logString;
    
    private boolean logToDashboard;
    
    public static TorqueLogging getInstance()
    {
        return (instance == null) ? instance = new TorqueLogging() : instance;
    }
    
    private TorqueLogging()
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
        logToDashboard = false;
    }
    
    public void setDashboardLogging(boolean dashLog)
    {
        logToDashboard = dashLog;
    }
    
    public void createNewFile()
    {
        try
        {
            if(fileConnection.exists())
            {
                fileConnection.delete();
            }
            instance = new TorqueLogging();
        }
        catch(IOException e)
        {
            System.err.println("Error trying to create a new file in createNewFile() method");
        }
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
        if(logToDashboard)
        {
            SmartDashboard.putString("TorqueLogging", str);
        }
        else
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
    
}
