package org.TexasTorque.TexasTorque2013.autonomous;

public class DoNothingAutonomous extends AutonomousBase
{
    
    public DoNothingAutonomous()
    {          
        super();
    }
    
    public void init()
    {
       
    }
    
    public void run()
    {
        while(ds.isAutonomous())
        {
            watchdog.feed();
            robotOutput.setDriveMotors(0.0, 0.0);
        }
    }
    
    public void end()
    {
        
    }
    
}
