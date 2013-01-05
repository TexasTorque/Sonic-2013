package org.TexasTorque.TexasTorque2013;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Watchdog;
import org.TexasTorque.TexasTorque2013.autonomous.AutonomousManager;
import org.TexasTorque.TexasTorque2013.io.*;
import org.TexasTorque.TexasTorque2013.subsystem.*;
import org.TexasTorque.TorqueLib.util.DashboardManager;

public class RobotBase extends IterativeRobot
{
    Watchdog watchdog;
    DashboardManager dashboardManager;
    DriverInput driverInput;
    SensorInput sensorInput;
    RobotOutput robotOutput;
    AutonomousManager autoManager;
    Drivebase drivebase;
    
    public void robotInit()
    {
        watchdog = Watchdog.getInstance();
        watchdog.setEnabled(true);
        dashboardManager = DashboardManager.getInstance();
        driverInput = DriverInput.getInstance();
        sensorInput = SensorInput.getInstance();
        robotOutput = RobotOutput.getInstance();
        autoManager = new AutonomousManager();
        drivebase = new Drivebase();
    }

    public void autonomousInit()
    {
        watchdog.setEnabled(true);
        sensorInput.resetEncoders();
    }

    public void autonomousPeriodic()
    {
        watchdog.feed();
        dashboardManager.updateLCD();
    }
    
    public void autonomousContinuous()
    {
        
    }
    

    public void teleopInit()
    {
        watchdog.setEnabled(true);
        sensorInput.resetEncoders();
    }

    public void teleopPeriodic()
    {
        watchdog.feed();
        dashboardManager.updateLCD();
    }
    
    public void teleopContinuous()
    {
        drivebase.run();
    }
    
    
    public void disabledInit()
    {
        watchdog.setEnabled(true);
    }
    
    public void disabledPeriodic()
    {
        watchdog.feed();
        dashboardManager.updateLCD();
    }
    
    public void disabledContinuous()
    {
        
    }
    
}
