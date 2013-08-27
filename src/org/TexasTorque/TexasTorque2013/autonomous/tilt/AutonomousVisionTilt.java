package org.TexasTorque.TexasTorque2013.autonomous.tilt;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.TexasTorque.TexasTorque2013.autonomous.AutonomousCommand;
import org.TexasTorque.TexasTorque2013.constants.Constants;
import org.TexasTorque.TexasTorque2013.subsystem.manipulator.Elevator;
import org.TexasTorque.TexasTorque2013.subsystem.manipulator.Shooter;
import org.TexasTorque.TexasTorque2013.subsystem.manipulator.Tilt;

public class AutonomousVisionTilt extends AutonomousCommand
{   
    private boolean firstCycle;
    private double timeOut;
    private double timeIn;
    private Timer timeoutTimer;
    
    private double lastTempAngle;
    private double tempAngle;
    private int visionWait;
    private int initialDelay;
    private int visionCycleDelay;
    private double defaultAngle;
    
    public AutonomousVisionTilt(double timeOut, double timeIn)
    {
        super();
        visionCycleDelay = params.getAsInt("V_CycleDelay", 4);
        initialDelay = params.getAsInt("V_InitialDelay", 50);
        visionWait = 0;
        tempAngle = 0.0;
        this.timeOut = timeOut;
        this.timeIn = timeIn;
        timeoutTimer = new Timer();
        firstCycle = true;
        defaultAngle = Tilt.lowAngle;
    }
    
    public AutonomousVisionTilt(double timeOut, double timeIn, double defaultAngle)
    {
        super();
        visionCycleDelay = params.getAsInt("V_CycleDelay", 4);
        initialDelay = params.getAsInt("V_InitialDelay", 50);
        visionWait = 0;
        tempAngle = 0.0;
        this.timeOut = timeOut;
        this.timeIn = timeIn;
        timeoutTimer = new Timer();
        firstCycle = true;
        defaultAngle = Tilt.lowAngle;
        this.defaultAngle = defaultAngle;
    }
    
    public void reset()
    {
    }
    
    public boolean run()
    {
        if(firstCycle)
        {
            firstCycle = false;
            timeoutTimer.start();
            tempAngle = defaultAngle;
            tilt.setTiltAngle(tempAngle);
        }
        
        visionWait = (visionWait + 1) % visionCycleDelay;
        if(initialDelay > 0)
        {
            initialDelay--;
        }
        
        if(SmartDashboard.getBoolean("found",false) && visionWait == 0 && initialDelay == 0)
        {
            double elevation = SmartDashboard.getNumber("elevation", 0.0);
            if(elevation > 180)
            {
                elevation -= 360;// elevation = -(360 - elevation);
            }
            lastTempAngle = tempAngle;
            tempAngle = SmartDashboard.getNumber("setpoint", lastTempAngle);
            tilt.setTiltAngle(tempAngle);
        }
        
        if(timeoutTimer.get() > timeOut)
        {
            System.err.println("Vision tracking timed out");
            return true;
        }
        
        if(timeoutTimer.get() < timeIn)
        {
            return false;
        }
        
        return tilt.isLocked();
    }
}
