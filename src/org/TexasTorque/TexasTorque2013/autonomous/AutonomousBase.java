package org.TexasTorque.TexasTorque2013.autonomous;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Watchdog;
import org.TexasTorque.TexasTorque2013.io.RobotOutput;
import org.TexasTorque.TexasTorque2013.io.SensorInput;
import org.TexasTorque.TexasTorque2013.subsystem.drivebase.Drivebase;
import org.TexasTorque.TexasTorque2013.subsystem.manipulator.Elevator;
import org.TexasTorque.TexasTorque2013.subsystem.manipulator.Intake;
import org.TexasTorque.TexasTorque2013.subsystem.manipulator.Magazine;
import org.TexasTorque.TexasTorque2013.subsystem.manipulator.Manipulator;
import org.TexasTorque.TexasTorque2013.subsystem.manipulator.Shooter;
import org.TexasTorque.TorqueLib.util.DashboardManager;
import org.TexasTorque.TorqueLib.util.Parameters;

public abstract class AutonomousBase
{
    
    protected DriverStation ds;
    protected Watchdog watchdog;
    protected RobotOutput robotOutput;
    protected SensorInput sensorInput;
    protected Drivebase drivebase;
    protected Intake intake;
    protected Shooter shooter;
    protected Elevator elevator;
    protected Magazine magazine;
    protected Manipulator manipulator;
    protected DashboardManager dashboardManager;
    protected Parameters params;
    
    public AutonomousBase()
    {
        ds = DriverStation.getInstance();
        watchdog = Watchdog.getInstance();
        robotOutput = RobotOutput.getInstance();
        sensorInput = SensorInput.getInstance();
        dashboardManager = DashboardManager.getInstance();
        
        drivebase = Drivebase.getInstance();
        intake = Intake.getInstance();
        shooter = Shooter.getInstance();
        elevator = Elevator.getInstance();
        magazine = Magazine.getInstance();
        params = Parameters.getInstance();
        manipulator = Manipulator.getInstance();
    }
    
    public abstract void init();
    
    public abstract void run();
    
    public abstract void end();
    
}
