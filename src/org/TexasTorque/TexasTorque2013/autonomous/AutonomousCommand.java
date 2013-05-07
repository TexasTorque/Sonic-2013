package org.TexasTorque.TexasTorque2013.autonomous;

import org.TexasTorque.TexasTorque2013.io.RobotOutput;
import org.TexasTorque.TexasTorque2013.io.SensorInput;
import org.TexasTorque.TexasTorque2013.subsystem.drivebase.Drivebase;
import org.TexasTorque.TexasTorque2013.subsystem.manipulator.Climber;
import org.TexasTorque.TexasTorque2013.subsystem.manipulator.Elevator;
import org.TexasTorque.TexasTorque2013.subsystem.manipulator.Intake;
import org.TexasTorque.TexasTorque2013.subsystem.manipulator.Magazine;
import org.TexasTorque.TexasTorque2013.subsystem.manipulator.Shooter;
import org.TexasTorque.TexasTorque2013.subsystem.manipulator.Tilt;
import org.TexasTorque.TorqueLib.util.Parameters;

public abstract class AutonomousCommand
{
    protected RobotOutput robotOutput;
    protected SensorInput sensorInput;
    protected Parameters params;
    
    protected Drivebase drivebase;
    protected Intake intake;
    protected Elevator elevator;
    protected Magazine magazine;
    protected Shooter shooter;
    protected Tilt tilt;
    protected Climber climber;
    
    protected AutonomousCommand()
    {
        robotOutput = RobotOutput.getInstance();
        sensorInput = SensorInput.getInstance();
        params = Parameters.getTeleopInstance();
        
        drivebase = Drivebase.getInstance();
        intake = Intake.getInstance();
        elevator = Elevator.getInstance();
        magazine = Magazine.getInstance();
        shooter = Shooter.getInstance();
        tilt = Tilt.getInstance();
        climber = Climber.getInstance();
    }
    
    public abstract void reset();
    public abstract boolean run();
}
