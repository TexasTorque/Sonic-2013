package org.TexasTorque.TexasTorque2013.autonomous.drive;

import edu.wpi.first.wpilibj.Timer;
import org.TexasTorque.TexasTorque2013.autonomous.AutonomousCommand;
import org.TexasTorque.TorqueLib.controlLoop.TorquePID;

public class AutonomousTurn extends AutonomousCommand
{
    private TorquePID gyroPID;
    private Timer timeoutTimer;
    private double timeoutSecs;
    private boolean firstCycle;
    private double goal;
    private double cee;
    private double dr;
    
    public AutonomousTurn(double degrees, double timeout, double doneRange)
    {
        super();
        
        goal = degrees;
        firstCycle = true;
        dr = doneRange;
        
        gyroPID = new TorquePID();
        
        timeoutSecs = timeout;
        timeoutTimer = new Timer();
    }

    public void reset()
    {   
        double p = params.getAsDouble("D_TurnGyroP", 0.0);
        double i = params.getAsDouble("D_TurnGyroI", 0.0);
        double d = params.getAsDouble("D_TurnGyroD", 0.0);
        double c = params.getAsDouble("D_TurnGyroAdditive", 0.0);
        double e = params.getAsDouble("D_TurnGyroEpsilon", 0.0);
        
        gyroPID.setPIDGains(p, i, d);
        gyroPID.setEpsilon(e);
        gyroPID.setDoneRange(dr);
        gyroPID.setMinDoneCycles(10);
        gyroPID.reset();
        
        cee = c;
        
        timeoutTimer.reset();
        timeoutTimer.start();
    }

    public boolean run()
    {
        if(firstCycle)
        {
            firstCycle = false;
            gyroPID.setSetpoint(this.sensorInput.getGyroAngle() + goal);
        }

        double xVal = gyroPID.calculate(sensorInput.getGyroAngle());
        xVal += cee * ((xVal < 0.0) ? -1.0 : 1.0);
        double yVal = 0.0;
        
        double leftDrive = yVal + xVal;
        double rightDrive = yVal - xVal;
        
        drivebase.setDriveSpeeds(-leftDrive, -rightDrive);
        
        if(timeoutTimer.get() > timeoutSecs)
        {
            System.err.println("Turn timed out");
            return true;
        }
        
        if(gyroPID.isDone())
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
}