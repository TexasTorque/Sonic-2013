package org.TexasTorque.TexasTorque2013.subsystem.manipulator;

import org.TexasTorque.TexasTorque2013.io.DriverInput;
import org.TexasTorque.TexasTorque2013.io.RobotOutput;
import org.TexasTorque.TexasTorque2013.io.SensorInput;
import org.TexasTorque.TorqueLib.util.Parameters;

public class Magazine
{
    private static Magazine instance;
    private RobotOutput robotOutput;
    private SensorInput sensorInput;
    private DriverInput driverInput;
    private Parameters params;
    
    private boolean magazineClosed;
    private boolean loaderReady;
    private int numFrisbees;
    
    public Magazine()
    {
        robotOutput = RobotOutput.getInstance();
        sensorInput = SensorInput.getInstance();
        driverInput = DriverInput.getInstance();
        params = Parameters.getInstance();
        magazineClosed = false;
        loaderReady = true;
        numFrisbees = 0;
    }
    
    public static Magazine getInstance()
    {
        return (instance == null) ? instance = new Magazine() : instance;
    }
    
    public void run()
    {
        robotOutput.setFrisbeeLifter(magazineClosed);
        robotOutput.setLoaderSolenoid(loaderReady);
    }
}
