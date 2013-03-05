package org.TexasTorque.TexasTorque2013.autonomous;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
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

    public boolean run() 
    {
        drivebase.setDriveSpeeds(Constants.MOTOR_STOPPED, Constants.MOTOR_STOPPED);
        manipulator.shootLowWithoutVision();
        if(autonomousTimer.get() > (stopTime + AutonomousManager.getAutoDelay()))
        {
            manipulator.restoreDefaultPositions();
        }
        else if(autonomousTimer.get() > 14.0)
        {
            return true;
        }
        intake.run();
        shooter.run();
        elevator.run();
        magazine.run();
        drivebase.run();
        SmartDashboard.putNumber("TiltAngle", sensorInput.getTiltAngle());
        return false;
    }

    public void end() 
    {
    }
    
}
