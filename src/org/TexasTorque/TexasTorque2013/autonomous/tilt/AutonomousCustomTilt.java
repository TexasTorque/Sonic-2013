package org.TexasTorque.TexasTorque2013.autonomous.tilt;

import org.TexasTorque.TexasTorque2013.autonomous.AutonomousCommand;

public class AutonomousCustomTilt extends AutonomousCommand
{
    private double tiltAngle;
    
    public AutonomousCustomTilt(double angle)
    {
        super();
        
        tiltAngle = angle;
    }
    
    public void reset()
    {
    }
    
    public boolean run()
    {
        tilt.setTiltAngle(tiltAngle);
        return true;
    }
}
