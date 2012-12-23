package org.TexasTorque.TexasTorque2013;

import edu.wpi.first.wpilibj.IterativeRobot;
import org.TexasTorque.TexasTorque2013.io.*;
import org.TexasTorque.TexasTorque2013.subsystem.*;
import org.TexasTorque.TorqueLib.util.DashboardManager;

public class RobotBase extends IterativeRobot
{
    DashboardManager dashboardManager;
    DriverInput driverInput;
    SensorInput sensorInput;
    RobotOutput robotOutput;
    Drivebase drivebase;
    
    public void robotInit()
    {
        dashboardManager = DashboardManager.getInstance();
        driverInput = DriverInput.getInstance();
        sensorInput = SensorInput.getInstance();
        robotOutput = RobotOutput.getInstance();
        drivebase = new Drivebase();
    }

    public void autonomousInit()
    {
        
    }

    public void autonomousPeriodic()
    {
        dashboardManager.updateLCD();
    }
    
    public void autonomousContinuous()
    {
        
    }

    public void teleopInit()
    {
        
    }

    public void teleopPeriodic()
    {
        dashboardManager.updateLCD();
    }
    
    public void teleopContinuous()
    {
        drivebase.run();
    }
    
    public void disabledInit()
    {
        
    }
    
    public void disabledPeriodic()
    {
        dashboardManager.updateLCD();
    }
    
    public void disabledContinuous()
    {
        
    }
}
