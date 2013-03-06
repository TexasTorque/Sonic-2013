package org.TexasTorque.TexasTorque2013.autonomous.drive;

import org.TexasTorque.TexasTorque2013.autonomous.AutonomousCommand;

public class AutonomousDriveShift extends AutonomousCommand
{
    
    private boolean shiftState;
    
    public AutonomousDriveShift(boolean highGear)
    {
        super();
        
        shiftState = highGear;
    }
    
    public void reset()
    {
    }
    
    public boolean run()
    {
        drivebase.setShifters(shiftState);
        return true;
    }
}
