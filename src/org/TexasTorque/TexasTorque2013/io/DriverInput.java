package org.TexasTorque.TexasTorque2013.io;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.TexasTorque.TexasTorque2013.constants.Ports;
import org.TexasTorque.TorqueLib.controller.*;
import org.TexasTorque.TorqueLib.util.Parameters;

public class DriverInput
{
    private static DriverInput instance;
    private Parameters params;
    public TraxxasTQ3 driveController;
    public XBox360Controller operatorController;
        
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
        return SmartDashboard.getNumber("Autonomous Delay", params.getAsDouble("AutonomousDelay", 0.0));
    }
    
    public synchronized boolean shiftHighGear()
    {
        return driveController.getSwitch();
    }
    
    public synchronized boolean getRunIntake()
    {
        return operatorController.getRightBumper();
    }
    
    public synchronized boolean extendWheelyBar()
    {
        return false;
    }
}
