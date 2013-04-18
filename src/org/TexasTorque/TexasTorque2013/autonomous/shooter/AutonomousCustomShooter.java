package org.TexasTorque.TexasTorque2013.autonomous.shooter;

import org.TexasTorque.TexasTorque2013.autonomous.AutonomousCommand;

public class AutonomousCustomShooter extends AutonomousCommand
{
    private double frontRate;
    private double rearRate;
    
    public AutonomousCustomShooter(double front, double rear)
    {
        super();
        
        frontRate = front;
        rearRate = rear;
    }
    
    public void reset()
    {
    }
    
    public boolean run()
    {
        shooter.setShooterRates(frontRate, rearRate);
        return true;
    }
}
