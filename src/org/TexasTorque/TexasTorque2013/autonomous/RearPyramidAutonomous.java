package org.TexasTorque.TexasTorque2013.autonomous;

import edu.wpi.first.wpilibj.Timer;
import org.TexasTorque.TexasTorque2013.constants.Constants;

public class RearPyramidAutonomous extends AutonomousBase
{

    Timer autonomousTimer;
    
    double stopTime;
    
    public RearPyramidAutonomous()
    {
        super();
        autonomousTimer = new Timer();
    }
    
    public void init() 
    {
        autonomousTimer.reset();
        autonomousTimer.start();
        stopTime = params.getAsDouble("A_RearPyramidAutonomousStopTime", 5.0);
    }

    public void run() 
    {
        System.err.println("outside");
        drivebase.setDriveSpeeds(Constants.MOTOR_STOPPED, Constants.MOTOR_STOPPED);
        manipulator.shootLowWithoutVision();
        if(autonomousTimer.get() > (stopTime + AutonomousManager.getAutoDelay()));
        {
            System.err.println("inside");
            manipulator.restoreDefaultPositions();
        }
        intake.run();
        shooter.run();
        elevator.run();
        magazine.run();
        drivebase.run();
    }

    public void end() 
    {
    }
    
}
