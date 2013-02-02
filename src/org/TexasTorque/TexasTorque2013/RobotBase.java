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
    Parameters params;
    TorqueLogging logging;
    DashboardManager dashboardManager;
    DriverInput driverInput;
    SensorInput sensorInput;
    RobotOutput robotOutput;
    Drivebase drivebase;
    Manipulator  manipulator;
    AutonomousManager autoManager;
    
    public void robotInit()
    {
        watchdog = Watchdog.getInstance();
        watchdog.setEnabled(true);
        params = Parameters.getInstance();
        params.load();
        initSmartDashboard();
        initLogging();
        dashboardManager = DashboardManager.getInstance();
        driverInput = DriverInput.getInstance();
        sensorInput = SensorInput.getInstance();
        robotOutput = RobotOutput.getInstance();
        drivebase = Drivebase.getInstance();
        manipulator = Manipulator.getInstance();
        autoManager = new AutonomousManager();
    }

    public void autonomousInit()
    {
        autoManager.setAutonomousDelay(driverInput.getAutonomousDelay());
        sensorInput.resetEncoders();
    }

    public void autonomousPeriodic()
    {
        watchdog.feed();
        dashboardManager.updateLCD();
    }

    public void teleopInit()
    {
        params.load();
        sensorInput.resetEncoders();
    }

    public void teleopPeriodic()
    {
        watchdog.feed();
        logging.logValue("FrameTime", dashboardManager.getDS().getMatchTime());
        drivebase.run();
        manipulator.run();
        dashboardManager.updateLCD();
    }
    
    public void disabledInit()
    {
    }
    
    public void disabledPeriodic()
    {
        watchdog.feed();
        dashboardManager.updateLCD();
    }
    
    public void initSmartDashboard()
    {
        SmartDashboard.putNumber("Autonomous Delay", 0.0);
    }
    
    public void initLogging()
    {
        TorqueLogging.setDashboardLogging(true);
        TorqueLogging.setLoopTime(params.getAsInt("LoggingLoopTime", Constants.TORQUE_LOGGING_LOOP_TIME));
        logging = TorqueLogging.getInstance();
        logging.setKeyMapping("FrameNumber,FrameTime");
        logging.startLogging();
    }
    
}
