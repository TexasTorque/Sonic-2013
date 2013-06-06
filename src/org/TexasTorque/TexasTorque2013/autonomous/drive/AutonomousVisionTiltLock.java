package org.TexasTorque.TexasTorque2013.autonomous.drive;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.TexasTorque.TexasTorque2013.autonomous.AutonomousCommand;
import org.TexasTorque.TexasTorque2013.constants.Constants;
import org.TexasTorque.TexasTorque2013.subsystem.manipulator.Elevator;
import org.TexasTorque.TexasTorque2013.subsystem.manipulator.Shooter;
import org.TexasTorque.TexasTorque2013.subsystem.manipulator.Tilt;
import org.TexasTorque.TorqueLib.controlLoop.TorquePID;

public class AutonomousVisionTiltLock extends AutonomousCommand
{   
    private boolean firstCycle;
    private double timeOut;
    private double timeIn;
    private Timer timeoutTimer;
    
    private double lastTempTiltAngle;
    private double tempTiltAngle;
    private int visionWait;
    private int initialDelay;
    private int visionCycleDelay;
    private TorquePID visionCorrect;
    
    public AutonomousVisionTiltLock(double timeOut, double timeIn)
    {
        super();
        visionCycleDelay = params.getAsInt("V_CycleDelay", 4);
        initialDelay = params.getAsInt("V_InitialDelay", 50);
        
        visionCorrect = new TorquePID();
        visionCorrect.setSetpoint(0.0);
        double p = params.getAsDouble("V_TurnP", 0.0);
        double i = params.getAsDouble("V_TurnI", 0.0);
        double d = params.getAsDouble("V_TurnD", 0.0);
        double e = params.getAsDouble("V_TurnEpsilon", 0.0);
        double r = params.getAsDouble("V_TurnDoneRange", 0.0);
        visionCorrect.setPIDGains(p, i, d);
        visionCorrect.setEpsilon(e);
        visionCorrect.setDoneRange(r);
        visionCorrect.reset();
        
        visionWait = 0;
        tempTiltAngle = 0.0;
        this.timeOut = timeOut;
        this.timeIn = timeIn;
        timeoutTimer = new Timer();
        firstCycle = true;
    }
    
    public void reset()
    {
    }
    
    public boolean run()
    {
        if(firstCycle)
        {
            firstCycle = !firstCycle;
            timeoutTimer.start();
            tempTiltAngle = Tilt.lowAngle;
            tilt.setTiltAngle(tempTiltAngle);
        }
        
        visionWait = (visionWait + 1) % visionCycleDelay;
        if(initialDelay > 0)
        {
            initialDelay--;
        }
        
        if(SmartDashboard.getBoolean("found",false) && visionWait == 0 && initialDelay == 0)
        {
            // ---- Tilt -----
            double elevation = SmartDashboard.getNumber("elevation", 0.0);
            if(elevation > 180)
            {
                elevation -= 360;// elevation = -(360 - elevation);
            }
            
            lastTempTiltAngle = tempTiltAngle;
            tempTiltAngle = SmartDashboard.getNumber("setpoint", lastTempTiltAngle);
            tilt.setTiltAngle(tempTiltAngle);
            
            // ----- Drive -----
            double az = SmartDashboard.getNumber("azimuth",0.0);
            if(az>180)
            {
               az -= 360; //Angle correction Expanded: az = -(360 - az)
            }
            drivebase.calcAngleCorrection(az);
        }
        else
        {
            drivebase.calcAngleCorrection(params.getAsDouble("V_TurnAdditive", 0.0));
        }
        double output = drivebase.calcAngleCorrection();
        drivebase.mixTurn(output);
        
        if(timeoutTimer.get() > timeOut)
        {
            System.err.println("Vision Lock timed out");
            return true;
        }
        
        boolean tiltDone = tilt.isLocked();
        //boolean driveDone = visionCorrect.isDone();
        
        return (tiltDone && timeoutTimer.get() > timeIn);// && driveDone;        
    }
}
