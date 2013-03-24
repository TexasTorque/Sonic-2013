package org.TexasTorque.TexasTorque2013.subsystem.manipulator;

import org.TexasTorque.TexasTorque2013.TorqueSubsystem;
import org.TexasTorque.TexasTorque2013.constants.Constants;
import org.TexasTorque.TorqueLib.util.TorqueToggle;

public class Climber extends TorqueSubsystem
{
    
    private static Climber instance;
    
    private TorqueToggle passiveClimberToggle;
    
    private boolean passiveClimberState;
    
    public static Climber getInstance()
    {
        return (instance == null) ? instance = new Climber() : instance;
    }
    
    public Climber()
    {
        super();
        
        passiveClimberToggle = new TorqueToggle();
        
        passiveClimberState = Constants.PASSIVE_HANG_DOWN;
    }
    
    public void run()
    {
        boolean buttonPressed = driverInput.passiveHang();
        
        passiveClimberToggle.calc(buttonPressed);
        
        passiveClimberState = passiveClimberToggle.get();
    }
    
    public void setToRobot()
    {
        robotOutput.setPassiveClimber(passiveClimberState);
    }
    
    public String getKeyNames()
    {
        String names = "ClimberState,";
        
        return names;
    }
    
    public String logData()
    {
        String data = passiveClimberState + ",";
        
        return data;
    }
    
    public void loadParameters()
    {
    }
}
