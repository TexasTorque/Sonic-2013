package org.TexasTorque.TexasTorque2013.subsystem.manipulator;

import org.TexasTorque.TexasTorque2013.io.DriverInput;
import org.TexasTorque.TexasTorque2013.io.RobotOutput;
import org.TexasTorque.TexasTorque2013.io.SensorInput;

public class Manipulator
{
    
    private static Manipulator instance;
    private RobotOutput robotOutput;
    private SensorInput sensorInput;
    private DriverInput driverInput;
    private Shooter shooter;
    
    public static Manipulator getInstance()
    {
        return (instance == null) ? instance = new Manipulator() : instance;
    }
    
    public Manipulator()
    {
        robotOutput = RobotOutput.getInstance();
        sensorInput = SensorInput.getInstance();
        driverInput = DriverInput.getInstance();
        shooter = Shooter.getInstance();
    }
    
    public void run()
    {
        shooter.run();
    }
    
}
