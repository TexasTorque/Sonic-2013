package org.TexasTorque.TorqueLib.controller;

import edu.wpi.first.wpilibj.Joystick;

public class XBox360Controller extends Joystick
{
    public XBox360Controller(int port){ super(port); }
    
    public boolean getAButton(){ return getRawButton(1); }
    public boolean getBButton(){ return getRawButton(2); }
    public boolean getXButton(){ return getRawButton(3); }
    public boolean getYButton(){ return getRawButton(4); }
    public boolean getLeftBumper(){ return getRawButton(5); }
    public boolean getRightBumper(){ return getRawButton(6); }
    public boolean getBackButton(){ return getRawButton(7); }
    public boolean getStartButton(){ return getRawButton(8); }
    public double  getLeftStickX(){ return getRawAxis(1); }
    public double  getLeftStickY(){ return getRawAxis(2); }
    public double  getRightStickX(){ return getRawAxis(4); }
    public double  getRightStickY(){ return getRawAxis(5); }
    public double  getDPADX(){ return getRawAxis(6); }
    public double  getDPADY(){ return getRawAxis(7); }
    public boolean getLeftStickClick(){ return getRawButton(9); }
    public boolean getRightStickClick(){ return getRawButton(10); }
    public double  getTriggers(){ return getRawAxis(3); }
}