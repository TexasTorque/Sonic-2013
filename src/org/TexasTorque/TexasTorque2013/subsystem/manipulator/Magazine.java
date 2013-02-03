package org.TexasTorque.TexasTorque2013.subsystem.manipulator;

import edu.wpi.first.wpilibj.Timer;
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
    
    private Timer resetTimer;
    private boolean magazineClosed;
    private boolean loaderReady;
    private int numFrisbees;
    private int magazineState;
    
    public Magazine()
    {
        robotOutput = RobotOutput.getInstance();
        sensorInput = SensorInput.getInstance();
        driverInput = DriverInput.getInstance();
        params = Parameters.getInstance();
        
        resetTimer = new Timer();
        magazineClosed = false;
        loaderReady = true;
        numFrisbees = 0;
        magazineState = Constants.MAGAZINE_RESET_STATE;
    }
    
    public static Magazine getInstance()
    {
        return (instance == null) ? instance = new Magazine() : instance;
    }
    
    public void run()
    {
        if(driverInput.shootHigh())
        {
            magazineClosed = true;
            if(magazineState == Constants.MAGAZINE_RESET_STATE)
            {
                if(!timerStarted())
                {
                    resetTimer.reset();
                    resetTimer.start();
                }
                else if(resetTimer.get() >= 0.5)
                {
                    loaderReady = false;
                    resetTimer.stop();
                    resetTimer.reset();
                    magazineState = Constants.MAGAZINE_SHOT_STATE;
                }
            }
            else if(magazineState == Constants.MAGAZINE_SHOT_STATE)
            {
                if(!timerStarted())
                {
                    resetTimer.reset();
                    resetTimer.start();
                }
                else if(resetTimer.get() >= 0.5)
                {
                    loaderReady = true;
                    resetTimer.stop();
                    resetTimer.reset();
                    magazineState = Constants.MAGAZINE_RESET_STATE;
                }
            }
        }
        else
        {
            magazineClosed = false;
            loaderReady = true;
            magazineState = Constants.MAGAZINE_RESET_STATE;
            resetTimer.stop();
            resetTimer.reset();
        }
        robotOutput.setFrisbeeLifter(magazineClosed);
        robotOutput.setLoaderSolenoid(loaderReady);
    }
    
    private boolean timerStarted()
    {
        return (resetTimer.get() >= 0.0001);
    }
    
}
