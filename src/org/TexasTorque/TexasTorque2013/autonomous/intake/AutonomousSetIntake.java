package org.TexasTorque.TexasTorque2013.autonomous.intake;

import org.TexasTorque.TexasTorque2013.autonomous.AutonomousCommand;

public class AutonomousSetIntake extends AutonomousCommand
{
    private double intakeSpeed;
    
    public AutonomousSetIntake(double motorSpeed)
    {
        super();
        
        intakeSpeed = motorSpeed;
    }
    
    public void reset()
    {  
    }
    
    public boolean run()
    {
        intake.setIntakeSpeed(intakeSpeed);
        return true;
    }
}
