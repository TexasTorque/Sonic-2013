package org.TexasTorque.TexasTorque2013.autonomous;

import java.util.Vector;
import org.TexasTorque.TexasTorque2013.autonomous.drive.AutonomousDriveStop;
import org.TexasTorque.TexasTorque2013.autonomous.elevator.AutonomousCustomElevator;
import org.TexasTorque.TexasTorque2013.autonomous.elevator.AutonomousElevatorBottom;
import org.TexasTorque.TexasTorque2013.autonomous.elevator.AutonomousElevatorDone;
import org.TexasTorque.TexasTorque2013.autonomous.magazine.AutonomousFireOnce;
import org.TexasTorque.TexasTorque2013.autonomous.magazine.AutonomousMagazineDone;
import org.TexasTorque.TexasTorque2013.autonomous.shooter.AutonomousShooterDone;
import org.TexasTorque.TexasTorque2013.autonomous.shooter.AutonomousSpinShooter;
import org.TexasTorque.TexasTorque2013.autonomous.shooter.AutonomousStopShooter;
import org.TexasTorque.TexasTorque2013.autonomous.tilt.AutonomousCustomTilt;
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
        System.err.println("getAutonomousList");
        AutonomousCommand[] result = new AutonomousCommand[commands.size()];
        commands.copyInto(result);
        return result;
    }
    
    public void clearCommands()
    {
        commands.removeAllElements();
    }
    
    public void addAutonomousDelay(double seconds)
    {
        addCommand(new AutonomousWait(seconds));
    }
    
    public void addLowFireSequence(double numFires, double delay)
    {
        addCommand(new AutonomousDriveStop());
        addCommand(new AutonomousLowAngle());
        addCommand(new AutonomousSpinShooter());
        
        for(double i = 0; i < numFires; i++)
        {
            addCommand(new AutonomousWait(delay));
            addCommand(new AutonomousTiltDone(2));
            addCommand(new AutonomousShooterDone(2));
            addCommand(new AutonomousMagazineDone(2));
            addCommand(new AutonomousFireOnce());
        }
        
        addCommand(new AutonomousWait(0.5));
        addCommand(new AutonomousTiltParallel());
        addCommand(new AutonomousStopShooter());
        
        addCommand(new AutonomousTiltDone(2));
    }
    
    public void addVariableFireSequence(double numFires, double angle, int elevation, double delay)
    {
        addCommand(new AutonomousDriveStop());
        addCommand(new AutonomousCustomTilt(angle));
        addCommand(new AutonomousSpinShooter());
        addCommand(new AutonomousCustomElevator(elevation));
        
        for(double i = 0; i < numFires; i++)
        {
            addCommand(new AutonomousWait(delay));
            addCommand(new AutonomousTiltDone(2));
            addCommand(new AutonomousShooterDone(2));
            addCommand(new AutonomousElevatorDone(4));
            addCommand(new AutonomousMagazineDone(2));
            addCommand(new AutonomousFireOnce());
        }
        
        addCommand(new AutonomousWait(0.25));
        addCommand(new AutonomousTiltParallel());
        addCommand(new AutonomousStopShooter());
        addCommand(new AutonomousElevatorBottom());
        
        addCommand(new AutonomousTiltDone(2));
        addCommand(new AutonomousElevatorDone(4));
    }
}
