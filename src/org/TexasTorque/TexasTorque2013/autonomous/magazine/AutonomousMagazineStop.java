package org.TexasTorque.TexasTorque2013.autonomous.magazine;

import org.TexasTorque.TexasTorque2013.autonomous.AutonomousCommand;
import org.TexasTorque.TexasTorque2013.constants.Constants;

public class AutonomousMagazineStop extends AutonomousCommand
{
    public AutonomousMagazineStop()
    {
        super();
    }
    
    public void reset()
    {
    }
    
    public boolean run()
    {
        magazine.setDesiredState(Constants.MAGAZINE_READY_STATE);
        return true;
    }
}
