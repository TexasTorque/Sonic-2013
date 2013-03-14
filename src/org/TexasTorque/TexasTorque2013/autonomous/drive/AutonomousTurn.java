package org.TexasTorque.TexasTorque2013.autonomous.drive;

import edu.wpi.first.wpilibj.Timer;
import org.TexasTorque.TexasTorque2013.autonomous.AutonomousCommand;
import org.TexasTorque.TorqueLib.controlLoop.SimPID;

public class AutonomousTurn extends AutonomousCommand
{
    private SimPID gyroPID;
    private Timer timeoutTimer;
    private double timeoutSecs;
    private boolean firstCycle;
    private double goal;
    
    public AutonomousTurn(double degrees, double timeout)
    {
        super();
        goal = degrees;
        this.firstCycle = true;
        
        gyroPID = new SimPID();
        gyroPID.setDesiredValue(goal);
        timeoutSecs = timeout;
        timeoutTimer = new Timer();
    }

    public void reset() {
        System.err.println("reset");
        
        double p = params.getAsDouble("D_TurnGyroP", 0.0);
        double i = params.getAsDouble("D_TurnGyroI", 0.0);
        double d = params.getAsDouble("D_TurnGyroD", 0.0);
        double e = params.getAsDouble("D_TurnGyroEpsilon", 0.0);
        double r = params.getAsDouble("D_TurnGyroDoneRange", 0.0);
        
        gyroPID.setConstants(p, i, d);
        gyroPID.setErrorEpsilon(e);
        gyroPID.setDoneRange(r);
        gyroPID.resetErrorSum();
        gyroPID.resetPreviousVal();
        
        timeoutTimer.reset();
        timeoutTimer.start();
    }

    public boolean run() {
        if ( this.firstCycle){
            this.firstCycle = false;
            this.gyroPID.setDesiredValue(this.sensorInput.getGyroAngle()+ this.goal);
        }

        double xVal = -this.gyroPID.calcPID(this.sensorInput.getGyroAngle());
        double yVal = 0.0;
        
        double leftDrive = xVal+yVal;
        double rightDrive = xVal-yVal;
        
        drivebase.setDriveSpeeds(leftDrive, rightDrive);
        
        if(timeoutTimer.get() > timeoutSecs)
        {
            return true;
        }
        
        if (this.gyroPID.isDone()){
            return true;
        }else{
            return false;
        }
    }
    
}