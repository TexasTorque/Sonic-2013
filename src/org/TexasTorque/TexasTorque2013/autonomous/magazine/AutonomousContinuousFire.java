package org.TexasTorque.TexasTorque2013.autonomous.magazine;

import org.TexasTorque.TexasTorque2013.autonomous.AutonomousCommand;
import org.TexasTorque.TexasTorque2013.constants.Constants;

public class AutonomousContinuousFire extends AutonomousCommand
{
    public AutonomousContinuousFire()
    {
        super();
    }
    
    public void reset()
    {
    }
    
    public boolean run()
    {
        magazine.setDesiredState(Constants.MAGAZINE_SHOOTING_STATE);
        return true;
    }
}
