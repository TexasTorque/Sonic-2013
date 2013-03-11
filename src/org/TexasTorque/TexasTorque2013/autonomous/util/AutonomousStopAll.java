package org.TexasTorque.TexasTorque2013.autonomous.util;

import org.TexasTorque.TexasTorque2013.autonomous.AutonomousCommand;
import org.TexasTorque.TexasTorque2013.constants.Constants;
import org.TexasTorque.TexasTorque2013.subsystem.manipulator.Elevator;

public class AutonomousStopAll extends AutonomousCommand
{
    public AutonomousStopAll()
    {
        super();
    }
    
    public void reset()
    {
    }
    
    public boolean run()
    {
        drivebase.setDriveSpeeds(Constants.MOTOR_STOPPED, Constants.MOTOR_STOPPED);
        intake.setIntakeSpeed(Constants.MOTOR_STOPPED);
        shooter.stopShooter();
        elevator.setDesiredPosition(Elevator.elevatorBottomPosition);
        tilt.setTiltAngle(0.0);
        magazine.setDesiredState(Constants.MAGAZINE_READY_STATE);
        
        return true;
    }
}
