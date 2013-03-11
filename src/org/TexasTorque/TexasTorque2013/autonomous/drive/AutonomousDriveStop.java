package org.TexasTorque.TexasTorque2013.autonomous.drive;

import org.TexasTorque.TexasTorque2013.autonomous.AutonomousCommand;
import org.TexasTorque.TexasTorque2013.constants.Constants;

public class AutonomousDriveStop extends AutonomousCommand
{
    public AutonomousDriveStop()
    {
        super();
    }
    
    public void reset()
    {
    }
    
    public boolean run()
    {
        drivebase.setDriveSpeeds(Constants.MOTOR_STOPPED, Constants.MOTOR_STOPPED);
        return true;
    }
}
