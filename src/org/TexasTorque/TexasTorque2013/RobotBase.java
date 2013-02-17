package org.TexasTorque.TexasTorque2013;

import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.IterativeRobot; 
import edu.wpi.first.wpilibj.Watchdog;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.TexasTorque.TexasTorque2013.autonomous.AutonomousManager;
import org.TexasTorque.TexasTorque2013.constants.Constants;
import org.TexasTorque.TexasTorque2013.io.*;
import org.TexasTorque.TexasTorque2013.subsystem.drivebase.Drivebase;
import org.TexasTorque.TexasTorque2013.subsystem.manipulator.Elevator;
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
        drivebase = Drivebase.getInstance();
        manipulator = Manipulator.getInstance();
        
        autoManager = new AutonomousManager();
        
        driverInput.pullJoystickTypes();
        
        loopTime = 0.0;
    }

    public void autonomousInit()
    {
        pullNewPIDGains();
        logData = SmartDashboard.getBoolean("logData", false);
        logging.setLogging(logData);
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
        logging.setLogging(logData);
        driverInput.pullJoystickTypes();
        pullNewPIDGains();
    }

    public void teleopPeriodic()
    {
        watchdog.feed();
        drivebase.run();
        manipulator.run();
        dashboardManager.updateLCD();
        SmartDashboard.putNumber("Elevator Position", sensorInput.getElevatorEncoder());
        dashboardManager.printToErr(sensorInput.getElevatorEncoder());
        logData();
    }
    
    public void disabledInit()
    {
        logData = SmartDashboard.getBoolean("logData", false);
        logging.setLogging(logData);
    }
    
    public void disabledPeriodic()
    {
        watchdog.feed();
        dashboardManager.updateLCD();
        if(driverInput.getResetEncoders())
        {
            sensorInput.resetEncoders();
            sensorInput.resetGyro();
        }
    }
    
    public void initSmartDashboard()
    {
        SmartDashboard.putNumber("Autonomous Delay", 0.0);
        SmartDashboard.putBoolean("logData", true);
    }
    
    public void initLogging()
    {
        TorqueLogging.setDashboardLogging(false);
        TorqueLogging.setLoopTime(params.getAsInt("LoggingLoopTime", Constants.TORQUE_LOGGING_LOOP_TIME));
        logging = TorqueLogging.getInstance();
        
        final String loggingString =
                "FrameNumber,FrameTime,LoopTime,InOverrideState,"
                + "LeftDriveSpeed,LeftDriveEncoderPosition,LeftDriveEncoderVelocity,RightDriveEncoderPosition,RightDriveSpeed,RightDriveEncoderVelocity,GyroAngle,"
                + "ElevatorMotorSpeed,ActualElevatorPosition,DesiredElevatorPosition,IsLocking,"
                + "IntakeMotorSpeed,"
                + "DesiredTiltAngle,TiltMotorSpeed,ActualTiltAngle,"
                + "DesiredFrontShooterRate,FrontShooterMotorSpeed,ActualFrontShooterRate,DesiredRearShooterRate,RearShooterMotorSpeed,ActualRearShooterRate,"
                + "MagazineFrisbeePosition,MagazineTriggerPosition,CurrentMagazineState,DesiredMagazineState";
        logging.setKeyMapping(loggingString);
        logging.startLogging();
    }
    
    public void pullNewPIDGains()
    {
        manipulator.pullNewPIDGains();
        drivebase.loadGyroPID();
    }
    
    public void logLoopTime()
    {
        double previous = loopTime;
        loopTime = dashboardManager.getDS().getMatchTime();
        logging.logValue("LoopTime", loopTime - previous);
    }
    
    public void logData()
    {
        logging.logValue("FrameTime", dashboardManager.getDS().getMatchTime());
        logLoopTime();
        drivebase.logData();
        manipulator.logData();
    }
}
