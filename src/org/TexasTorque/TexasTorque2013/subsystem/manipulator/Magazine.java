package org.TexasTorque.TexasTorque2013.subsystem.manipulator;

import edu.wpi.first.wpilibj.Timer;
import org.TexasTorque.TexasTorque2013.TorqueSubsystem;
import org.TexasTorque.TexasTorque2013.constants.Constants;

public class Magazine extends TorqueSubsystem
{   
    private static Magazine instance;
    
    private double previousTime;
    private double deltaTime;
    
    private boolean magazineRaised;
    private boolean triggerBack;
    private int magazineState;
    private int desiredState;
    
    private Magazine()
    {
        super();
        
        previousTime = Timer.getFPGATimestamp();
        deltaTime = Constants.MAGAZINE_DELTA_TIME;
        
        magazineRaised = false;
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
    
    public synchronized String logData()
    {
        String data = magazineRaised + ",";
        data += triggerBack + ",";
        data += magazineState + ",";
        data += desiredState + ",";
        
        return data;
    }
    
    public synchronized String getKeyNames()
    {
        String names = "MagazinePosition,MagazineTriggerPosition,CurrentMagazineState,DesiredMagazineState,";
        
        return names;
    }
    
    public synchronized void loadParameters()
    {
        deltaTime = params.getAsDouble("M_DeltaTime", Constants.MAGAZINE_DELTA_TIME);
    }
    
    public synchronized void setDesiredState(int state)
    {
        desiredState = state;
    }
    
    private synchronized void calcMagazineState()
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
    
    private synchronized void calcReadyState()
    {
        magazineRaised = true;
        triggerBack = true;
    }
    
    private synchronized void calcShootingState()
    {
        magazineRaised = true;
        triggerBack = false;
        if(timeHasElapsed())
        {
            magazineState = Constants.MAGAZINE_RESETTING_STATE;
        }
    }
    
    private synchronized void calcResettingState()
    {
        magazineRaised = true;
        triggerBack = true;
        if(timeHasElapsed())
        {
            magazineState = Constants.MAGAZINE_READY_STATE;
        }
    }
    
    private synchronized void calcLoadingState()
    {
        magazineRaised = false;
        triggerBack = true;
    }
    
    public synchronized boolean getIsWaiting()
    {
        return (magazineState == Constants.MAGAZINE_READY_STATE);
    }
    
    private synchronized boolean timeHasElapsed()
    {
        double currentTime = Timer.getFPGATimestamp();
        if((currentTime - previousTime) > deltaTime)
        {
            previousTime = currentTime;
            return true;
        }
        return false;
    }
    
}
