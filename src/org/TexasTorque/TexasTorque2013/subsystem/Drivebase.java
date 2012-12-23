package org.TexasTorque.TexasTorque2013.subsystem;

import org.TexasTorque.TexasTorque2013.io.*;

public class Drivebase 
{
    
    private RobotOutput robotOutput;
    private DriverInput driverInput;
    private SensorInput sensorInput;
            
    public Drivebase()
    {
        robotOutput = RobotOutput.getInstance();
        driverInput = DriverInput.getInstance();
        sensorInput = SensorInput.getInstance();
    }
    
    public void run()
    {
        
    }
}
