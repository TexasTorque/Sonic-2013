package org.TexasTorque.TorqueLib.controller;

import edu.wpi.first.wpilibj.Joystick;

public class RCController extends Joystick
{
    private int port;
    
    public RCController(int p)
    {
        super(p);
        port = p;
    }
    
    public double getThrottle(){ return getRawAxis(2);}
    public double getWheel(){ return getRawAxis(1); }
    public boolean getToggleSwitch(){ return getRawButton(2); }
    
}
