package org.TexasTorque.TexasTorque2013;

import org.TexasTorque.TexasTorque2013.io.DriverInput;
import org.TexasTorque.TexasTorque2013.io.RobotOutput;
import org.TexasTorque.TexasTorque2013.io.SensorInput;
import org.TexasTorque.TorqueLib.util.DashboardManager;
import org.TexasTorque.TorqueLib.util.Parameters;

public abstract class TorqueSubsystem
{
    
    protected DashboardManager dashboardManager;
    protected RobotOutput robotOutput;
    protected DriverInput driverInput;
    protected SensorInput sensorInput;
    protected Parameters params;
    
    protected TorqueSubsystem()
    {
        dashboardManager = DashboardManager.getInstance();
        robotOutput = RobotOutput.getInstance();
        driverInput = DriverInput.getInstance();
        sensorInput = SensorInput.getInstance();
        params = Parameters.getTeleopInstance();
    }
    
    
    public abstract void run();
    public abstract void setToRobot();
    public abstract void loadParameters();
    public abstract String logData();
    public abstract String getKeyNames();
}
