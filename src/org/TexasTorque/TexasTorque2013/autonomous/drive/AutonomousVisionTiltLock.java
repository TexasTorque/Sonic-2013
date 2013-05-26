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
    private Timer timeoutTimer;
    
    private double lastTempTiltAngle;
    private double tempTiltAngle;
    private int visionWait;
    private int initialDelay;
    private int visionCycleDelay;
    private TorquePID visionCorrect;
    
    public AutonomousVisionTiltLock(double timeOut)
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
            timeoutTimer.start();
        }
        
        visionWait = (visionWait + 1) % visionCycleDelay;
        if(initialDelay > 0)
        {
            initialDelay--;
        }
        
        if(tempTiltAngle == 0.0)
        {
            tempTiltAngle = Tilt.lowAngle;
        }
        
        if(SmartDashboard.getBoolean("found",false) && visionWait == 0 && initialDelay == 0)
        {
            // ---- Tilt -----
            double currentTiltAngle = sensorInput.getTiltAngle();
            double elevation = SmartDashboard.getNumber("elevation", 0.0);
            if(elevation > 180)
            {
                elevation -= 360;// elevation = -(360 - elevation);
            }
            double funcAdditive = (currentTiltAngle * currentTiltAngle * currentTiltAngle * Tilt.visionAdditiveThird);
            funcAdditive += (currentTiltAngle * currentTiltAngle * Tilt.visionAdditiveSecond);
            funcAdditive += (currentTiltAngle * Tilt.visionAdditiveFirst);
            funcAdditive += Tilt.visionAdditive;
            
            lastTempTiltAngle = tempTiltAngle;
            tempTiltAngle = currentTiltAngle + elevation + funcAdditive;
            tilt.setTiltAngle(tempTiltAngle);
            
            // ----- Drive -----
            double az = SmartDashboard.getNumber("azimuth",0.0);
            if(az>180)
            {
               az -= 360; //Angle correction Expanded: az = -(360 - az)
            }
            visionCorrect.setSetpoint(sensorInput.getGyroAngle() + az);
            //drivebase.mixTurn(output);
        }
        double output = visionCorrect.calculate(sensorInput.getGyroAngle());
        drivebase.mixTurn(output);
        
        if(timeoutTimer.get() > timeOut)
        {
            return true;
        }
        
        boolean tiltDone = (Math.abs(lastTempTiltAngle - tempTiltAngle) < .2);
        boolean driveDone = visionCorrect.isDone();
        
        return tiltDone && driveDone;        
    }
}
