package org.TexasTorque.TexasTorque2013;

import edu.wpi.first.wpilibj.SimpleRobot;
import edu.wpi.first.wpilibj.Timer;
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
    
    Timer robotTime;
    
    boolean logData;
    int logCycles;
    double numCycles;
    
    public void robotInit()
    {
        watchdog = getWatchdog();
        watchdog.setEnabled(true);
        watchdog.setExpiration(0.5);
        
        params = Parameters.getInstance();
        params.load();
        
        initSmartDashboard();
        
        logging = TorqueLogging.getInstance();
        logging.setDashboardLogging(logData);
        
        dashboardManager = DashboardManager.getInstance();
        driverInput = DriverInput.getInstance();
        sensorInput = SensorInput.getInstance();
        robotOutput = RobotOutput.getInstance();
        drivebase = Drivebase.getInstance();
        manipulator = Manipulator.getInstance();
        
        autoManager = new AutonomousManager();
        
        driverInput.pullJoystickTypes();
        
        robotTime = new Timer();
        
        logCycles = 0;
        numCycles = 0.0;
    }
    
//---------------------------------------------------------------------------------------------------------------------------------
    
    public void autonomous()
    {
        autonomousInit();
        while(isAutonomous() && isEnabled())
        {
            watchdog.feed();
            autonomousPeriodic();
            robotOutput.runLights();
            logData();
        }
    }

    public void autonomousInit()
    {
        System.err.println("init");
        loadParameters();
        initLogging();
        autoManager.reset();
        sensorInput.resetEncoders();
        autoManager.setAutonomousDelay(driverInput.getAutonomousDelay());
        autoManager.setAutoMode(Constants.REAR_SHOOT_AUTO);
        autoManager.initAutonomous();
    }

    public void autonomousPeriodic()
    {
        System.err.println("periodic");
        dashboardManager.updateLCD();
        autoManager.runAutonomous();
    }
    
//---------------------------------------------------------------------------------------------------------------------------------   
    
    public void operatorControl()
    {
        teleopInit();
        while(isOperatorControl() && isEnabled())
        {
            double previousTime = Timer.getFPGATimestamp();
            
            watchdog.feed();
            teleopPeriodic();
            robotOutput.runLights();
            logData();
            
            numCycles++;
            SmartDashboard.putNumber("NumCycles", numCycles);
            SmartDashboard.putNumber("Hertz", 1.0/(Timer.getFPGATimestamp() - previousTime));
        }
    }

    public void teleopInit()
    {
        loadParameters();
        initLogging();
        
        driverInput.pullJoystickTypes();
        
        manipulator.setLightsNormal();
        
        logCycles = Constants.CYCLES_PER_LOG;
        
        robotTime.reset();
        robotTime.start();
    }

    public void teleopPeriodic()
    {
        drivebase.run();
        manipulator.run();
    }
    
//---------------------------------------------------------------------------------------------------------------------------------
    
    public void disabled()
    {
        disabledInit();
        while(isDisabled())
        {
            watchdog.feed();
            disabledPeriodic();
            robotOutput.runLights();
        }
    }
    
    public void disabledInit()
    {
        robotOutput.setLightsState(Constants.PARTY_MODE);
    }
    
    public void disabledPeriodic()
    {
        if(driverInput.resetSensors())
        {
            sensorInput.resetEncoders();
            sensorInput.resetGyro();
        }
    }
    
//---------------------------------------------------------------------------------------------------------------------------------    
    
    public void initSmartDashboard()
    {
        SmartDashboard.putNumber("Autonomous Delay", 0.0);
        SmartDashboard.putBoolean("logData", false);
        SmartDashboard.putBoolean("firstControllerIsLogitech", Constants.DEFAULT_FIRST_CONTROLLER_TYPE);
        SmartDashboard.putBoolean("secondControllerIsLogitech", Constants.DEFAULT_SECOND_CONTROLLER_TYPE);
    }
    
    public void loadParameters()
    {
        params.load();
        manipulator.loadParameters();
        drivebase.loadParameters();
    }
    
    public void initLogging()
    {
        logData = SmartDashboard.getBoolean("logData", false);
        if(logData)
        {
            String data = "MatchTime,RobotTime,";
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
                data += robotTime.get() + ",";
                data += drivebase.logData();
                data += manipulator.logData();

                logging.logData(data);
                
                logCycles = 0;
            }
            
            logCycles++;
        }
    }
}
