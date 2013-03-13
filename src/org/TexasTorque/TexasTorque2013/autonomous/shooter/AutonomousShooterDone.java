package org.TexasTorque.TexasTorque2013.autonomous.shooter;

import edu.wpi.first.wpilibj.Timer;
import org.TexasTorque.TexasTorque2013.autonomous.AutonomousCommand;

public class AutonomousShooterDone extends AutonomousCommand
{
    private double timeoutSecs;
    
    private Timer timeoutTimer;
    
    public AutonomousShooterDone(double timeout)
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
            System.err.println("Shooter Timed out");
            return true;
        }
        
        boolean shooterDone = shooter.isSpunUp();
        return shooterDone;
    }
}
