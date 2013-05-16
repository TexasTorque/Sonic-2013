package org.TexasTorque.TexasTorque2013.io;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.TexasTorque.TexasTorque2013.constants.Constants;
import org.TexasTorque.TexasTorque2013.constants.Ports;
import org.TexasTorque.TorqueLib.util.GenericController;
import org.TexasTorque.TorqueLib.util.Parameters;

public class DriverInput
{
    private static DriverInput instance;
    private Parameters params;
    public GenericController driveController;
    private GenericController operatorController;
    
    private boolean inOverrideState;
        
    public DriverInput()
    {
        params = Parameters.getTeleopInstance();
        driveController = new GenericController(Ports.DRIVE_CONTROLLER_PORT, Constants.DEFAULT_FIRST_CONTROLLER_TYPE);
        operatorController = new GenericController(Ports.OPERATOR_CONTROLLER_PORT, Constants.DEFAULT_SECOND_CONTROLLER_TYPE);
        
        inOverrideState = false;
    }
    
    public synchronized static DriverInput getInstance()
    {
        return (instance == null) ? instance = new DriverInput() : instance;
    }
    
    public synchronized void pullJoystickTypes()
    {
        boolean firstControllerType = SmartDashboard.getBoolean("firstControllerIsLogitech", Constants.DEFAULT_FIRST_CONTROLLER_TYPE);
        boolean secondControllerType = SmartDashboard.getBoolean("secondControllerIsLogitech", Constants.DEFAULT_SECOND_CONTROLLER_TYPE);
        
        if(firstControllerType)
        {
            driveController.setAsLogitech();
        }
        else
        {
            driveController.setAsXBox();
        }
        
        if(secondControllerType)
        {
            operatorController.setAsLogitech();
        }
        else
        {
            operatorController.setAsXBox();
        }
    }
    
    public synchronized double getAutonomousDelay()
    {
        return SmartDashboard.getNumber("Autonomous Delay", 0.0);
    }
    
    public synchronized int getAutonomousMode()
    {
        return (int) SmartDashboard.getNumber("AutonomousMode", Constants.DO_NOTHING_AUTO);
    }
    
    public synchronized boolean resetSensors()
    {
        return operatorController.getBottomActionButton();
    }
    
//---------- Drivebase ----------
    
    public synchronized double getThrottle()
    {
        return -1 * driveController.getLeftYAxis();
    }
    
    public synchronized double getTurn()
    {
        return driveController.getRightXAxis();
    }
    
    public synchronized boolean shiftHighGear()
    {
        return driveController.getTopLeftBumper();
    }
    
//---------- Manipulator ----------    
    
    public synchronized boolean runIntake()
    {
        return operatorController.getTopLeftBumper();
    }
    
    public synchronized boolean reverseIntake()
    {
        return operatorController.getTopRightBumper();
    }
    
    public synchronized boolean restoreToDefault()
    {
        return operatorController.getBottomLeftBumper();
    }
    
    public synchronized boolean shootHigh()
    {
        return operatorController.getRightActionButton();
    }
    
    public synchronized boolean shootSide()
    {
        return false;//return operatorController.getBottomActionButton();
    }
    
    public synchronized boolean shootLow()
    {
        return operatorController.getLeftActionButton();
    }
    
    public synchronized boolean shootFar()
    {
        return operatorController.getTopActionButton();
    }
    
    public synchronized boolean shootClose()
    {
        return operatorController.getTopActionButton();
    }
    
    public synchronized boolean fireFrisbee()
    {
        return operatorController.getBottomRightBumper();
    }
    
    public synchronized boolean gotoSlotHeight()
    {
        return operatorController.getLeftStickClick();
    }
    
    public synchronized boolean incrementAngle()
    {
        return operatorController.getRightDPAD();
    }
    
    public synchronized boolean decrementAngle()
    {
        return operatorController.getLeftDPAD();
    }
    
    public synchronized boolean getAutoTargeting()
    {
        return operatorController.getBottomActionButton(); // Need to know what control to put it on.
    }
    
    public synchronized boolean passiveHang()
    {
        return operatorController.getRightStickClick();
    }
    
//---------- Overrides ----------
    
    public synchronized boolean overrideState()
    {
        if(operatorController.getLeftCenterButton())
        {
            inOverrideState = true;
        }
        else if(operatorController.getRightCenterButton())
        {
            inOverrideState = false;
        }
        
        return inOverrideState;
    }
    
    public synchronized boolean intakeOverride()
    {
        return operatorController.getTopLeftBumper();
    }
    
    public synchronized boolean outtakeOverride()
    {
        return operatorController.getTopRightBumper();
    }
    
    public synchronized boolean tiltUpOverride()
    {
        return (operatorController.getRightYAxis() < -0.5);
    }
    
    public synchronized boolean tiltDownOverride()
    {
        return (operatorController.getRightYAxis() > 0.5);
    }
    
    public synchronized boolean shootLowOverride()
    {
        return operatorController.getLeftActionButton();
    }
    
    public synchronized boolean shootSideOverride()
    {
        return operatorController.getBottomActionButton();
    }
    
    public synchronized boolean shootFarOverride()
    {
        return false;
    }
    
    public synchronized boolean magazineShootOverride()
    {
        return operatorController.getBottomRightBumper();
    }
    
    public synchronized boolean restoreToDefaultOverride()
    {
        return operatorController.getBottomLeftBumper();
    }
    
    public synchronized boolean elevatorTopOverride()
    {
        return (operatorController.getLeftYAxis() < -0.5);
    }
    
    public synchronized boolean elevatorBottomOverride()
    {
        return (operatorController.getLeftYAxis() > 0.5);
    }
    
    public synchronized double getElevatorJoystick()
    {
        return (operatorController.getLeftYAxis());
    }
    
    public synchronized boolean getMadtownUnjam()
    {
        return operatorController.getTopActionButton();
    }
    
    public synchronized boolean fireUnjam()
    {
        return operatorController.getLeftStickClick();
    }
}
