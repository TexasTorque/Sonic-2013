
package org.TexasTorque.TexasTorque2013.autonomous;

import edu.wpi.first.wpilibj.Timer;
import java.util.Hashtable;
import org.TexasTorque.TexasTorque2013.constants.Constants;

public class AutonomousManager
{
    
    private String queuedAutoMode;
    private Hashtable autoMapping;
    private double autoDelay;
    private boolean firstCycle;
    
    public AutonomousManager()
    {
        firstCycle = true;
        autoDelay = 0.0;
        queuedAutoMode = "0";
        autoMapping = new Hashtable();
        autoMapping.put(Integer.toString(Constants.DO_NOTHING_AUTO), new DoNothingAutonomous());
        autoMapping.put(Integer.toString(Constants.FRONT_SHOOT_AUTO), new FrontPyramidAutonomous());
        autoMapping.put(Integer.toString(Constants.REAR_SHOOT_AUTO), new RearPyramidAutonomous());
    }
    
    public void setAutonomousDelay(double delay)
    {
        autoDelay = delay;
    }
    
    public void setAutoMode(int mode)
    {
        queuedAutoMode = Integer.toString(mode);
    }
    
    public void initAutonomous()
    {
        ((AutonomousBase)autoMapping.get(queuedAutoMode)).init();
        Timer.delay(autoDelay * 1000);
    }
    
    public void runAutonomous()
    {
        if(firstCycle)
        {
            ((AutonomousBase)autoMapping.get(queuedAutoMode)).run();
            ((AutonomousBase)autoMapping.get(queuedAutoMode)).end();
            firstCycle = false;
        }
    }
    
}
