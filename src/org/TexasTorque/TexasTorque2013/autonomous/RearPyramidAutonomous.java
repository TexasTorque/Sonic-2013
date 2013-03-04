package org.TexasTorque.TexasTorque2013.autonomous;

import edu.wpi.first.wpilibj.Timer;
import org.TexasTorque.TexasTorque2013.constants.Constants;

public class RearPyramidAutonomous extends AutonomousBase
{

    Timer autonomousTimer;
    
    public RearPyramidAutonomous()
    {
        super();
        autonomousTimer = new Timer();
    }
    
    public void init() 
    {
        autonomousTimer.reset();
        autonomousTimer.start();
    }

    public void run() 
    {
            robotOutput.setDriveMotors(Constants.MOTOR_STOPPED, Constants.MOTOR_STOPPED);
            manipulator.shootLowWithoutVision();
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

    public void end() 
    {
    }
    
}
