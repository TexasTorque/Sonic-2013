package org.TexasTorque.TexasTorque2013.autonomous.shooter;

import org.TexasTorque.TexasTorque2013.autonomous.AutonomousCommand;

public class AutonomousSetShooter extends AutonomousCommand
{
    private double frontRate;
    private double middleRate;
    private double rearRate;
    
    public AutonomousSetShooter(double front, double middle, double rear)
    {
        super();
        
        frontRate = front;
        middleRate = middle;
        rearRate = rear;
    }
    
    public void reset()
    {
    }
    
    public boolean run()
    {
        shooter.setShooterRates(frontRate, middleRate, rearRate);
        return true;
    }
}
