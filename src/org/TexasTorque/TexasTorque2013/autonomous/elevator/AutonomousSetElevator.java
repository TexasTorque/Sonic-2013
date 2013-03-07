package org.TexasTorque.TexasTorque2013.autonomous.elevator;

import org.TexasTorque.TexasTorque2013.autonomous.AutonomousCommand;

public class AutonomousSetElevator extends AutonomousCommand
{
    private int elevatorPosition;
    
    public AutonomousSetElevator(int position)
    {
        super();
        
        elevatorPosition = position;
    }
    
    public void reset()
    {   
    }
    
    public boolean run()
    {
        elevator.setDesiredPosition(elevatorPosition);
        return true;
    }
}
