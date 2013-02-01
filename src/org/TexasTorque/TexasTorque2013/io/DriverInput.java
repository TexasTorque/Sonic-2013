package org.TexasTorque.TexasTorque2013.io;

import org.TexasTorque.TexasTorque2013.constants.Ports;
import org.TexasTorque.TorqueLib.controller.*;

public class DriverInput
{
    private static DriverInput instance;
    public TraxxasTQ3 driveController;
    public XBox360Controller operatorController;
        
    public DriverInput()
    {
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
    
    public synchronized boolean getHighGear()
    {
        return driveController.getSwitch();
    }
    
    public synchronized boolean getLowGear()
    {
        return !driveController.getSwitch();
    }
    
    public synchronized boolean getRunIntake()
    {
        return operatorController.getRightBumper();
    }
}
