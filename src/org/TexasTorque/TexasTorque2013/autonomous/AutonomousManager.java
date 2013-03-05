
package org.TexasTorque.TexasTorque2013.autonomous;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Watchdog;
import java.util.Hashtable;
import org.TexasTorque.TexasTorque2013.constants.Constants;

public class AutonomousManager
{
    
    private String queuedAutoMode;
    private Hashtable autoMapping;
    private static double autoDelay;
    private boolean firstCycle;
    
    public AutonomousManager()
    {
        firstCycle = true;
        autoDelay = 0.0;
        queuedAutoMode = "0";
        autoMapping = new Hashtable();
    }
    
    public void setAutonomousDelay(double delay)
    {
        autoDelay = delay;
    }
    
    public void setAutoMode(int mode)
    {
        queuedAutoMode = Integer.toString(mode);
    }
    
    public static double getAutoDelay()
    {
        return autoDelay;
    }
    
    public void reset()
    {
        autoMapping.clear();
        autoMapping.put(Integer.toString(Constants.DO_NOTHING_AUTO), new DoNothingAutonomous());
        autoMapping.put(Integer.toString(Constants.REAR_SHOOT_AUTO), new RearPyramidAutonomous());
        autoMapping.put(Integer.toString(Constants.REAR_DRIVE_FORWARD_AUTO), new FiveFrisbeeAuto());
        autoMapping.put(Integer.toString(Constants.REAR_DRIVE_FORWARD_DOUBLE_AUTO), new SevenFrisbeeAuto());
    }
    
    public void initAutonomous()
    {
        Timer.delay(autoDelay * 1000);
        ((AutonomousBase)autoMapping.get(queuedAutoMode)).init();
        firstCycle = true;
    }
    
    public void runAutonomous()
    {
        if(firstCycle)
        {
            while(DriverStation.getInstance().isAutonomous())
            {
                Watchdog.getInstance().feed();
               ((AutonomousBase)autoMapping.get(queuedAutoMode)).run();
            }
            ((AutonomousBase)autoMapping.get(queuedAutoMode)).end();
            firstCycle = false;
        }
    }
    
}
