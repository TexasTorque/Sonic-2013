package org.TexasTorque.TexasTorque2013.autonomous.shooter;

import javax.microedition.io.Connection;
import org.TexasTorque.TexasTorque2013.autonomous.AutonomousCommand;
import org.TexasTorque.TexasTorque2013.constants.Constants;

public class AutonomousStopShooter extends AutonomousCommand
{
    public AutonomousStopShooter()
    {
        super();
    }
    
    public void reset()
    {
    }
    
    public boolean run()
    {
        shooter.setShooterRates(Constants.SHOOTER_STOPPED_RATE, Constants.SHOOTER_STOPPED_RATE, Constants.SHOOTER_STOPPED_RATE);
        return true;
    }
}
