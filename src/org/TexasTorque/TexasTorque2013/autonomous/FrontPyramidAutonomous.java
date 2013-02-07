package org.TexasTorque.TexasTorque2013.autonomous;

import edu.wpi.first.wpilibj.Timer;
import org.TexasTorque.TexasTorque2013.constants.Constants;

public class FrontPyramidAutonomous extends AutonomousBase {

    public FrontPyramidAutonomous()
    {
        super();
    }
    
    public void init() 
    {
    }

    public void run() 
    {
        Timer time = new Timer();
        time.start();
        
        while(ds.isAutonomous())
        {
            robotOutput.setDriveMotors(Constants.MOTOR_STOPPED, Constants.MOTOR_STOPPED);
            robotOutput.setIntakeMotor(Constants.MOTOR_STOPPED);
            manipulator.shootHighWithVision();
            if(time.get() > 7.0)
            {
                manipulator.restoreDefaultPositions();
            }
            intake.run();
            shooter.run();
            elevator.run();
            magazine.run();
        }
    }

    public void end() 
    {
    }
    
}
