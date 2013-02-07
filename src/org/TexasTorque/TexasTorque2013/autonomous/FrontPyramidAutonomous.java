package org.TexasTorque.TexasTorque2013.autonomous;

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
        shooter.setShooterRates(params.getAsInt("S_FrontShooterRate", Constants.DEFAULT_FRONT_SHOOTER_RATE),
                 params.getAsInt("S_RearShooterRate", Constants.DEFAULT_REAR_SHOOTER_RATE));
        
    }

    public void run() 
    {
        
    }

    public void end() 
    {
    
    }
    
}
