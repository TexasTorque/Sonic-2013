package org.TexasTorque.TexasTorque2013.io;

public class RobotOutput
{
    private static RobotOutput instance;
    
    public RobotOutput()
    {
        
    }
    
    public static RobotOutput getInstance()
    {
        return (instance == null) ? instance = new RobotOutput() : instance;
    }
}
