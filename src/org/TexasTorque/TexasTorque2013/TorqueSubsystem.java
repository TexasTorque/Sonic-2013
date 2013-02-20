package org.TexasTorque.TexasTorque2013;

import org.TexasTorque.TexasTorque2013.io.DriverInput;
import org.TexasTorque.TexasTorque2013.io.RobotOutput;
import org.TexasTorque.TexasTorque2013.io.SensorInput;
import org.TexasTorque.TorqueLib.util.DashboardManager;
import org.TexasTorque.TorqueLib.util.Parameters;
import org.TexasTorque.TorqueLib.util.TorqueLogging;

public abstract class TorqueSubsystem
{
    
    protected static TorqueSubsystem instance;
    protected DashboardManager dashboardManager;
    protected RobotOutput robotOutput;
    protected DriverInput driverInput;
    protected SensorInput sensorInput;
    protected Parameters params;
    protected TorqueLogging logging;
    
    protected TorqueSubsystem()
    {
        dashboardManager = DashboardManager.getInstance();
        robotOutput = RobotOutput.getInstance();
        driverInput = DriverInput.getInstance();
        sensorInput = SensorInput.getInstance();
        params = Parameters.getInstance();
        logging = TorqueLogging.getInstance();
    }
    
    public static TorqueSubsystem getInstance()
    {
        return (instance == null) ? instance = new TorqueSubsystem()
        {
            public void run()
            {
            }
            
            public void loadParameters()
            {
            }
            
            public void logData()
            {
            }
            
            public String getKeyNames()
            {
                return null;
            }
        } : instance;
    }
    
    public abstract void run();
    public abstract void loadParameters();
    public abstract void logData();
    public abstract String getKeyNames();
}
