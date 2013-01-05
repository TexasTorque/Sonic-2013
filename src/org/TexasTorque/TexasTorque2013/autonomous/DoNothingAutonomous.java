package org.TexasTorque.TexasTorque2013.autonomous;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Watchdog;
import org.TexasTorque.TexasTorque2013.io.RobotOutput;

public class DoNothingAutonomous extends AutonomousBase
{
    
    private RobotOutput robotOutput;
    private DriverStation ds;
    private Watchdog watchdog;
    
    public DoNothingAutonomous()
    {          
    }
    
    public void init()
    {
        watchdog = Watchdog.getInstance();
        watchdog.setEnabled(true);
        robotOutput = RobotOutput.getInstance();
        ds = DriverStation.getInstance();
    }
    
    public void run()
    {
        while(ds.isAutonomous())
        {
            watchdog.feed();
            robotOutput.setLeftDriveMotors(0.0);
            robotOutput.setRightDriveMotors(0.0);
        }
    }
    
    public void end()
    {
        
    }
    
}
