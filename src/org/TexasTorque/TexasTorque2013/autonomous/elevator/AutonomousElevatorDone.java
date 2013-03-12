package org.TexasTorque.TexasTorque2013.autonomous.elevator;

import edu.wpi.first.wpilibj.Timer;
import org.TexasTorque.TexasTorque2013.autonomous.AutonomousCommand;

public class AutonomousElevatorDone extends AutonomousCommand
{
    private double timeoutSecs;
    
    private Timer timeoutTimer;
    
    public AutonomousElevatorDone(double timeout)
    {
        super();
        
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
        if(timeoutTimer.get() > timeoutSecs)
        {
            return true;
        }
        
        boolean isDone = elevator.atDesiredPosition();
        return isDone;
    }
}
