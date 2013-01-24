package org.TexasTorque.TexasTorque2013.autonomous;

public class DiagnosticAutonomous extends AutonomousBase
{
    public DiagnosticAutonomous()
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
        }
    }
    
    public void end()
    {
        
    }
}
