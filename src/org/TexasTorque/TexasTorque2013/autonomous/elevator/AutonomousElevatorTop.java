package org.TexasTorque.TexasTorque2013.autonomous.elevator;

import org.TexasTorque.TexasTorque2013.autonomous.AutonomousCommand;
import org.TexasTorque.TexasTorque2013.subsystem.manipulator.Elevator;

public class AutonomousElevatorTop extends AutonomousCommand
{   
    public AutonomousElevatorTop(int position)
    {
        super();
    }
    
    public void reset()
    {   
    }
    
    public boolean run()
    {
        elevator.setDesiredPosition(Elevator.elevatorTopPosition);
        return true;
    }
}
