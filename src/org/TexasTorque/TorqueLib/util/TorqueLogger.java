package org.TexasTorque.TorqueLib.util;

import com.sun.squawk.io.BufferedWriter;
import com.sun.squawk.microedition.io.*;
import java.io.IOException;
import java.io.OutputStreamWriter;
import javax.microedition.io.*;

public class TorqueLogger
{

    String fileName;
    String filePath;
    FileConnection fileConnection = null;
    BufferedWriter fileIO = null;

    public TorqueLogger(String fileNm) throws IOException
    {
        filePath = "file:///ni-rt/startup/";
        fileName = fileNm;
        fileConnection = (FileConnection) Connector.open(filePath + fileName);
        fileIO = new BufferedWriter(new OutputStreamWriter(fileConnection.openOutputStream()));
    }

    public void logString(String line) throws IOException
    {
        fileIO.write(line + "\n");
    }

    public void logString(String line, String value) throws IOException
    {
        fileIO.write(line + value);
    }

    /**
     * Will log all types of data
     */
    public void log(String line, Object o) throws IOException
    {
        fileIO.write(line + o);
    }

    public void logInteger(String line, int value) throws IOException
    {
        fileIO.write(line + value);
    }
}
