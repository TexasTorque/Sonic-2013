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
import org.TexasTorque.TexasTorque2013.subsystem.manipulator.Tilt;
import org.TexasTorque.TorqueLib.util.DashboardManager;
import org.TexasTorque.TorqueLib.util.Parameters;
import org.TexasTorque.TorqueLib.util.TorqueLogging;

public class RobotBase extends IterativeRobot implements Runnable
{
    
    Thread continuousThread;
    
    Watchdog watchdog;
    Parameters params;
    TorqueLogging logging;
    DashboardManager dashboardManager;
    DriverInput driverInput;
    SensorInput sensorInput;
    RobotOutput robotOutput;
    Drivebase drivebase;
    Manipulator  manipulator;
    Tilt tilt;
    
    AutonomousManager autoManager;
    
    Timer robotTime;
    
    boolean logData;
    int logCycles;
    double numCycles;
    double previousTime;
    
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
        tilt = Tilt.getInstance();
        
        autoManager = new AutonomousManager();
        
        driverInput.pullJoystickTypes();
        
        robotTime = new Timer();
        
        logCycles = 0;
        numCycles = 0.0;
        
        //(new Thread(this)).start();
        
        continuousThread = new Thread(this);
        continuousThread.start();
        
    }
    
    public void run()
    {
        
        previousTime = Timer.getFPGATimestamp();
        
        while(true)
        {
            watchdog.feed();
            if(isAutonomous() && isEnabled())
            {
                autonomousContinuous();
                Timer.delay(0.004);
            }
            else if(isOperatorControl() && isEnabled())
            {
                teleopContinuous();
                Timer.delay(0.004);
            }
            else if(isDisabled())
            {
                disabledContinuous();
                Timer.delay(0.05);
            }
            
            sensorInput.calcEncoders();
            
            //double currentTime = Timer.getFPGATimestamp();
            //SmartDashboard.putNumber("Hertz", 1 / (currentTime - previousTime));
            //previousTime = currentTime;
            
            numCycles++;
            
        }
    }
    
//---------------------------------------------------------------------------------------------------------------------------------

    public void autonomousInit()
    {
        loadParameters();
        initLogging();
        
        autoManager.setAutoMode((int) SmartDashboard.getNumber("AutonomousMode", Constants.DO_NOTHING_AUTO));
        autoManager.addAutoDelay(driverInput.getAutonomousDelay());
        autoManager.reset();
        autoManager.loadAutonomous();
        
        sensorInput.resetEncoders();
    }

    public void autonomousPeriodic()
    {
        watchdog.feed();
        
        robotOutput.runLights();
        dashboardManager.updateLCD();
        logData();
        
        drivebase.setToRobot();
        manipulator.setToRobot();
        
        /*SmartDashboard.putNumber("TiltAngle", sensorInput.getTiltAngle());
        SmartDashboard.putNumber("FrontRate", sensorInput.getFrontShooterRate());
        SmartDashboard.putNumber("MiddleRate", sensorInput.getMiddleShooterRate());
        SmartDashboard.putNumber("RearRate", sensorInput.getRearShooterRate());*/
        
        SmartDashboard.putNumber("Distance", (sensorInput.getLeftDriveEncoder() + sensorInput.getRightDriveEncoder()) / 2.0);
        SmartDashboard.putNumber("GyroAngle", sensorInput.getGyroAngle());
        
        SmartDashboard.putNumber("NumCycles", numCycles);
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
        watchdog.feed();
        robotOutput.runLights();
        
        logData();
        
        tilt.run();
        
        drivebase.setToRobot();
        manipulator.setToRobot();
        
        SmartDashboard.putNumber("NumCycles", numCycles);
    }
    
    public void teleopContinuous()
    {   
        drivebase.run();
        manipulator.run();
    }
    
//---------------------------------------------------------------------------------------------------------------------------------

    public void disabledInit()
    {
        robotOutput.setLightsState(Constants.PARTY_MODE);
        robotOutput.runLights();
    }
    
    public void disabledPeriodic()
    {
        watchdog.feed();
        
        if(driverInput.resetSensors())
        {
            sensorInput.resetEncoders();
            sensorInput.resetGyro();
        }
        dashboardManager.updateLCD();
        
        //SmartDashboard.putNumber("NumCycles", numCycles);
    }
    
    public void disabledContinuous()
    {
    }
    
//---------------------------------------------------------------------------------------------------------------------------------    
    
    public void initSmartDashboard()
    {
        SmartDashboard.putNumber("Autonomous Delay", 0.0);
        SmartDashboard.putNumber("AutonomousMode", Constants.REAR_SHOOT_AUTO);
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
