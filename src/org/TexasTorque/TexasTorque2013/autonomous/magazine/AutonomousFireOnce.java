package org.TexasTorque.TexasTorque2013.autonomous.magazine;

import org.TexasTorque.TexasTorque2013.autonomous.AutonomousCommand;
import org.TexasTorque.TexasTorque2013.constants.Constants;

public class AutonomousFireOnce extends AutonomousCommand
{
    private boolean firstCycle;
    
    public AutonomousFireOnce()
    {
        super();
        firstCycle = true;
    }
    
    public void reset()
    {
        firstCycle = true;
    }
    
    public boolean run()
    {
        if(firstCycle)
        {
            magazine.setDesiredState(Constants.MAGAZINE_SHOOTING_STATE);
            firstCycle = false;
            return false;
        }
        else
        {
            magazine.setDesiredState(Constants.MAGAZINE_READY_STATE);
            return true;
        }
    }
}
