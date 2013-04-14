package org.TexasTorque.TexasTorque2013.autonomous.drive;

import edu.wpi.first.wpilibj.Timer;
import org.TexasTorque.TexasTorque2013.autonomous.AutonomousCommand;
import org.TexasTorque.TorqueLib.controlLoop.TorquePID;

public class AutonomousPivotPIDTurn extends AutonomousCommand
{
    
    private TorquePID gyroPID;
    private Timer timeoutTimer;
    
    private double timeoutSecs;
    private boolean firstCycle;
    private double goal;
    private double leftSpeed;
    private double rightSpeed;
    private double setpoint;
    private boolean inPIDControl;
    
    public AutonomousPivotPIDTurn(double dAngle, double left, double right, double timeout)
    {
        super();
        
        gyroPID = new TorquePID();
        timeoutTimer = new Timer();
        
        leftSpeed = left;
        rightSpeed = right;
        timeoutSecs = timeout;
        inPIDControl = false;
        goal = dAngle;
        firstCycle = true;
    }
    
    public void reset()
    {
        double p = params.getAsDouble("D_TurnGyroP", 0.0);
        double i = params.getAsDouble("D_TurnGyroI", 0.0);
        double d = params.getAsDouble("D_TurnGyroD", 0.0);
        double e = params.getAsDouble("D_TurnGyroEpsilon", 0.0);
        double r = params.getAsDouble("D_TurnGyroDoneRange", 0.0);
        
        gyroPID.setPIDGains(p, i, d);
        gyroPID.setEpsilon(e);
        gyroPID.setDoneRange(r);
        gyroPID.setMinDoneCycles(10);
        gyroPID.reset();
        
        timeoutTimer.reset();
        timeoutTimer.start();
    }
    
    public boolean run()
    {
        if(firstCycle)
        {
            firstCycle = false;
            setpoint = this.sensorInput.getGyroAngle() + goal;
            gyroPID.setSetpoint(setpoint);
        }
        
        double currentAngle = sensorInput.getGyroAngle();
        double errorAbs = Math.abs(setpoint) - Math.abs(currentAngle);
        
        if(errorAbs > 10.0)
        {
            System.err.println("pivot");
            drivebase.setDriveSpeeds(leftSpeed, rightSpeed);
            inPIDControl = false;
        }
        else
        {
            System.err.println("pid");
            double xVal = -gyroPID.calculate(sensorInput.getGyroAngle());
            double yVal = 0.0;

            double leftDrive = xVal + yVal;
            double rightDrive = xVal - yVal;

            drivebase.setDriveSpeeds(leftDrive, -rightDrive);
            inPIDControl = true;
        }
        
        if(timeoutTimer.get() > timeoutSecs)
        {
            System.err.println("Timed out");
            return true;
        }
        
        if(inPIDControl && gyroPID.isDone())
        {
            System.err.println("done turning");
            return true;
        }
        else
        {
            return false;
        }
    }
}
