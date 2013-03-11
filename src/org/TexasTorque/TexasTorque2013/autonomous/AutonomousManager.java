package org.TexasTorque.TexasTorque2013.autonomous;

import org.TexasTorque.TexasTorque2013.constants.Constants;
import org.TexasTorque.TexasTorque2013.autonomous.*;
import org.TexasTorque.TexasTorque2013.autonomous.drive.AutonomousDriveStop;
import org.TexasTorque.TexasTorque2013.autonomous.drive.AutonomousDriveStraight;
import org.TexasTorque.TexasTorque2013.autonomous.intake.AutonomousIntake;
import org.TexasTorque.TexasTorque2013.autonomous.intake.AutonomousStopIntake;
import org.TexasTorque.TexasTorque2013.autonomous.magazine.AutonomousMagazineLoad;
import org.TexasTorque.TexasTorque2013.autonomous.magazine.AutonomousMagazineStop;
import org.TexasTorque.TexasTorque2013.autonomous.util.AutonomousStop;
import org.TexasTorque.TexasTorque2013.autonomous.util.AutonomousStopAll;
import org.TexasTorque.TexasTorque2013.subsystem.drivebase.Drivebase;
import org.TexasTorque.TexasTorque2013.subsystem.manipulator.Elevator;
import org.TexasTorque.TexasTorque2013.subsystem.manipulator.Intake;
import org.TexasTorque.TexasTorque2013.subsystem.manipulator.Magazine;
import org.TexasTorque.TexasTorque2013.subsystem.manipulator.Shooter;
import org.TexasTorque.TexasTorque2013.subsystem.manipulator.Tilt;
import org.TexasTorque.TorqueLib.util.Parameters;

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
    private Parameters params;
    
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
        params = Parameters.getInstance();
        
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
            case Constants.SIDE_SHOOT_AUTO:
                sideAuto();
                break;
            case Constants.SEVEN_FRISBEE_AUTO:
                sevenFrisbeeAuto();
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
        double driveSpeed = params.getAsDouble("A_SevenFrisbeeSpeed", 0.5);
        double driveDistance = params.getAsDouble("A_SevenFrisbeeDistance", 100);
        
        autoBuilder.clearCommands();
        autoBuilder.addAutonomousDelay(autoDelay);
        autoBuilder.addLowFireSequence(3);
        autoBuilder.addCommand(new AutonomousIntake());
        autoBuilder.addCommand(new AutonomousMagazineLoad());
        autoBuilder.addCommand(new AutonomousDriveStraight(driveDistance, driveSpeed));
        autoBuilder.addCommand(new AutonomousStopIntake());
        autoBuilder.addCommand(new AutonomousMagazineStop());
        autoBuilder.addCommand(new AutonomousDriveStraight(0.0, driveSpeed));
        autoBuilder.addCommand(new AutonomousDriveStop());
        autoBuilder.addLowFireSequence(4);
        autoBuilder.addCommand(new AutonomousStopAll());
        autoBuilder.addCommand(new AutonomousStop());
    }
    
    
}
