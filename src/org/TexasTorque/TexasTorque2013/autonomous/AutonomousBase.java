package org.TexasTorque.TexasTorque2013.autonomous;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Watchdog;
import org.TexasTorque.TexasTorque2013.io.RobotOutput;
import org.TexasTorque.TexasTorque2013.io.SensorInput;
import org.TexasTorque.TorqueLib.util.DashboardManager;

public abstract class AutonomousBase
{
    
    protected DriverStation ds;
    protected Watchdog watchdog;
    protected RobotOutput robotOutput;
    protected SensorInput sensorInput;
    protected DashboardManager dashboardManager;
    
    public AutonomousBase()
    {
        ds = DriverStation.getInstance();
        watchdog = Watchdog.getInstance();
        robotOutput = RobotOutput.getInstance();
        sensorInput = SensorInput.getInstance();
        dashboardManager = DashboardManager.getInstance();
    }
    
    public abstract void init();
    
    public abstract void run();
    
    public abstract void end();
    
}
