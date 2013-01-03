package org.TexasTorque.TorqueLib.controller;

import edu.wpi.first.wpilibj.Joystick;

public class TraxxasTQ3 extends Joystick
{
    public int port;
    
    public TraxxasTQ3(int p)
    {
        super(p);
        port = p;
    }
    
    public double getWheel(){ return this.getRawAxis(1); }
    public double getThrottle(){ return this.getRawAxis(2); }
    public double getWheelTrim(){ return this.getRawAxis(3); }
    public double getThrottleTrim(){ return this.getRawAxis(4); }
    public boolean getSwitch(){ return this.getRawButton(2); }
}
