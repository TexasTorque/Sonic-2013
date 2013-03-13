package org.TexasTorque.TexasTorque2013.autonomous.tilt;

import edu.wpi.first.wpilibj.Timer;
import org.TexasTorque.TexasTorque2013.autonomous.AutonomousCommand;

public class AutonomousTiltDone extends AutonomousCommand
{
    private double timeoutSecs;
    
    private Timer timeoutTimer;
    
    public AutonomousTiltDone(double timeout)
    {
        super();
        
        timeoutSecs = timeout;
        
        timeoutTimer = new Timer();
    }
    
    public void reset()
    {
        timeoutTimer.reset();
        timeoutTimer.start();
    }
    
    public boolean run()
    {
        if(timeoutTimer.get() > timeoutSecs)
        {
            System.err.println("Tilt timed out");
            return true;
        }
        
        boolean done = tilt.isLocked();
        return done;
    }
}
