package org.TexasTorque.TexasTorque2013.autonomous.shooter;

import org.TexasTorque.TexasTorque2013.autonomous.AutonomousCommand;
import org.TexasTorque.TexasTorque2013.subsystem.manipulator.Shooter;

public class AutonomousSpinShooter extends AutonomousCommand
{
    public AutonomousSpinShooter()
    {
        super();
    }
    
    public void reset()
    {
    }
    
    public boolean run()
    {
        double frontRate = Shooter.frontShooterRate;
        double middleRate = Shooter.middleShooterRate;
        double rearRate = Shooter.rearShooterRate;
        
        shooter.setShooterRates(frontRate, middleRate, rearRate);
        
        return true;
    }
}
