package org.TexasTorque.TexasTorque2013.autonomous;

import java.util.Hashtable;
import org.TexasTorque.TexasTorque2013.constants.Constants;

public class AutonomousManager
{
    
    private String queuedAutoMode;
    private Hashtable autoMapping;
    
    public AutonomousManager()
    {
        autoMapping = new Hashtable();
        autoMapping.put(Integer.toString(Constants.DO_NOTHING_AUTO), new DoNothingAutonomous());
    }
    
    public void setAutoMode(int mode)
    {
        queuedAutoMode = Integer.toString(mode);
    }
    
    public void initAutonomous()
    {
        ((AutonomousBase)autoMapping.get(queuedAutoMode)).init();
    }
    
    public void runAutonomous()
    {
        ((AutonomousBase)autoMapping.get(queuedAutoMode)).run();
        ((AutonomousBase)autoMapping.get(queuedAutoMode)).end();
    }
    
}
