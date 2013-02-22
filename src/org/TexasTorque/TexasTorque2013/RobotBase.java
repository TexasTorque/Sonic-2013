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
    
    boolean logData;
    int logCycles;
    
    public void robotInit()
    {
        watchdog = getWatchdog();
        watchdog.setEnabled(true);
        watchdog.setExpiration(0.2);
        
        params = Parameters.getInstance();
        params.load();
        
        initSmartDashboard();
        
        logging = TorqueLogging.getInstance();
        logging.setDashboardLogging(logData);
        
        dashboardManager = DashboardManager.getInstance();
        driverInput = DriverInput.getInstance();
        sensorInput = SensorInput.getInstance();
        robotOutput = RobotOutput.getInstance();
        drivebase = (Drivebase) Drivebase.getInstance();
        manipulator = (Manipulator) Manipulator.getInstance();
        
        autoManager = new AutonomousManager();
        
        driverInput.pullJoystickTypes();
        
        logCycles = 0;
    }
    
    public void autonomous()
    {
        autonomousInit();
        while(isAutonomous())
        {
            watchdog.feed();
            autonomousPeriodic();
            dashboardManager.updateLCD();
        }
    }
    
    public void operatorControl()
    {
        teleopInit();
        while(isOperatorControl() && isEnabled())
        {
            watchdog.feed();
            teleopPeriodic();
            dashboardManager.updateLCD();
        }
    }
    
    public void disabled()
    {
        disabledInit();
        while(isDisabled())
        {
            watchdog.feed();
            disabledPeriodic();
            dashboardManager.updateLCD();
        }
    }

    public void autonomousInit()
    {
        params.load();
        loadParameters();
        initLogging();
        sensorInput.resetEncoders();
        autoManager.setAutonomousDelay(driverInput.getAutonomousDelay());
        autoManager.setAutoMode(Constants.DO_NOTHING_AUTO);
        autoManager.initAutonomous();
    }

    public void autonomousPeriodic()
    {
        dashboardManager.updateLCD();
        autoManager.runAutonomous();
        logData();
    }

    public void teleopInit()
    {
        params.load();
        loadParameters();
        initLogging();
        driverInput.pullJoystickTypes();
        
        logCycles = Constants.CYCLES_PER_LOG;
    }

    public void teleopPeriodic()
    {
        drivebase.run();
        manipulator.run();
        logData();
    }
    
    public void disabledInit()
    {
    }
    
    public void disabledPeriodic()
    {
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
        SmartDashboard.putBoolean("logData", false);
        SmartDashboard.putBoolean("firstControllerIsLogitech", Constants.DEFAULT_FIRST_CONTROLLER_TYPE);
        SmartDashboard.putBoolean("secondControllerIsLogitech", Constants.DEFAULT_SECOND_CONTROLLER_TYPE);
    }
    
    public void loadParameters()
    {
        manipulator.loadParameters();
        drivebase.loadParameters();
    }
    
    public void initLogging()
    {
        logData = SmartDashboard.getBoolean("logData", false);
        if(logData)
        {
            logging.createNewFile();
            String data = "FrameTime,";
            data += drivebase.getKeyNames();
            data += manipulator.getKeyNames();
            
            logging.logKeyNames(data);
        }
    }
    
    public void logData()
    {
        if(logData)
        {
            if(logCycles  == Constants.CYCLES_PER_LOG)
            {
                String data = dashboardManager.getDS().getMatchTime() + ",";
                data += drivebase.logData();
                data += manipulator.logData();

                logging.logData(data);
                
                logCycles = 0;
            }
            
            logCycles++;
        }
    }
}
