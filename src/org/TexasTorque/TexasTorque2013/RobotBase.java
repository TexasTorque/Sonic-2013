package org.TexasTorque.TexasTorque2013;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Watchdog;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.TexasTorque.TexasTorque2013.autonomous.AutonomousManager;
import org.TexasTorque.TexasTorque2013.constants.Constants;
import org.TexasTorque.TexasTorque2013.io.*;
import org.TexasTorque.TexasTorque2013.subsystem.drivebase.Drivebase;
import org.TexasTorque.TexasTorque2013.subsystem.manipulator.Manipulator;
import org.TexasTorque.TorqueLib.util.DashboardManager;
import org.TexasTorque.TorqueLib.util.Parameters;
import org.TexasTorque.TorqueLib.util.TorqueLogging;

public class RobotBase extends IterativeRobot
{
    Watchdog watchdog;
    DashboardManager dashboardManager;
    DriverInput driverInput;
    SensorInput sensorInput;
    RobotOutput robotOutput;
    AutonomousManager autoManager;
    Parameters params;
    TorqueLogging logging;
    Drivebase drivebase;
    Manipulator  manipulator;
    
    public void robotInit()
    {
        watchdog = Watchdog.getInstance();
        watchdog.setEnabled(true);
        params.load();
        dashboardManager = DashboardManager.getInstance();
        driverInput = DriverInput.getInstance();
        sensorInput = SensorInput.getInstance();
        robotOutput = RobotOutput.getInstance();
        autoManager = new AutonomousManager();
        drivebase = Drivebase.getInstance();
        manipulator = Manipulator.getInstance();
        SmartDashboard.putNumber("Test", 1477);
        SmartDashboard.putNumber("Autonomous Delay", 0.0);
        TorqueLogging.setDashboardLogging(true);
        TorqueLogging.setLoopTime(Constants.TORQUE_LOGGING_LOOP_TIME);
        logging = TorqueLogging.getInstance();
        logging.setKeyMapping("FrameNumber,FrameTime");
        logging.startLogging();
    }

    public void autonomousInit()
    {
        autoManager.setAutonomousDelay(SmartDashboard.getNumber("Autonomous Delay", 0.0));
        sensorInput.resetEncoders();
    }

    public void autonomousPeriodic()
    {
        watchdog.feed();
        dashboardManager.updateLCD();
    }

    public void teleopInit()
    {
        sensorInput.resetEncoders();
    }

    public void teleopPeriodic()
    {
        watchdog.feed();
        logging.logValue("FrameTime", dashboardManager.getDS().getMatchTime());
        dashboardManager.updateLCD();
        drivebase.run();
        manipulator.run();
    }
    
    public void disabledInit()
    {
    }
    
    public void disabledPeriodic()
    {
        watchdog.feed();
        dashboardManager.updateLCD();
    }
    
}
