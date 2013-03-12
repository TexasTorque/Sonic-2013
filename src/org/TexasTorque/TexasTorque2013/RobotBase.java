package org.TexasTorque.TexasTorque2013;

import edu.wpi.first.wpilibj.IterativeRobot;
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

public class RobotBase extends IterativeRobot implements Runnable
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
        
        (new Thread(new RobotBase())).start();
    }
    
    public void run()
    {
        while(true)
        {
            watchdog.feed();
            if(isAutonomous() && isEnabled())
            {
                autonomousContinuous();
            }
            else if(isOperatorControl() && isEnabled())
            {
                teleopContinuous();
            }
            else if(isDisabled())
            {
                disabledContinuous();
            }
            
            Timer.delay(0.008);
        }
    }
    
//---------------------------------------------------------------------------------------------------------------------------------

    public void autonomousInit()
    {
        loadParameters();
        initLogging();
        
        autoManager.setAutoMode((int) SmartDashboard.getNumber("Autonomous Mode", Constants.DO_NOTHING_AUTO));
        autoManager.addAutoDelay(driverInput.getAutonomousDelay());
        
        sensorInput.resetEncoders();
    }

    public void autonomousPeriodic()
    {
        robotOutput.runLights();
        dashboardManager.updateLCD();
        logData();
        
        SmartDashboard.putNumber("LeftEncoder", sensorInput.getLeftDriveEncoder());
        SmartDashboard.putNumber("RightEncoder", sensorInput.getRightDriveEncoder());
        SmartDashboard.putNumber("GyroAngle", sensorInput.getGyroAngle());
    }
    
    public void autonomousContinuous()
    {
        autoManager.runAutonomous();
    }
    
//---------------------------------------------------------------------------------------------------------------------------------   

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
        robotOutput.runLights();
        dashboardManager.updateLCD();
        logData();
    }
    
    public void teleopContinuous()
    {
        double previousTime = Timer.getFPGATimestamp();
        
        drivebase.run();
        manipulator.run();
        
        numCycles++;
        SmartDashboard.putNumber("NumCycles", numCycles);
        SmartDashboard.putNumber("Hertz", 1.0/(Timer.getFPGATimestamp() - previousTime));
        SmartDashboard.putNumber("GyroAngle", sensorInput.gyro.getAngle());
        System.err.println(1.0/(Timer.getFPGATimestamp() - previousTime));
    }
    
//---------------------------------------------------------------------------------------------------------------------------------

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
        robotOutput.runLights();
        dashboardManager.updateLCD();
    }
    
    public void disabledContinuous()
    {
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
