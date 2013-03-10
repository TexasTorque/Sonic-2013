package org.TexasTorque.TexasTorque2013.autonomous.elevator;

import org.TexasTorque.TexasTorque2013.autonomous.AutonomousCommand;

public class AutonomousCustomElevator extends AutonomousCommand
{
    private int elevatorPosition;
    
    public AutonomousCustomElevator(int position)
    {
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
