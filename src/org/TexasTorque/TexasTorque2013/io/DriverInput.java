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
    private GenericController driveController;
    private GenericController operatorController;
        
    public DriverInput()
    {
        params = Parameters.getInstance();
        driveController = new GenericController(Ports.DRIVE_CONTROLLER_PORT, true);
        operatorController = new GenericController(Ports.OPERATOR_CONTROLLER_PORT, false);
    }
    
    public synchronized static DriverInput getInstance()
    {
        return (instance == null) ? instance = new DriverInput() : instance;
    }
    
    public synchronized void pullJoystickTypes()
    {
        boolean firstType = SmartDashboard.getBoolean("firstControllerIsLogitech", Constants.DEFAULT_FIRST_CONTROLLER_TYPE);
        boolean secondType = SmartDashboard.getBoolean("secondControllerIsLogitech", Constants.DEFAULT_SECOND_CONTROLLER_TYPE);
        
        if(firstType)
        {
            driveController.setAsLogitech();
        }
        else
        {
            driveController.setAsXBox();
        }
        
        if(secondType)
        {
            operatorController.setAsLogitech();
        }
        else
        {
            operatorController.setAsXBox();
        }
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
        return driveController.getLeftYAxis();
    }
    
    public synchronized double getTurn()
    {
        return driveController.getRightXAxis();
    }
    
    public synchronized boolean shiftHighGear()
    {
        return driveController.getTopRightBumper();
    }
    
//---------- Manipulator ----------    
    
    public synchronized boolean runIntake()
    {
        return operatorController.getRightActionButton();
    }
    
    public synchronized boolean reverseIntake()
    {
        return operatorController.getLeftActionButton();
    }
    
    public synchronized boolean shootVisionHigh()
    {
        return operatorController.getTopActionButton();
    }
    
    public synchronized boolean fireFrisbee()
    {
        return operatorController.getBottomRightBumper();
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
        return (operatorController.getLeftYAxis() < -0.5);
    }
    
    public synchronized boolean elevatorBottomOverride()
    {
        return (operatorController.getLeftYAxis() > 0.5);
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
        return (operatorController.getRightYAxis() < -0.2);
    }
    
    public synchronized boolean tiltDownOverride()
    {
        return (operatorController.getRightYAxis() > 0.2);
    }
    
    
}
