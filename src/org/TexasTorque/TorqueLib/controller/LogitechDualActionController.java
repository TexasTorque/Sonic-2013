package org.TexasTorque.TorqueLib.controller;

import edu.wpi.first.wpilibj.Joystick;

public class LogitechDualActionController extends Joystick
{
    public LogitechDualActionController(int port){super(port);}
    
    public boolean getButton1(){return this.getRawButton(1);}
    public boolean getButton2(){return this.getRawButton(2);}
    public boolean getButton3(){return this.getRawButton(3);}
    public boolean getButton4(){return this.getRawButton(4);}
    public boolean getButton5(){return this.getRawButton(5);}
    public boolean getButton6(){return this.getRawButton(6);}
    public boolean getButton7(){return this.getRawButton(7);}
    public boolean getButton8(){return this.getRawButton(8);}
    public boolean getButton9(){return this.getRawButton(9);}
    public boolean getButton10(){return this.getRawButton(10);}
    public boolean getButton11(){return this.getRawButton(11);}
    public boolean getButton12(){return this.getRawButton(12);}
    public double getLeftXAxis(){return this.getRawAxis(1);}
    public double getLeftYAxis(){return this.getRawAxis(2);}
    public double getRightXAxis(){return this.getRawAxis(3);}
    public double getRightYAxis(){return this.getRawAxis(4);}
    public double getXDPAD(){return this.getRawAxis(5);}
    public double getYDPAD(){return this.getRawAxis(6);}
}
