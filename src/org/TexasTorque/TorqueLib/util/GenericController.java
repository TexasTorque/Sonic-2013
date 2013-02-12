package org.TexasTorque.TorqueLib.util;

import edu.wpi.first.wpilibj.Joystick;

public class GenericController extends Joystick
{
    
    private boolean isLogitechController;
    
    public GenericController(int port)
    {
        super(port);
        isLogitechController = true;
    }
    
    public GenericController(int port, boolean isLogitech)
    {
        super(port);
        isLogitechController = isLogitech;
    }
    
    public synchronized void setAsLogitech()
    {
        isLogitechController = true;
    }
    
    public synchronized void setAsXBox()
    {
        isLogitechController = false;
    }
    
    public synchronized double getLeftYAxis()
    {
        if(isLogitechController)
        {
            return getRawAxis(2);
        }
        else
        {
            return 0.0;
        }
    }
    
    public synchronized double getLeftXAxis()
    {
        if(isLogitechController)
        {
            return getRawAxis(1);
        }
        else
        {
            return 0.0;
        }
    }
    
    public synchronized double getRightYAxis()
    {
        if(isLogitechController)
        {
            return getRawAxis(4);
        }
        else
        {
            return 0.0;
        }
    }
    
    public synchronized double getRightXAxis()
    {
        return 0.0;
    }
    
    public synchronized boolean getLeftDPAD()
    {
        return false;
    }
    
    public synchronized boolean getRightDPAD()
    {
        return false;
    }
    
    public synchronized boolean getLeftStickClick()
    {
        return false;
    }
    
    public synchronized boolean getRightStickClick()
    {
        return false;
    }
    
    public synchronized boolean getTopLeftBumper()
    {
        return false;
    }
    
    public synchronized boolean getTopRightBumper()
    {
        return false;
    }
    
    public synchronized boolean getBottomLeftBumper()
    {
        return false;
    }
    
    public synchronized boolean getBottomRightBumper()
    {
        return false;
    }
    
    public synchronized boolean getLeftCenterButton()
    {
        return false;
    }
    
    public synchronized boolean getRightCenterButton()
    {
        return false;
    }
    
    public synchronized boolean getLeftActionButton()
    {
        return false;
    }
    
    public synchronized boolean getTopActionButton()
    {
        return false;
    }
    
    public synchronized boolean getRightActionButton()
    {
        return false;
    }
    
    public synchronized boolean getBottomActionButton()
    {
        return false;
    }
}
