package org.TexasTorque.TexasTorque2013.autonomous.elevator;

import org.TexasTorque.TexasTorque2013.autonomous.AutonomousCommand;
import org.TexasTorque.TexasTorque2013.subsystem.manipulator.Elevator;

public class AutonomousElevatorBottom extends AutonomousCommand
{
    public AutonomousElevatorBottom()
    {
        super();
    }
    
    public void reset()
    {
    }
    
    public boolean run()
    {
        elevator.setDesiredPosition(Elevator.elevatorBottomPosition);
        return true;
    }
}
