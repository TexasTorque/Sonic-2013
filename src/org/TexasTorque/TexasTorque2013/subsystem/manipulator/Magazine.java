package org.TexasTorque.TexasTorque2013.subsystem.manipulator;

import edu.wpi.first.wpilibj.Timer;
import org.TexasTorque.TexasTorque2013.TorqueSubsystem;
import org.TexasTorque.TexasTorque2013.constants.Constants;

public class Magazine extends TorqueSubsystem
{   
    private static Magazine instance;
    
    private double previousTime;
    
    private double deltaTimeForward;
    private double deltaTimeReverse; 
    
    private boolean magazineRaised;
    private boolean triggerBack;
    private int magazineState;
    private int desiredState;
    
    private Magazine()
    {
        super();
        
        previousTime = Timer.getFPGATimestamp();
        
        deltaTimeForward = Constants.MAGAZINE_DELTA_TIME;
        deltaTimeReverse = Constants.MAGAZINE_DELTA_TIME;
        
        magazineRaised = Constants.MAGAZINE_STORED;
        triggerBack = true;
        magazineState = Constants.MAGAZINE_READY_STATE;
        desiredState = Constants.MAGAZINE_READY_STATE;
    }
    
    public static Magazine getInstance()
    {
        return (instance == null) ? instance = new Magazine() : instance;
    }
    
    public void run()
    {
        calcMagazineState();
        robotOutput.setFrisbeeLifter(magazineRaised);
        robotOutput.setLoaderSolenoid(triggerBack);
    }
    
    public void setDesiredState(int state)
    {
        desiredState = state;
    }
    
    private void calcMagazineState()
    {
        
        if(magazineState == Constants.MAGAZINE_LOADING_STATE && desiredState == Constants.MAGAZINE_LOADING_STATE)
        {
            calcLoadingState();
        }
        else if(magazineState == Constants.MAGAZINE_LOADING_STATE && desiredState == Constants.MAGAZINE_READY_STATE)
        {
            magazineState = Constants.MAGAZINE_READY_STATE;
        }
        else if(magazineState == Constants.MAGAZINE_LOADING_STATE && desiredState == Constants.MAGAZINE_SHOOTING_STATE)
        {
            magazineState = Constants.MAGAZINE_READY_STATE;
        }
        else if(magazineState == Constants.MAGAZINE_SHOOTING_STATE)
        {
            calcShootingState();
        }
        else if(magazineState == Constants.MAGAZINE_RESETTING_STATE)
        {
            calcResettingState();
        }
        else if(magazineState == Constants.MAGAZINE_READY_STATE && desiredState == Constants.MAGAZINE_LOADING_STATE)
        {
            magazineState = Constants.MAGAZINE_LOADING_STATE;
        }
        else if(magazineState == Constants.MAGAZINE_READY_STATE && desiredState == Constants.MAGAZINE_SHOOTING_STATE)
        {
            magazineState = Constants.MAGAZINE_SHOOTING_STATE;
            previousTime = Timer.getFPGATimestamp();
        }
        else if(magazineState == Constants.MAGAZINE_READY_STATE && desiredState == Constants.MAGAZINE_READY_STATE)
        {
            calcReadyState();
        }
    }
    
    private void calcReadyState()
    {
        magazineRaised = true;
        triggerBack = true;
    }
    
    private void calcShootingState()
    {
        magazineRaised = true;
        triggerBack = false;
        if(timeHasElapsed(deltaTimeForward))
        {
            magazineState = Constants.MAGAZINE_RESETTING_STATE;
        }
    }
    
    private void calcResettingState()
    {
        magazineRaised = true;
        triggerBack = true;
        if(timeHasElapsed(deltaTimeReverse))
        {
            magazineState = Constants.MAGAZINE_READY_STATE;
        }
    }
    
    private void calcLoadingState()
    {
        magazineRaised = false;
        triggerBack = true;
    }
    
    public boolean getIsWaiting()
    {
        return (magazineState == Constants.MAGAZINE_READY_STATE);
    }
    
    private boolean timeHasElapsed(double dt)
    {
        double currentTime = Timer.getFPGATimestamp();
        if((currentTime - previousTime) > dt)
        {
            previousTime = currentTime;
            return true;
        }
        return false;
    }
    
    public String getKeyNames()
    {
        String names = "MagazinePosition,MagazineTriggerPosition,CurrentMagazineState,DesiredMagazineState,";
        
        return names;
    }
    
    public String logData()
    {
        String data = magazineRaised + ",";
        data += triggerBack + ",";
        data += magazineState + ",";
        data += desiredState + ",";
        
        return data;
    }
    
    public void loadParameters()
    {
        deltaTimeForward = params.getAsDouble("M_DeltaTimeForward", Constants.MAGAZINE_DELTA_TIME);
        deltaTimeReverse = params.getAsDouble("M_DeltaTimeReverse", Constants.MAGAZINE_DELTA_TIME);
    }
    
}
