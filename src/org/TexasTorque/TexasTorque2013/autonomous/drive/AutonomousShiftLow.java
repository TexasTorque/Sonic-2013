package org.TexasTorque.TexasTorque2013.autonomous.drive;

import org.TexasTorque.TexasTorque2013.autonomous.AutonomousCommand;
import org.TexasTorque.TexasTorque2013.constants.Constants;

public class AutonomousShiftLow extends AutonomousCommand
{
    public AutonomousShiftLow()
    {
        super();
    }
    
    public void reset()
    {
    }
    
    public boolean run()
    {
        drivebase.setShifters(Constants.LOW_GEAR);
        return true;
    }
}
