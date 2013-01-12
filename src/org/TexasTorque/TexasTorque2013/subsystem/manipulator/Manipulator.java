package org.TexasTorque.TexasTorque2013.subsystem.manipulator;

import org.TexasTorque.TexasTorque2013.io.DriverInput;
import org.TexasTorque.TexasTorque2013.io.RobotOutput;
import org.TexasTorque.TexasTorque2013.io.SensorInput;

public class Manipulator
{
    
    private RobotOutput robotOutput;
    private SensorInput sensorInput;
    private DriverInput driverInput;
    public Shooter shooter;
    
    public Manipulator()
    {
        robotOutput = RobotOutput.getInstance();
        sensorInput = SensorInput.getInstance();
        driverInput = DriverInput.getInstance();
        shooter = new Shooter();
    }
    
    public void run()
    {
        shooter.run();
    }
    
}
