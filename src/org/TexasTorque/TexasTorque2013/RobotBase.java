package org.TexasTorque.TexasTorque2013;

import edu.wpi.first.wpilibj.IterativeRobot;
import org.TexasTorque.TexasTorque2013.io.*;
import org.TexasTorque.TexasTorque2013.subsystem.*;

public class RobotBase extends IterativeRobot
{
    
    DriverInput driverInput;
    SensorInput sensorInput;
    RobotOutput robotOutput;
    Drivebase drivebase;
    
    public void robotInit()
    {
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

    }
    
    public void autonomousContinuous()
    {
        
    }

    public void teleopInit()
    {
        
    }

    public void teleopPeriodic()
    {
        
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
        
    }
    
    public void disabledContinuous()
    {
        
    }
}
