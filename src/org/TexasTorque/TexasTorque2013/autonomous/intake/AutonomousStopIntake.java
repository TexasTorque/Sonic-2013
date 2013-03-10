package org.TexasTorque.TexasTorque2013.autonomous.intake;

import org.TexasTorque.TexasTorque2013.autonomous.AutonomousCommand;
import org.TexasTorque.TexasTorque2013.constants.Constants;

public class AutonomousStopIntake extends AutonomousCommand
{
    public AutonomousStopIntake()
    {
        super();
    }
    
    public void reset()
    {
    }
    
    public boolean run()
    {
        intake.setIntakeSpeed(Constants.MOTOR_STOPPED);
        return true;
    }
}
