package org.TexasTorque.TorqueLib.util;

import com.sun.squawk.io.BufferedReader;
import com.sun.squawk.microedition.io.*;
import edu.wpi.first.wpilibj.Watchdog;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;
import javax.microedition.io.*;

public class Parameters
{
    private static Parameters teleopInstance;
    private static Parameters autonInstance;
    private static Parameters visionInstance;
    private Watchdog watchdog;
    private Hashtable map;
    private String fileName;
    private String filePath;
    private FileConnection fileConnection = null;
    private BufferedReader fileIO = null;
    
    public synchronized static Parameters getTeleopInstance()
    {
        return (teleopInstance == null) ? teleopInstance = new Parameters("params.txt") : teleopInstance;
    }
    
    public synchronized static Parameters getAutonInstance()
    {
        return (autonInstance == null) ? autonInstance = new Parameters("autonParams.txt") : autonInstance;
    }
    
    public synchronized static Parameters getVisionInstance()
    {
        return (visionInstance == null) ? visionInstance = new Parameters("params.txt", "C:/Users/Texas Torque/Desktop/Params/") : visionInstance;
    }
  
    public Parameters(String fileNm)
    {
        watchdog = Watchdog.getInstance();
        map = new Hashtable();
        filePath = "file:///ni-rt/startup/";
        fileName = fileNm;
    }
    public Parameters(String fileNm, String path)
    {
        watchdog = Watchdog.getInstance();
        map = new Hashtable();
        filePath = path;
        fileName = fileNm;
    }
    
    public void load()
    {
        try
        {
            clearList();
            fileConnection = (FileConnection)Connector.open(filePath + fileName);
            if(fileConnection.exists())
            {
                 fileIO = new BufferedReader(new InputStreamReader(fileConnection.openInputStream()));
                 String line;
                 int index;
                 while((line = fileIO.readLine()) != null)
                 {
                     watchdog.feed();
                     map.put(line.substring(0, index = line.indexOf(" ")), line.substring(index + 1));
                 }
                 System.err.println("Parameters file: " + fileName + " successfully loaded.");
                 fileConnection.close();
            }
            else
            {
                System.err.println("Could not load parameters file: " + fileName);
            }
        }
        catch(IOException e)
        {
            System.err.println("IOException caught trying to read in parameters file.");
        }
    }
    
    public synchronized int getAsInt(String name, int dflt)
    {
        String value = (String)map.get(name);
        return (value == null) ? dflt : (int)Double.parseDouble(value);
    }
    
    public synchronized double getAsDouble(String name, double dflt)
    {
        String value = (String)map.get(name);
        return (value == null) ? dflt : Double.parseDouble(value);
    }
    
    public synchronized boolean getAsBoolean(String name, boolean dflt)
    {
        String value = (String)map.get(name);
        return (value == null) ? dflt : Integer.parseInt(value) != 0;
    }
    
    private void clearList()
    {
        map.clear();
    }
}
