package org.TexasTorque.TexasTorque2013;

import edu.wpi.first.wpilibj.SimpleRobot;
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

public class RobotBase extends SimpleRobot
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
    
    double loopTime;
    boolean logData;
    
    public void robotInit()
    {     
        watchdog = Watchdog.getInstance();
        watchdog.setEnabled(true);
        watchdog.setExpiration(0.2);
        
        params = Parameters.getInstance();
        params.load();
        
        initSmartDashboard();
        initLogging();
        
        dashboardManager = DashboardManager.getInstance();
        driverInput = DriverInput.getInstance();
        sensorInput = SensorInput.getInstance();
        robotOutput = RobotOutput.getInstance();
        drivebase = (Drivebase) Drivebase.getInstance();
        manipulator = (Manipulator) Manipulator.getInstance();
        
        autoManager = new AutonomousManager();
        
        driverInput.pullJoystickTypes();
        
        loopTime = 0.0;
    }
    
    public void autonomous()
    {
        autonomousInit();
        while(isAutonomous())
        {
            autonomousPeriodic();
        }
    }
    
    public void operatorControl()
    {
        teleopInit();
        while(isOperatorControl())
        {
            teleopPeriodic();
        }
    }
    
    public void disabled()
    {
        disabledInit();
        while(isDisabled())
        {
            disabledPeriodic();
        }
    }

    public void autonomousInit()
    {
        pullNewPIDGains();
        logData = SmartDashboard.getBoolean("logData", false);
        sensorInput.resetEncoders();
        autoManager.setAutonomousDelay(driverInput.getAutonomousDelay());
        autoManager.setAutoMode(Constants.DO_NOTHING_AUTO);
        autoManager.initAutonomous();
    }

    public void autonomousPeriodic()
    {
        watchdog.feed();
        dashboardManager.updateLCD();
        autoManager.runAutonomous();
        logData();
    }

    public void teleopInit()
    {
        params.load();
        logData = SmartDashboard.getBoolean("logData", false);
        if(logData)
        {
            logging.createNewFile();
            String data = "FrameTime,";
            data += drivebase.getKeyNames();
            data += manipulator.getKeyNames();
            
            logging.logKeyNames(data);
        }
        driverInput.pullJoystickTypes();
        pullNewPIDGains();
    }

    public void teleopPeriodic()
    {
        watchdog.feed();
        drivebase.run();
        manipulator.run();
        dashboardManager.updateLCD();
        logData();
    }
    
    public void disabledInit()
    {
        logData = SmartDashboard.getBoolean("logData", false);
    }
    
    public void disabledPeriodic()
    {
        watchdog.feed();
        dashboardManager.updateLCD();
        if(driverInput.resetSensors())
        {
            sensorInput.resetEncoders();
            sensorInput.resetGyro();
        }
    }
    
    public void initSmartDashboard()
    {
        SmartDashboard.putNumber("Autonomous Delay", 0.0);
        SmartDashboard.putBoolean("logData", true);
        SmartDashboard.putBoolean("firstControllerIsLogitech", true);
        SmartDashboard.putBoolean("secondControllerIsLogitech", false);
    }
    
    public void initLogging()
    {
        logging = TorqueLogging.getInstance();
        logging.setDashboardLogging(logData);
        
        final String loggingString = drivebase.getKeyNames() + manipulator.getKeyNames();
    }
    
    public void pullNewPIDGains()
    {
        manipulator.loadParameters();
        drivebase.loadParameters();
    }
    
    public void logData()
    {
        if(logData)
        {
            String data = dashboardManager.getDS().getMatchTime() + ",";
            data += drivebase.logData();
            data += manipulator.logData();
            
            logging.logData(data);
        }
    }
}
