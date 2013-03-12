package org.TexasTorque.TexasTorque2013.autonomous.magazine;

import edu.wpi.first.wpilibj.Timer;
import org.TexasTorque.TexasTorque2013.autonomous.AutonomousCommand;

public class AutonomousMagazineDone extends AutonomousCommand
{
    private double timeoutSecs;
    
    private Timer timeoutTimer;
    
    public AutonomousMagazineDone(double timeout)
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
            return true;
        }
        
        boolean magazineDone = magazine.getIsWaiting();
        return magazineDone;
    }
}
