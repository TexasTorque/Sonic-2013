package org.TexasTorque.TexasTorque2013.autonomous;

import edu.wpi.first.wpilibj.Timer;
import org.TexasTorque.TexasTorque2013.constants.Constants;

public class FrontPyramidAutonomous extends AutonomousBase {

    Timer autonomousTimer;
    
    public FrontPyramidAutonomous()
    {
        super();
        autonomousTimer = new Timer();
    }
    
    public void init() 
    {
        
    }

    public void run() 
    {
        autonomousTimer.reset();
        autonomousTimer.start();
     
        while(ds.isAutonomous())
        {
            watchdog.feed();
            robotOutput.setDriveMotors(Constants.MOTOR_STOPPED, Constants.MOTOR_STOPPED);
            robotOutput.setIntakeMotor(Constants.MOTOR_STOPPED);
            manipulator.shootHighWithVision();
            if(autonomousTimer.get() > 7.0)
            {
                manipulator.restoreDefaultPositions();
            }
            intake.run();
            shooter.run();
            elevator.run();
            magazine.run();
            drivebase.run();
        }
    }

    public void end() 
    {
    }
    
}
