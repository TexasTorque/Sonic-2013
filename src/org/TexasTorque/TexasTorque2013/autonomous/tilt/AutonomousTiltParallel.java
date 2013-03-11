package org.TexasTorque.TexasTorque2013.autonomous.tilt;

import org.TexasTorque.TexasTorque2013.autonomous.AutonomousCommand;

public class AutonomousTiltParallel extends AutonomousCommand
{
    public AutonomousTiltParallel()
    {
        super();
    }
    
    public void reset()
    {
    }
    
    public boolean run()
    {
        tilt.setTiltAngle(0.0);
        return true;
    }
}
