package org.TexasTorque.TexasTorque2013.autonomous.shooter;

import org.TexasTorque.TexasTorque2013.autonomous.AutonomousCommand;

public class AutonomousShooterDone extends AutonomousCommand
{
    public AutonomousShooterDone()
    {
        super();
    }
    
    public void reset()
    {
    }
    
    public boolean run()
    {
        boolean shooterDone = shooter.isSpunUp();
        return shooterDone;
    }
}
