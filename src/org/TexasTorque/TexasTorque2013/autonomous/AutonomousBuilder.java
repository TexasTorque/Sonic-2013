package org.TexasTorque.TexasTorque2013.autonomous;

import java.util.Vector;
import org.TexasTorque.TexasTorque2013.autonomous.drive.AutonomousDriveStop;
import org.TexasTorque.TexasTorque2013.autonomous.magazine.AutonomousFireOnce;
import org.TexasTorque.TexasTorque2013.autonomous.shooter.AutonomousShooterDone;
import org.TexasTorque.TexasTorque2013.autonomous.shooter.AutonomousSpinShooter;
import org.TexasTorque.TexasTorque2013.autonomous.shooter.AutonomousStopShooter;
import org.TexasTorque.TexasTorque2013.autonomous.tilt.AutonomousLowAngle;
import org.TexasTorque.TexasTorque2013.autonomous.tilt.AutonomousTiltDone;
import org.TexasTorque.TexasTorque2013.autonomous.tilt.AutonomousTiltParallel;
import org.TexasTorque.TexasTorque2013.autonomous.util.AutonomousWait;

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
    
    public void addAutonomousDelay(double seconds)
    {
        addCommand(new AutonomousWait(seconds));
    }
    
    public void addLowFireSequence(double numFires)
    {
        addCommand(new AutonomousDriveStop());
        addCommand(new AutonomousLowAngle());
        addCommand(new AutonomousSpinShooter());
        addCommand(new AutonomousTiltDone());
        addCommand(new AutonomousShooterDone());
        
        for(double i = 0; i < numFires; i++)
        {
            addCommand(new AutonomousFireOnce());
            addCommand(new AutonomousTiltDone());
            addCommand(new AutonomousShooterDone());
        }
        
        addCommand(new AutonomousTiltParallel());
        addCommand(new AutonomousStopShooter());
        addCommand(new AutonomousTiltDone());
    }
}
