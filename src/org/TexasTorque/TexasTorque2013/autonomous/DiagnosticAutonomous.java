package org.TexasTorque.TexasTorque2013.autonomous;

import org.TexasTorque.TexasTorque2013.constants.Constants;

public class DiagnosticAutonomous extends AutonomousBase
{
 
    private final int DRIVE_DIAGNOSTIC = 0;
    private final int SHIFTER_DIAGNOSTIC = 1;
    
    private int diagnosticState;
    private int previousRateLeft;
    private int previousRateRight;
    
    public DiagnosticAutonomous()
    {
        super();
        diagnosticState = DRIVE_DIAGNOSTIC;
        previousRateLeft = 0;
        previousRateRight = 0;
    }
    
    public void init()
    {
       sensorInput.resetEncoders();
    }
    
    public void run()
    {
        while(ds.isAutonomous())
        {
            watchdog.feed();
            if(diagnosticState == DRIVE_DIAGNOSTIC)
            {
                intake.setIntakeSpeed(Constants.MOTOR_STOPPED);
                shooter.setShooterRates(Constants.SHOOTER_STOPPED_RATE, Constants.SHOOTER_STOPPED_RATE);
                magazine.setDesiredState(Constants.MAGAZINE_READY_STATE);
                elevator.setDesiredPosition(params.getAsInt("E_ElevatorBottomPosition", Constants.DEFAULT_ELEVATOR_BOTTOM_POSITION));
                robotOutput.setDriveMotors(1.0, 1.0);
                // stuff for the tilt
                if(sensorInput.getLeftDriveEncoder() > 1000 && sensorInput.getRightDriveEncoder() > 1000)
                {
                    previousRateLeft = (int)sensorInput.getLeftDriveEncoderRate();
                    previousRateRight = (int)sensorInput.getRightDriveEncoderRate();
                    diagnosticState = SHIFTER_DIAGNOSTIC;
                }
            }
            else if(diagnosticState == SHIFTER_DIAGNOSTIC)
            {
                robotOutput.setShifters(true);
                if(sensorInput.getLeftDriveEncoderRate() > previousRateLeft + 100 && sensorInput.getRightDriveEncoderRate() > previousRateRight + 100)
                {
                    
                }
            }
        }
    }
    
    public void end()
    {
        
    }
    
}
