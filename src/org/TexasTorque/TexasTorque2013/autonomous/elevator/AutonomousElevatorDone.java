package org.TexasTorque.TexasTorque2013.autonomous.elevator;

import org.TexasTorque.TexasTorque2013.autonomous.AutonomousCommand;

public class AutonomousElevatorDone extends AutonomousCommand
{
    public AutonomousElevatorDone()
    {
        super();
    }
    
    public void reset()
    {
        
    }
    
    public boolean run()
    {
        boolean isDone = elevator.atDesiredPosition();
        
        return isDone;
    }
}
