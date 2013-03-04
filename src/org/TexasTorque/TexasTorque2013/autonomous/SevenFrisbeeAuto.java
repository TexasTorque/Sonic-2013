package org.TexasTorque.TexasTorque2013.autonomous;

import org.TexasTorque.TorqueLib.controlLoop.SimPID;

public class SevenFrisbeeAuto extends AutonomousBase
{
    
    private SimPID linearPID;
    private SimPID gyroPID;
    
    public SevenFrisbeeAuto()
    {
        super();
        
        linearPID = new SimPID();
        gyroPID = new SimPID();
    }
    
    public void init()
    {
        
    }
    
    public void run()
    {
    }
    
    public void end()
    {
        
    }
}
