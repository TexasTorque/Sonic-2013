package org.TexasTorque.TexasTorque2013.io;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.TexasTorque.TexasTorque2013.constants.Ports;
import org.TexasTorque.TorqueLib.controller.*;
import org.TexasTorque.TorqueLib.util.Parameters;

public class DriverInput
{
    private static DriverInput instance;
    private Parameters params;
    private SpektrumDX2E driveController;
    private XBox360Controller operatorController;
        
    public DriverInput()
    {
        params = Parameters.getInstance();
        driveController = new SpektrumDX2E(Ports.DRIVE_CONTROLLER_PORT);
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
        return operatorController.getBButton();
    }
    
    public synchronized boolean reverseIntake()
    {
        return operatorController.getXButton();
    }
    
    public synchronized boolean shootVisionHigh()
    {
        return operatorController.getYButton();
    }
    
    public synchronized boolean fireFrisbee()
    {
        return (operatorController.getTriggers() < -0.2);
    }
    
//---------- Overrides ----------
    
    public synchronized boolean override()
    {
        return (intakeOverride() || outtakeOverride() || elevatorTopOverride() || elevatorBottomOverride() || shooterOverride() || magazineShootOverride() || tiltUpOverride() || tiltDownOverride());
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
    
    public synchronized boolean magazineShootOverride()
    {
        return false;
    }
    
    public synchronized boolean tiltUpOverride()
    {
        return false;
    }
    
    public synchronized boolean tiltDownOverride()
    {
        return false;
    }
    
}
