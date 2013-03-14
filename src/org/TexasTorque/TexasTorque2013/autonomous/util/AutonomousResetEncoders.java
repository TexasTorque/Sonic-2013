package org.TexasTorque.TexasTorque2013.autonomous.util;

import org.TexasTorque.TexasTorque2013.autonomous.AutonomousCommand;

public class AutonomousResetEncoders extends AutonomousCommand
{
    public AutonomousResetEncoders()
    {
        super();
    }
    
    public void reset()
    {
    }
    
    public boolean run()
    {
        sensorInput.resetEncoders();
        return true;
    }
}
