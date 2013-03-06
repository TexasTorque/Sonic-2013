package org.TexasTorque.TexasTorque2013.autonomous;

import java.util.Vector;

public class AutonomousBuilder
{
    private Vector commands;
    
    public AutonomousBuilder()
    {
        commands = new Vector();
    }
    
    public void addCommand(AutonomousCommand cmd)
    {
        commands.addElement(cmd);
    }
    
    public AutonomousCommand[] getAutonomousList()
    {
        AutonomousCommand[] result = new AutonomousCommand[commands.size()];
        commands.copyInto(result);
        return result;
    }
}
