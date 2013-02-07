package org.TexasTorque.TexasTorque2013.io;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.TexasTorque.TexasTorque2013.constants.Ports;
import org.TexasTorque.TorqueLib.controller.*;
import org.TexasTorque.TorqueLib.util.Parameters;

public class DriverInput
{
    private static DriverInput instance;
    private Parameters params;
    private TraxxasTQ3 driveController;
    private XBox360Controller operatorController;
        
    public DriverInput()
    {
        params = Parameters.getInstance();
        driveController = new TraxxasTQ3(Ports.DRIVE_CONTROLLER_PORT);
        operatorController = new XBox360Controller(Ports.OPERATOR_CONTROLLER_PORT);
    }
    
    public synchronized static DriverInput getInstance()
    {
        return (instance == null) ? instance = new DriverInput() : instance;
    }
    
    public synchronized double applyDeadband(double axisValue, double deadband)
    {
        if(Math.abs(axisValue) <= deadband)
        {
            return 0.0;
        }
        else
        {
            return axisValue;
        }
    }
    
    public synchronized double getAutonomousDelay()
    {
        return SmartDashboard.getNumber("Autonomous Delay", params.getAsDouble("Misc_AutonomousDelay", 0.0));
    }
    
//---------- Drivebase ----------
    
    public synchronized double getThrottle()
    {
        return driveController.getThrottle();
    }
    
    public synchronized double getTurn()
    {
        return driveController.getWheel();
    }
    
    public synchronized boolean shiftHighGear()
    {
        return driveController.getSwitch();
    }
    
//---------- Manipulator ----------    
    
    public synchronized boolean runIntake()
    {
        return false;
    }
    
    public synchronized boolean reverseIntake()
    {
        return false;
    }
    
    public synchronized boolean extendWheelyBar()
    {
        return false;
    }
    
    public synchronized boolean shootVisionHigh()
    {
        return false;
    }
    
//---------- Overrides ----------
    
    public synchronized boolean override()
    {
        return (intakeOverride() || outtakeOverride() || elevatorTopOverride() || elevatorBottomOverride() || shooterOverride());
    }
    
    public synchronized boolean intakeOverride()
    {
        return false;
    }
    
    public synchronized boolean outtakeOverride()
    {
        return false;
    }
    
    public synchronized boolean elevatorTopOverride()
    {
        return false;
    }
    
    public synchronized boolean elevatorBottomOverride()
    {
        return false;
    }
    
    public synchronized boolean shooterOverride()
    {
        return false;
    }
    
}
