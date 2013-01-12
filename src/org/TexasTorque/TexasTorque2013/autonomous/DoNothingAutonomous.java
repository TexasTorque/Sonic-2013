package org.TexasTorque.TexasTorque2013.autonomous;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Watchdog;
import org.TexasTorque.TexasTorque2013.io.RobotOutput;

public class DoNothingAutonomous extends AutonomousBase
{
    
    public DoNothingAutonomous()
    {          
    }
    
    public void init()
    {
        watchdog = Watchdog.getInstance();
        watchdog.setEnabled(true);
        robotOutput = RobotOutput.getInstance();
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
