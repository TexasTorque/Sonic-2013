package org.TexasTorque.TexasTorque2013.autonomous.intake;

import org.TexasTorque.TexasTorque2013.autonomous.AutonomousCommand;
import org.TexasTorque.TexasTorque2013.subsystem.manipulator.Intake;

public class AutonomousIntake extends AutonomousCommand
{   
    public AutonomousIntake()
    {
        super();
    }
    
    public void reset()
    {  
    }
    
    public boolean run()
    {
        intake.setIntakeSpeed(Intake.intakeSpeed);
        return true;
    }
}
