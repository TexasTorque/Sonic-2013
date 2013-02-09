package org.TexasTorque.TorqueLib.controller;

import edu.wpi.first.wpilibj.Joystick;

public class SpektrumDX2E extends Joystick
{
    public int port;
    
    public SpektrumDX2E(int p)
    {
        super(p);
        port = p;
    }
    
    public double getWheel(){ return this.getRawAxis(3); }
    public double getThrottle(){ return this.getRawAxis(2); }
    public boolean getSwitch(){ return this.getRawButton(5); }
}
