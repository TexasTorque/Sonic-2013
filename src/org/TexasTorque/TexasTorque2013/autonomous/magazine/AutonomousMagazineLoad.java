package org.TexasTorque.TexasTorque2013.autonomous.magazine;

import org.TexasTorque.TexasTorque2013.autonomous.AutonomousCommand;
import org.TexasTorque.TexasTorque2013.constants.Constants;

public class AutonomousMagazineLoad extends AutonomousCommand
{
    public AutonomousMagazineLoad()
    {
        super();
    }
    
    public void reset()
    {
    }
    
    public boolean run()
    {
        magazine.setDesiredState(Constants.MAGAZINE_LOADING_STATE);
        return true;
    }
}
