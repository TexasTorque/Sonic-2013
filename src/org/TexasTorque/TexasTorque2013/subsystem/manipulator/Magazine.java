package org.TexasTorque.TexasTorque2013.subsystem.manipulator;

import org.TexasTorque.TexasTorque2013.constants.Constants;
import org.TexasTorque.TexasTorque2013.io.DriverInput;
import org.TexasTorque.TexasTorque2013.io.RobotOutput;
import org.TexasTorque.TexasTorque2013.io.SensorInput;
import org.TexasTorque.TorqueLib.util.Parameters;

public class Magazine
{
    private static Magazine instance;
    private RobotOutput robotOutput;
    private SensorInput sensorInput;
    private DriverInput driverInput;
    private Parameters params;
    
    private boolean magazineCompressed;
    private boolean triggerBack;
    private int magazineState;
    private int desiredState;
    
    public Magazine()
    {
        robotOutput = RobotOutput.getInstance();
        sensorInput = SensorInput.getInstance();
        driverInput = DriverInput.getInstance();
        params = Parameters.getInstance();
        magazineCompressed = false;
        triggerBack = true;
        magazineState = Constants.MAGAZINE_READY_STATE;
        
    }
    
    public static Magazine getInstance()
    {
        return (instance == null) ? instance = new Magazine() : instance;
    }
    
    public void run()
    {
        calcMagazineState();
        robotOutput.setFrisbeeLifter(magazineCompressed);
        robotOutput.setLoaderSolenoid(triggerBack);
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
        }
        else if(magazineState == Constants.MAGAZINE_READY_STATE && desiredState == Constants.MAGAZINE_READY_STATE)
        {
            calcReadyState();
        }
        
        /*if(magazineState == Constants.MAGAZINE_LOADING_STATE)
        {
            if(desiredState == Constants.MAGAZINE_LOADING_STATE)
            {
                calcLoadingState();
            }
            else
            {
                magazineState = Constants.MAGAZINE_READY_STATE;
            }
        }
        if(magazineState == Constants.MAGAZINE_SHOOTING_STATE)
        {
            calcShootingState();
        }
        else if(magazineState == Constants.MAGAZINE_RESETTING_STATE)
        {
            calcResettingState();
        }
        else if(magazineState == Constants.MAGAZINE_READY_STATE)
        {
            if(desiredState == Constants.MAGAZINE_LOADING_STATE)
            {
                magazineState = Constants.MAGAZINE_LOADING_STATE;
            }
            else if(desiredState == Constants.MAGAZINE_SHOOTING_STATE)
            {
                magazineState = Constants.MAGAZINE_SHOOTING_STATE;
            }
            else
            {
                calcReadyState();
            }
        }*/
    }
    
    private synchronized void calcReadyState()
    {
        magazineCompressed = true;
        triggerBack = true;
    }
    
    private synchronized void calcShootingState()
    {
        magazineCompressed = true;
        triggerBack = false;
        if(true) // Conditional that will say when the frisbee has passed the sensor
        {
            magazineState = Constants.MAGAZINE_RESETTING_STATE;
        }
    }
    
    private synchronized void calcResettingState()
    {
        magazineCompressed = true;
        triggerBack = true;
        if(true) // Conditional that will say when the loader has been reset
        {
            magazineState = Constants.MAGAZINE_READY_STATE;
        }
    }
    
    private synchronized void calcLoadingState()
    {
        magazineCompressed = false;
        triggerBack = true;
    }
    
    public synchronized boolean getIsWaiting()
    {
        return (magazineState == Constants.MAGAZINE_READY_STATE);
    }
    
}
