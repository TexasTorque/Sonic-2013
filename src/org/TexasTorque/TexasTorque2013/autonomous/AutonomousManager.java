package org.TexasTorque.TexasTorque2013.autonomous;

import org.TexasTorque.TexasTorque2013.autonomous.drive.AutonomousDriveStop;
import org.TexasTorque.TexasTorque2013.autonomous.drive.AutonomousDriveStraight;
import org.TexasTorque.TexasTorque2013.autonomous.drive.AutonomousPivotTurn;
import org.TexasTorque.TexasTorque2013.autonomous.drive.AutonomousShiftLow;
import org.TexasTorque.TexasTorque2013.autonomous.intake.AutonomousIntake;
import org.TexasTorque.TexasTorque2013.autonomous.intake.AutonomousOuttake;
import org.TexasTorque.TexasTorque2013.autonomous.intake.AutonomousStopIntake;
import org.TexasTorque.TexasTorque2013.autonomous.magazine.AutonomousMagazineLoad;
import org.TexasTorque.TexasTorque2013.autonomous.magazine.AutonomousMagazineStop;
import org.TexasTorque.TexasTorque2013.autonomous.util.AutonomousResetEncoders;
import org.TexasTorque.TexasTorque2013.autonomous.util.AutonomousResetGyro;
import org.TexasTorque.TexasTorque2013.autonomous.util.AutonomousStop;
import org.TexasTorque.TexasTorque2013.autonomous.util.AutonomousStopAll;
import org.TexasTorque.TexasTorque2013.autonomous.util.AutonomousWait;
import org.TexasTorque.TexasTorque2013.constants.Constants;
import org.TexasTorque.TexasTorque2013.subsystem.drivebase.Drivebase;
import org.TexasTorque.TexasTorque2013.subsystem.manipulator.Climber;
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
    private Climber climber;
    private Parameters params;
    
    private int autoMode;
    private double autoDelay;
    private int currentIndex;
    private boolean firstCycle;
    private boolean loaded;
    
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
        climber = Climber.getInstance();
        params = Parameters.getInstance();
        
        autoMode = Constants.DO_NOTHING_AUTO;
        autoDelay = 0.0;
        currentIndex = 0;
        firstCycle = true;
        loaded = false;
    }
    
    public void reset()
    {
        loaded = false;
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
            case Constants.MIDDLE_THREE_AUTO:
                rearAuto();
                break;
            case Constants.SIDE_THREE_AUTO:
                sideAuto();
                break;
            case Constants.MIDDLE_SEVEN_AUTO:
                sevenFrisbeeAuto();
                break;
            case Constants.RIGHT_SEVEN_AUTO:
                centerLineAuto();
                break;
            case Constants.RIGHT_THREE_DRIVE_AUTO:
                sideDriveAuto();
                break;
            case Constants.RIGHT_THREE_PRELOAD_AUTO:
                rightThreePreload();
                break;
            case Constants.RIGHT_THREE_PRIMER_AUTO:
                rightThreePrimer();
                break;
            default:
                doNothingAuto();
                break;
        }
        
        firstCycle = true;
        currentIndex = 0;
        autoList = autoBuilder.getAutonomousList();
        loaded = true;
    }
    
    public void runAutonomous()
    {
        if(loaded)
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
            climber.run();
        }
    }
    
    public void doNothingAuto()
    {
        autoBuilder.clearCommands();
        autoBuilder.addCommand(new AutonomousMagazineStop());
        autoBuilder.addCommand(new AutonomousStop());
    }
    
    public void rearAuto()
    {
        autoBuilder.clearCommands();
        autoBuilder.addAutonomousDelay(autoDelay);
        autoBuilder.addLowFireSequence(3, 1.0);
        autoBuilder.addCommand(new AutonomousStopAll());
        autoBuilder.addCommand(new AutonomousStop());
    }
    
    public void sideAuto()
    {
        double sideTiltAngle = params.getAsDouble("A_SideAutoAngle", Tilt.lowAngle);
        
        autoBuilder.clearCommands();
        autoBuilder.addAutonomousDelay(autoDelay);
        autoBuilder.addVariableFireSequence(3, sideTiltAngle, Elevator.elevatorBottomPosition, 1.0);
        autoBuilder.addCommand(new AutonomousStop());
    }
    
    public void sevenFrisbeeAuto()
    {
        double driveSpeed = params.getAsDouble("A_SevenFrisbeeSpeed", 0.5);
        double driveDistance = params.getAsDouble("A_SevenFrisbeeDistance", 100);
        double timeout = params.getAsDouble("A_SevenFrisbeeTimeout", 5.0);
        
        autoBuilder.clearCommands();
        autoBuilder.addAutonomousDelay(autoDelay);
        autoBuilder.addCommand(new AutonomousShiftLow());
        autoBuilder.addLowFireSequence(4, 0.0);
        autoBuilder.addCommand(new AutonomousIntake());
        autoBuilder.addCommand(new AutonomousMagazineLoad());
        autoBuilder.addCommand(new AutonomousDriveStraight(driveDistance, driveSpeed, timeout));
        autoBuilder.addCommand(new AutonomousDriveStop());
        autoBuilder.addCommand(new AutonomousMagazineStop());
        autoBuilder.addCommand(new AutonomousStopIntake());
        autoBuilder.addCommand(new AutonomousDriveStraight(-3.0, driveSpeed + 0.2, 3.0));
        autoBuilder.addCommand(new AutonomousDriveStop());
        autoBuilder.addLowFireSequence(4, 0.0);
        autoBuilder.addCommand(new AutonomousStopAll());
        autoBuilder.addCommand(new AutonomousStop());
    }
    
    public void centerLineAuto()
    {
        double sideTiltAngle = params.getAsDouble("A_SideAutoAngle", Tilt.lowAngle);
        double reverseDistance = params.getAsDouble("A_CenterLineReverseDistance", 100);
        double firstTurnAngle = params.getAsDouble("A_CenterLineFirstAngle", 35.0);
        double secondTurnAngle = params.getAsDouble("A_CenterLineSecondAngle", 90.0);
        double centerLineDistance = params.getAsDouble("A_CenterLineDistance", 100);
        double centerReverse = params.getAsDouble("A_CenterLineCenterReverse", 50);
        double centerLineTiltAngle = params.getAsDouble("A_CenterLineTiltAngle", Tilt.lowAngle);
        double pivotLeftSpeed = params.getAsDouble("A_CenterLinePivotLeftSpeed", 0.5);
        double pivotRightSpeed = params.getAsDouble("A_CenterLinePivotRightSpeed", -0.5);
        
        autoBuilder.clearCommands();
        autoBuilder.addAutonomousDelay(autoDelay);
        autoBuilder.addCommand(new AutonomousShiftLow());
        autoBuilder.addVariableFireSequence(3, sideTiltAngle, Elevator.elevatorBottomPosition, 0.0);
        autoBuilder.addCommand(new AutonomousDriveStraight(reverseDistance, 0.7, 2.0));
        autoBuilder.addCommand(new AutonomousPivotTurn(firstTurnAngle, -pivotLeftSpeed, pivotRightSpeed, 2.5));
        autoBuilder.addCommand(new AutonomousWait(0.25));
        autoBuilder.addCommand(new AutonomousResetEncoders());
        autoBuilder.addCommand(new AutonomousResetGyro());
        autoBuilder.addCommand(new AutonomousMagazineLoad());
        autoBuilder.addCommand(new AutonomousIntake());
        autoBuilder.addCommand(new AutonomousDriveStraight(centerLineDistance, 0.7, 3.25));
        autoBuilder.addCommand(new AutonomousResetEncoders());
        autoBuilder.addCommand(new AutonomousDriveStraight(centerReverse, 0.7, 1.25));
        autoBuilder.addCommand(new AutonomousMagazineStop());
        autoBuilder.addCommand(new AutonomousPivotTurn(secondTurnAngle, pivotLeftSpeed, -pivotRightSpeed, 5.0));
        autoBuilder.addCommand(new AutonomousDriveStop());
        autoBuilder.addCommand(new AutonomousOuttake());
        autoBuilder.addVariableFireSequence(6, centerLineTiltAngle, Elevator.elevatorBottomPosition, 0.0);
        autoBuilder.addCommand(new AutonomousStopAll());
        autoBuilder.addCommand(new AutonomousStop());
    }
    
    public void sideDriveAuto()
    {
        double sideTiltAngle = params.getAsDouble("A_SideAutoAngle", Tilt.lowAngle);
        double distance = params.getAsDouble("A_SideAutoDistance", -115);
        double speed = params.getAsDouble("A_SideAutoSpeed", 0.7);
        
        autoBuilder.clearCommands();
        autoBuilder.addAutonomousDelay(autoDelay);
        autoBuilder.addCommand(new AutonomousShiftLow());
        autoBuilder.addVariableFireSequence(4, sideTiltAngle, Elevator.elevatorBottomPosition, 1.0);
        autoBuilder.addCommand(new AutonomousResetGyro());
        autoBuilder.addCommand(new AutonomousWait(0.25));
        autoBuilder.addCommand(new AutonomousDriveStraight(distance, speed, 5.0));
        autoBuilder.addCommand(new AutonomousStopAll());
        autoBuilder.addCommand(new AutonomousStop());
    }
    
    public void rightThreePreload()
    {
        double sideTiltAngle = params.getAsDouble("A_SideAutoAngle", Tilt.lowAngle);
        double reverseDistance = params.getAsDouble("A_CenterLineReverseDistance", 100);
        double firstTurnAngle = params.getAsDouble("A_CenterLineFirstAngle", 35.0);
        double secondTurnAngle = params.getAsDouble("A_CenterLineSecondAngle", 90.0);
        double centerLineDistance = params.getAsDouble("A_CenterLineDistance", 100);
        double centerReverse = params.getAsDouble("A_CenterLineCenterReverse", 50);
        double pivotLeftSpeed = params.getAsDouble("A_CenterLinePivotLeftSpeed", 0.5);
        double pivotRightSpeed = params.getAsDouble("A_CenterLinePivotRightSpeed", -0.5);
        
        autoBuilder.clearCommands();
        autoBuilder.addAutonomousDelay(autoDelay);
        autoBuilder.addCommand(new AutonomousShiftLow());
        autoBuilder.addVariableFireSequence(3, sideTiltAngle, Elevator.elevatorBottomPosition, 0.0);
        autoBuilder.addCommand(new AutonomousDriveStraight(reverseDistance, 0.7, 2.0));
        autoBuilder.addCommand(new AutonomousPivotTurn(firstTurnAngle, -pivotLeftSpeed, pivotRightSpeed, 2.5));
        autoBuilder.addCommand(new AutonomousWait(0.25));
        autoBuilder.addCommand(new AutonomousResetEncoders());
        autoBuilder.addCommand(new AutonomousResetGyro());
        autoBuilder.addCommand(new AutonomousMagazineLoad());
        autoBuilder.addCommand(new AutonomousIntake());
        autoBuilder.addCommand(new AutonomousDriveStraight(centerLineDistance, 0.7, 3.25));
        autoBuilder.addCommand(new AutonomousResetEncoders());
        autoBuilder.addCommand(new AutonomousDriveStraight(centerReverse, 0.7, 1.25));
        autoBuilder.addCommand(new AutonomousMagazineStop());
        autoBuilder.addCommand(new AutonomousPivotTurn(secondTurnAngle, pivotLeftSpeed, -pivotRightSpeed, 5.0));
        autoBuilder.addCommand(new AutonomousDriveStop());
        autoBuilder.addCommand(new AutonomousOuttake());
        autoBuilder.addCommand(new AutonomousStop());
    }
    
    public void rightThreePrimer()
    {
        double sideTiltAngle = params.getAsDouble("A_SideAutoAngle", Tilt.lowAngle);
        double reverseDistance = params.getAsDouble("A_CenterLineReverseDistance", 100);
        double firstTurnAngle = params.getAsDouble("A_CenterLineFirstAngle", 35.0);
        double pivotLeftSpeed = params.getAsDouble("A_CenterLinePivotLeftSpeed", 0.5);
        double pivotRightSpeed = params.getAsDouble("A_CenterLinePivotRightSpeed", -0.5);
        
        autoBuilder.clearCommands();
        autoBuilder.addAutonomousDelay(autoDelay);
        autoBuilder.addCommand(new AutonomousShiftLow());
        autoBuilder.addVariableFireSequence(3, sideTiltAngle, Elevator.elevatorBottomPosition, 0.0);
        autoBuilder.addCommand(new AutonomousDriveStraight(reverseDistance, 0.7, 2.0));
        autoBuilder.addCommand(new AutonomousPivotTurn(firstTurnAngle, -pivotLeftSpeed, pivotRightSpeed, 2.5));
        autoBuilder.addCommand(new AutonomousStopAll());
        autoBuilder.addCommand(new AutonomousStop());
    }
    
}
