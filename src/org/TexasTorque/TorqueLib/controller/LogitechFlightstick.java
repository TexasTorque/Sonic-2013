package org.TexasTorque.TorqueLib.controller;

import edu.wpi.first.wpilibj.Joystick;

public class LogitechFlightstick extends Joystick
{
    public LogitechFlightstick(short port){ super(port);}
    
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
    public double getXAxis(){return this.getX();}
    public double getYAxis(){return this.getY();}
    public double getZAxis(){return this.getZ();}
}
