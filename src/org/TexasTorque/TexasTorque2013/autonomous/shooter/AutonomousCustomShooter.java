package org.TexasTorque.TexasTorque2013.autonomous.shooter;

import org.TexasTorque.TexasTorque2013.autonomous.AutonomousCommand;

public class AutonomousCustomShooter extends AutonomousCommand
{
    private double frontRate;
    private double middleRate;
    private double rearRate;
    
    public AutonomousCustomShooter(double front, double middle, double rear)
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
