package org.TexasTorque.TexasTorque2013.autonomous.drive;

import edu.wpi.first.wpilibj.Timer;
import org.TexasTorque.TexasTorque2013.autonomous.AutonomousCommand;

public class AutonomousPivotTurn extends AutonomousCommand
{
    private Timer timeoutTimer;
    private double timeoutSecs;
    
    private boolean firstCycle;
    private boolean turnPositive;
    private double goal;
    
    private double pivotTurnLeft;
    private double pivotTurnRight;
    
    public AutonomousPivotTurn(double degrees, double leftSpeed, double rightSpeed, double timeout)
    {
        super();
        
        goal = degrees;
        firstCycle = true;
        
        pivotTurnLeft = leftSpeed;
        pivotTurnRight = rightSpeed;
        
        timeoutSecs = timeout;
        timeoutTimer = new Timer();
    }

    public void reset()
    {
        timeoutTimer.reset();
        timeoutTimer.start();
    }

    public boolean run()
    {
        drivebase.setDriveSpeeds(pivotTurnLeft, pivotTurnRight);
        
        if(timeoutTimer.get() > timeoutSecs)
        {
            return true;
        }
        
        double currentAngle = sensorInput.getGyroAngle();
        
        if(currentAngle < goal + 0.1 && currentAngle > goal + 0.1)
        {
            return true;
        }
        
        return false;
    }
    
}