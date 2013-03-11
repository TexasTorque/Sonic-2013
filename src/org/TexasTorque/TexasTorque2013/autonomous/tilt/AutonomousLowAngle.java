package org.TexasTorque.TexasTorque2013.autonomous.tilt;

import org.TexasTorque.TexasTorque2013.autonomous.AutonomousCommand;
import org.TexasTorque.TexasTorque2013.subsystem.manipulator.Tilt;

public class AutonomousLowAngle extends AutonomousCommand
{
    public AutonomousLowAngle()
    {
        super();
    }
    
    public void reset()
    {
    }
    
    public boolean run()
    {
        tilt.setTiltAngle(Tilt.autonomousLowAngle);
        return true;
    }
}
