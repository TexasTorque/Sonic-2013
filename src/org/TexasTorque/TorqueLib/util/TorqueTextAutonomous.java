package org.TexasTorque.TorqueLib.util;

import com.sun.squawk.io.BufferedReader;
import com.sun.squawk.microedition.io.*;
import edu.wpi.first.wpilibj.IterativeRobot;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.microedition.io.*;

public class TorqueTextAutonomous
{
    private String fileName;
    private String filePath;
    private FileConnection fileConnection = null;
    private BufferedReader fileIO = null;
    
    public TorqueTextAutonomous(String fn)
    {
        fileName = fn;
        filePath = "file:///ni-rt/startup/";
    }
    
    public void load() throws IOException
    {
        fileConnection = (FileConnection)Connector.open(filePath + fileName);
        if(fileConnection.exists())
        {
             fileIO = new BufferedReader(new InputStreamReader(fileConnection.openInputStream())); 
             String line;
             while((line = fileIO.readLine()) != null)
             {
             }
        }
        
    }
    
    
}
