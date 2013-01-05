package org.TexasTorque.TorqueLib.util;

import com.sun.squawk.io.BufferedReader;
import com.sun.squawk.microedition.io.*;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;
import javax.microedition.io.*;

public class Parameters
{
    
    private static Parameters instance;
    private Hashtable map;
    private String fileName;
    private String filePath;
    private FileConnection fileConnection = null;
    private BufferedReader fileIO = null;
    
    public static Parameters getInstance()
    {
        return (instance == null) ? instance = new Parameters("params.txt") : instance;
    }
  
    public Parameters(String fileNm)
    {
        map = new Hashtable();
        filePath = "file:///ni-rt/startup/";
        fileName = fileNm;
    }
    
    public void load() throws IOException
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
                 map.put(line.substring(0, index=line.indexOf(" ")), line.substring(index + 1));
             }
             System.err.println("Parameters file: " + fileName + " sucsessfully loaded.");
             fileConnection.close();
        }
        else
        {
            System.err.println("Could not load parameters file: " + fileName);
        }
    }
    
    public int getAsInt(String name, int dflt)
    {
        String value = (String)map.get(name);
        return (value == null) ? dflt : Integer.parseInt(value);
    }
    
    public double getAsDouble(String name, double dflt)
    {
        String value = (String)map.get(name);
         return (value == null) ? dflt : Double.parseDouble(value);
    }
    
    public boolean getAsBoolean(String name, boolean dflt)
    {
        String value = (String)map.get(name);
        return (value==null)?dflt:Integer.parseInt(value)!=0;
    }
    
    private void clearList()
    {
        map.clear();
    }
}
