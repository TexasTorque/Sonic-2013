package org.TexasTorque.TexasTorque2013.autonomous.util;

import org.TexasTorque.TexasTorque2013.autonomous.AutonomousCommand;

public class AutonomousResetGyro extends AutonomousCommand
{
    public AutonomousResetGyro()
    {
        super();
    }
    
    public void reset()
    {
    }
    
    public boolean run()
    {
        sensorInput.resetGyro();
        return true;
    }
}
