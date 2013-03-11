package org.TexasTorque.TexasTorque2013.autonomous;

import org.TexasTorque.TexasTorque2013.constants.Constants;
import org.TexasTorque.TexasTorque2013.autonomous.*;
import org.TexasTorque.TexasTorque2013.autonomous.util.AutonomousStop;
import org.TexasTorque.TexasTorque2013.autonomous.util.AutonomousStopAll;
import org.TexasTorque.TexasTorque2013.subsystem.drivebase.Drivebase;
import org.TexasTorque.TexasTorque2013.subsystem.manipulator.Elevator;
import org.TexasTorque.TexasTorque2013.subsystem.manipulator.Intake;
import org.TexasTorque.TexasTorque2013.subsystem.manipulator.Magazine;
import org.TexasTorque.TexasTorque2013.subsystem.manipulator.Shooter;
import org.TexasTorque.TexasTorque2013.subsystem.manipulator.Tilt;

public class AutonomousManager
{
    private AutonomousBuilder autoBuilder;
    
    private AutonomousCommand[] autoList;
    
    private Drivebase drivebase;
    private Intake intake;
    private Elevator elevator;
    private Magazine magazine;
    private Shooter shooter;
    private Tilt tilt;
    
    private int autoMode;
    private double autoDelay;
    private int currentIndex;
    private boolean firstCycle;
    
    public AutonomousManager()
    {
        autoBuilder = new AutonomousBuilder();
        
        autoList = null;
        
        drivebase = Drivebase.getInstance();
        intake = Intake.getInstance();
        elevator = Elevator.getInstance();
        magazine = Magazine.getInstance();
        shooter = Shooter.getInstance();
        tilt = Tilt.getInstance();
        
        autoMode = Constants.DO_NOTHING_AUTO;
        autoDelay = 0.0;
        currentIndex = 0;
        firstCycle = true;
    }
    
    public void setAutoMode(int mode)
    {
        autoMode = mode;
    }
    
    public void addAutoDelay(double delay)
    {
        autoDelay = delay;
    }
    
    public void loadAutonomous()
    {
        switch(autoMode)
        {
            case Constants.DO_NOTHING_AUTO:
                doNothingAuto();
                break;
            case Constants.REAR_SHOOT_AUTO:
                rearAuto();
                break;
            default:
                doNothingAuto();
                break;
        }
        
        firstCycle = true;
        currentIndex = 0;
        autoList = autoBuilder.getAutonomousList();
    }
    
    public void runAutonomous()
    {
        if(firstCycle)
        {
            firstCycle = false;
            autoList[currentIndex].reset();
        }
        
        boolean commandFinished = autoList[currentIndex].run();
        
        if(commandFinished)
        {
            currentIndex++;
            autoList[currentIndex].reset();
        }
        
        drivebase.run();
        intake.run();
        elevator.run();
        shooter.run();
        magazine.run();
        tilt.run();
    }
    
    public void doNothingAuto()
    {
        autoBuilder.clearCommands();
        autoBuilder.addCommand(new AutonomousStop());
    }
    
    public void rearAuto()
    {
        autoBuilder.clearCommands();
        autoBuilder.addAutonomousDelay(autoDelay);
        autoBuilder.addLowFireSequence(3);
        autoBuilder.addCommand(new AutonomousStopAll());
        autoBuilder.addCommand(new AutonomousStop());
    }
    
    public void sideAuto()
    {
        autoBuilder.clearCommands();
        autoBuilder.addAutonomousDelay(autoDelay);
        
        
        
        autoBuilder.addCommand(new AutonomousStop());
    }
    
    public void sevenFrisbeeAuto()
    {
        autoBuilder.clearCommands();
        autoBuilder.addAutonomousDelay(autoDelay);
        autoBuilder.addLowFireSequence(3);
        
        
        autoBuilder.addCommand(new AutonomousStop());
    }
    
    
}
