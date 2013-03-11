package org.TexasTorque.TexasTorque2013.autonomous.util;

import edu.wpi.first.wpilibj.Timer;
import org.TexasTorque.TexasTorque2013.autonomous.AutonomousCommand;

public class AutonomousWait extends AutonomousCommand
{
    private Timer delayTimer;
    
    private double delaySeconds;
    
    public AutonomousWait(double seconds)
    {
        super();
        
        delayTimer = new Timer();
        delaySeconds = seconds;
    }
    
    public void reset()
    {
        delayTimer.reset();
        delayTimer.start();
    }
    
    public boolean run()
    {
        if(delayTimer.get() < delaySeconds)
        {
            return false;
        }
        
        return true;
    }
}
