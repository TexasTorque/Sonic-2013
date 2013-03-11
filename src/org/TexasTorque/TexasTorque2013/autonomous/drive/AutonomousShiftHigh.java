package org.TexasTorque.TexasTorque2013.autonomous.drive;

import org.TexasTorque.TexasTorque2013.autonomous.AutonomousCommand;
import org.TexasTorque.TexasTorque2013.constants.Constants;

public class AutonomousShiftHigh extends AutonomousCommand
{
    
    public AutonomousShiftHigh()
    {
        super();
    }
    
    public void reset()
    {
    }
    
    public boolean run()
    {
        drivebase.setShifters(Constants.HIGH_GEAR);
        return true;
    }
}
