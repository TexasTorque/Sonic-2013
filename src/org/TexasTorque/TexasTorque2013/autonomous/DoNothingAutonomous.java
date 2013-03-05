package org.TexasTorque.TexasTorque2013.autonomous;

import org.TexasTorque.TexasTorque2013.constants.Constants;

public class DoNothingAutonomous extends AutonomousBase
{
    
    public DoNothingAutonomous()
    {          
        super();
    }
    
    public void init()
    {
       
    }
    
    public boolean run()
    {
        robotOutput.setDriveMotors(Constants.MOTOR_STOPPED, Constants.MOTOR_STOPPED);
        robotOutput.setElevatorMotors(Constants.MOTOR_STOPPED);
        robotOutput.setShooterMotors(Constants.MOTOR_STOPPED, Constants.MOTOR_STOPPED, Constants.MOTOR_STOPPED);
        robotOutput.setTiltMotor(Constants.MOTOR_STOPPED);
        robotOutput.setIntakeMotor(Constants.MOTOR_STOPPED);
        robotOutput.setFrisbeeLifter(Constants.MAGAZINE_STORED);
        robotOutput.setFiringPin(true);
        return false;
    }
    
    public void end()
    {
        
    }
    
}
