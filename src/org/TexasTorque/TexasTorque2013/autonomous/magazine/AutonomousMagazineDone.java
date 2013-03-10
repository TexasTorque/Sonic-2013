package org.TexasTorque.TexasTorque2013.autonomous.magazine;

import org.TexasTorque.TexasTorque2013.autonomous.AutonomousCommand;

public class AutonomousMagazineDone extends AutonomousCommand
{
    public AutonomousMagazineDone()
    {
        super();
    }
    
    public void reset()
    {
    }
    
    public boolean run()
    {
        boolean magazineDone = magazine.getIsWaiting();
        return magazineDone;
    }
}
