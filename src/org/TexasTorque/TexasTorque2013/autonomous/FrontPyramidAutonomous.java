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
        robotOutput.setDriveMotors(0.0, 0.0);
        robotOutput.setIntakeMotor(0.0);
        
    }

    public void run() 
    {
        Timer time = new Timer();
        time.start();
        
        
        while(ds.isAutonomous())
        {
            
            manipulator.shootHighWithVision();
            if(time.get() > 7.0)
            {
                manipulator.restoreDefaultPositions();
            }
            //manipulator.run();
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
