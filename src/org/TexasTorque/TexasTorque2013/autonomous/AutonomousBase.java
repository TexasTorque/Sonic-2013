package org.TexasTorque.TexasTorque2013.autonomous;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Watchdog;
import org.TexasTorque.TexasTorque2013.io.RobotOutput;
import org.TexasTorque.TexasTorque2013.io.SensorInput;

public abstract class AutonomousBase
{
    
    public DriverStation ds = DriverStation.getInstance();
    public Watchdog watchdog = Watchdog.getInstance();
    public RobotOutput robotOutput = RobotOutput.getInstance();
    public SensorInput sensorInput = SensorInput.getInstance();
    
    public AutonomousBase()
    {
    }
    
    public abstract void init();
    
    public abstract void run();
    
    public abstract void end();
    
}
