package org.TexasTorque.TexasTorque2013.autonomous.climber;

import org.TexasTorque.TexasTorque2013.autonomous.AutonomousCommand;

public class AutonomousHang extends AutonomousCommand
{
    public AutonomousHang()
    {
        
    }
    
    public void reset()
    {
        
    }
    
    public boolean run()
    {
        robotOutput.setPassiveClimber(true);
        return true;
    }
}
