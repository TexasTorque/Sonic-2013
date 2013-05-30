package org.TexasTorque.TexasTorque2013.autonomous;

import org.TexasTorque.TexasTorque2013.autonomous.drive.AutonomousDriveStop;
import org.TexasTorque.TexasTorque2013.autonomous.drive.AutonomousDriveStraightHigh;
import org.TexasTorque.TexasTorque2013.autonomous.drive.AutonomousDriveStraightLow;
import org.TexasTorque.TexasTorque2013.autonomous.drive.AutonomousShiftHigh;
import org.TexasTorque.TexasTorque2013.autonomous.drive.AutonomousShiftLow;
import org.TexasTorque.TexasTorque2013.autonomous.drive.AutonomousTurn;
import org.TexasTorque.TexasTorque2013.autonomous.drive.AutonomousVisionTiltLock;
import org.TexasTorque.TexasTorque2013.autonomous.intake.AutonomousIntake;
import org.TexasTorque.TexasTorque2013.autonomous.intake.AutonomousOuttake;
import org.TexasTorque.TexasTorque2013.autonomous.magazine.AutonomousFireOnce;
import org.TexasTorque.TexasTorque2013.autonomous.magazine.AutonomousMagazineDone;
import org.TexasTorque.TexasTorque2013.autonomous.magazine.AutonomousMagazineLoad;
import org.TexasTorque.TexasTorque2013.autonomous.magazine.AutonomousMagazineStop;
import org.TexasTorque.TexasTorque2013.autonomous.shooter.AutonomousShooterDone;
import org.TexasTorque.TexasTorque2013.autonomous.shooter.AutonomousSpinShooter;
import org.TexasTorque.TexasTorque2013.autonomous.shooter.AutonomousStopShooter;
import org.TexasTorque.TexasTorque2013.autonomous.tilt.AutonomousCustomTilt;
import org.TexasTorque.TexasTorque2013.autonomous.tilt.AutonomousTiltDone;
import org.TexasTorque.TexasTorque2013.autonomous.tilt.AutonomousTiltParallel;
import org.TexasTorque.TexasTorque2013.autonomous.tilt.AutonomousVisionTilt;
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
        params = Parameters.getTeleopInstance();
        
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
                middleSevenAuto();
                break;
            case Constants.RIGHT_SEVEN_AUTO:
                rightCenterAuto();
                break;
            case Constants.LEFT_SEVEN_AUTO:
                leftCenterAuto();
                break;
            case Constants.RIGHT_THREE_DRIVE_AUTO:
                sideDriveAuto();
                break;
            case Constants.VISION_AUTO:
                visionAuto();
                break;
            case Constants.VISION_LOCK_AUTO:
                visionLockAuto();
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
        autoBuilder.addLowFireSequence(4, 1.0);
        autoBuilder.addCommand(new AutonomousStopAll());
        autoBuilder.addCommand(new AutonomousStop());
    }
    
    public void sideAuto()
    {
        double sideTiltAngle = params.getAsDouble("A_SideAutoAngle", Tilt.lowAngle);
        
        autoBuilder.clearCommands();
        autoBuilder.addAutonomousDelay(autoDelay);
        autoBuilder.addVariableFireSequence(5, sideTiltAngle, Elevator.elevatorBottomPosition, 1.0);
        autoBuilder.addCommand(new AutonomousStop());
    }
    
    public void middleSevenAuto()
    {
        double driveSpeed = params.getAsDouble("A_MiddleSevenSpeed", 0.5);
        double driveDistance = params.getAsDouble("A_MiddleSevenDistance", 100);
        double timeout = params.getAsDouble("A_MiddleSevenTimeout", 5.0);
        double turnAngle = params.getAsDouble("A_MiddleSevenTurnAngle", 40.0);
        double firstShotAngle = params.getAsDouble("A_RearLowAngle", Tilt.lowAngle);
        double secondShotAngle = params.getAsDouble("A_MiddleSevenSecondTiltAngle", Tilt.lowAngle);
        double reverseDistance = params.getAsDouble("A_MiddleSevenReverseDistance", -57.0);
        double genericTimeout = 0.25;
        
        autoBuilder.clearCommands();
        autoBuilder.addAutonomousDelay(autoDelay);
        autoBuilder.addCommand(new AutonomousResetGyro());
        autoBuilder.addCommand(new AutonomousShiftLow());
        autoBuilder.addCommand(new AutonomousIntake());
        autoBuilder.addCommand(new AutonomousDriveStop());
        autoBuilder.addCommand(new AutonomousCustomTilt(firstShotAngle));
        autoBuilder.addCommand(new AutonomousSpinShooter());
        autoBuilder.addCommand(new AutonomousTiltDone(1.5));
        autoBuilder.addCommand(new AutonomousWait(0.5));
        
        int numFires = 4;
        for(int i = 0; i < numFires; i++)
        {
            autoBuilder.addCommand(new AutonomousTiltDone(genericTimeout));
            autoBuilder.addCommand(new AutonomousShooterDone(genericTimeout));
            autoBuilder.addCommand(new AutonomousMagazineDone(magazine.deltaTimeForward + magazine.deltaTimeReverse));
            autoBuilder.addCommand(new AutonomousFireOnce());
        }
        
        autoBuilder.addCommand(new AutonomousWait(0.25));
        autoBuilder.addCommand(new AutonomousStopShooter());
        autoBuilder.addCommand(new AutonomousTiltParallel());
        autoBuilder.addCommand(new AutonomousTiltDone(1.0));
        autoBuilder.addCommand(new AutonomousMagazineLoad());
        autoBuilder.addCommand(new AutonomousDriveStraightLow(driveDistance, driveSpeed, true, timeout));
        autoBuilder.addCommand(new AutonomousDriveStop());
        autoBuilder.addCommand(new AutonomousShiftHigh());
        autoBuilder.addCommand(new AutonomousSpinShooter());
        autoBuilder.addCommand(new AutonomousDriveStraightHigh(-(driveDistance + 3), 1.0, true, timeout));
        autoBuilder.addCommand(new AutonomousMagazineStop());
        autoBuilder.addCommand(new AutonomousWait(0.125));
        autoBuilder.addCommand(new AutonomousCustomTilt(secondShotAngle));
        autoBuilder.addCommand(new AutonomousWait(0.75));
        autoBuilder.addCommand(new AutonomousOuttake());
        
        numFires = 4;
        for(int i = 0; i < numFires; i++)
        {
            autoBuilder.addCommand(new AutonomousTiltDone(genericTimeout));
            autoBuilder.addCommand(new AutonomousShooterDone(genericTimeout));
            autoBuilder.addCommand(new AutonomousMagazineDone(magazine.deltaTimeForward + magazine.deltaTimeReverse));
            autoBuilder.addCommand(new AutonomousFireOnce());
        }
        
        autoBuilder.addCommand(new AutonomousWait(0.25));
        autoBuilder.addCommand(new AutonomousStopShooter());
        autoBuilder.addCommand(new AutonomousTiltParallel());
        autoBuilder.addCommand(new AutonomousDriveStop());
        autoBuilder.addCommand(new AutonomousStopAll());
        autoBuilder.addCommand(new AutonomousStop());
    }
    
    public void visionAuto()
    {
        double driveSpeed = params.getAsDouble("A_MiddleSevenSpeed", 0.5);
        double driveDistance = params.getAsDouble("A_MiddleSevenDistance", 100);
        double timeout = params.getAsDouble("A_MiddleSevenTimeout", 5.0);
        double genericTimeout = 0.25;
        
        autoBuilder.clearCommands();
        autoBuilder.addAutonomousDelay(autoDelay);
        autoBuilder.addCommand(new AutonomousResetGyro());
        autoBuilder.addCommand(new AutonomousShiftLow());
        autoBuilder.addCommand(new AutonomousIntake());
        autoBuilder.addCommand(new AutonomousDriveStop());
        autoBuilder.addCommand(new AutonomousSpinShooter());
        autoBuilder.addCommand(new AutonomousVisionTilt(5.0));
        autoBuilder.addCommand(new AutonomousWait(0.5));
        
        int numFires = 4;
        for(int i = 0; i < numFires; i++)
        {
            autoBuilder.addCommand(new AutonomousTiltDone(genericTimeout));
            autoBuilder.addCommand(new AutonomousShooterDone(genericTimeout));
            autoBuilder.addCommand(new AutonomousMagazineDone(magazine.deltaTimeForward + magazine.deltaTimeReverse));
            autoBuilder.addCommand(new AutonomousFireOnce());
        }
        
        autoBuilder.addCommand(new AutonomousWait(0.25));
        autoBuilder.addCommand(new AutonomousStopShooter());
        autoBuilder.addCommand(new AutonomousTiltParallel());
        autoBuilder.addCommand(new AutonomousTiltDone(1.0));
        autoBuilder.addCommand(new AutonomousMagazineLoad());
        autoBuilder.addCommand(new AutonomousDriveStraightLow(driveDistance, driveSpeed, true, timeout));
        autoBuilder.addCommand(new AutonomousDriveStop());
        autoBuilder.addCommand(new AutonomousShiftHigh());
        autoBuilder.addCommand(new AutonomousSpinShooter());
        autoBuilder.addCommand(new AutonomousDriveStraightHigh(-(driveDistance + 3), 1.0, true, timeout));
        autoBuilder.addCommand(new AutonomousMagazineStop());
        autoBuilder.addCommand(new AutonomousWait(0.125));
        autoBuilder.addCommand(new AutonomousVisionTilt(5.0));
        autoBuilder.addCommand(new AutonomousWait(0.75));
        autoBuilder.addCommand(new AutonomousOuttake());
        
        numFires = 4;
        for(int i = 0; i < numFires; i++)
        {
            autoBuilder.addCommand(new AutonomousTiltDone(genericTimeout));
            autoBuilder.addCommand(new AutonomousShooterDone(genericTimeout));
            autoBuilder.addCommand(new AutonomousMagazineDone(magazine.deltaTimeForward + magazine.deltaTimeReverse));
            autoBuilder.addCommand(new AutonomousFireOnce());
        }
        
        autoBuilder.addCommand(new AutonomousWait(0.25));
        autoBuilder.addCommand(new AutonomousStopShooter());
        autoBuilder.addCommand(new AutonomousTiltParallel());
        autoBuilder.addCommand(new AutonomousDriveStop());
        autoBuilder.addCommand(new AutonomousStopAll());
        autoBuilder.addCommand(new AutonomousStop());
    }
    public void visionLockAuto()
    {
        double driveSpeed = params.getAsDouble("A_MiddleSevenSpeed", 0.5);
        double driveDistance = params.getAsDouble("A_MiddleSevenDistance", 100);
        double timeout = params.getAsDouble("A_MiddleSevenTimeout", 5.0);
        double genericTimeout = 0.25;
        
        autoBuilder.clearCommands();
        autoBuilder.addAutonomousDelay(autoDelay);
        autoBuilder.addCommand(new AutonomousResetGyro());
        autoBuilder.addCommand(new AutonomousShiftLow());
        autoBuilder.addCommand(new AutonomousIntake());
        autoBuilder.addCommand(new AutonomousDriveStop());
        autoBuilder.addCommand(new AutonomousSpinShooter());
        autoBuilder.addCommand(new AutonomousVisionTiltLock(3.0));
        autoBuilder.addCommand(new AutonomousWait(0.5));
        
        int numFires = 4;
        for(int i = 0; i < numFires; i++)
        {
            autoBuilder.addCommand(new AutonomousTiltDone(genericTimeout));
            autoBuilder.addCommand(new AutonomousShooterDone(genericTimeout));
            autoBuilder.addCommand(new AutonomousMagazineDone(magazine.deltaTimeForward + magazine.deltaTimeReverse));
            autoBuilder.addCommand(new AutonomousFireOnce());
        }
        
        autoBuilder.addCommand(new AutonomousWait(0.25));
        autoBuilder.addCommand(new AutonomousStopShooter());
        autoBuilder.addCommand(new AutonomousTiltParallel());
        autoBuilder.addCommand(new AutonomousTiltDone(1.0));
        autoBuilder.addCommand(new AutonomousMagazineLoad());
        autoBuilder.addCommand(new AutonomousDriveStraightLow(driveDistance, driveSpeed, true, timeout));
        autoBuilder.addCommand(new AutonomousDriveStop());
        autoBuilder.addCommand(new AutonomousShiftHigh());
        autoBuilder.addCommand(new AutonomousSpinShooter());
        autoBuilder.addCommand(new AutonomousDriveStraightHigh(-(driveDistance + 3), 1.0, true, timeout));
        autoBuilder.addCommand(new AutonomousMagazineStop());
        autoBuilder.addCommand(new AutonomousWait(0.125));
        autoBuilder.addCommand(new AutonomousVisionTiltLock(3.0));
        autoBuilder.addCommand(new AutonomousWait(0.75));
        autoBuilder.addCommand(new AutonomousOuttake());
        
        numFires = 4;
        for(int i = 0; i < numFires; i++)
        {
            autoBuilder.addCommand(new AutonomousTiltDone(genericTimeout));
            autoBuilder.addCommand(new AutonomousShooterDone(genericTimeout));
            autoBuilder.addCommand(new AutonomousMagazineDone(magazine.deltaTimeForward + magazine.deltaTimeReverse));
            autoBuilder.addCommand(new AutonomousFireOnce());
        }
        
        autoBuilder.addCommand(new AutonomousWait(0.25));
        autoBuilder.addCommand(new AutonomousStopShooter());
        autoBuilder.addCommand(new AutonomousTiltParallel());
        autoBuilder.addCommand(new AutonomousDriveStop());
        autoBuilder.addCommand(new AutonomousStopAll());
        autoBuilder.addCommand(new AutonomousStop());
    }
    
    public void rightCenterAuto()
    {
        double firstShotAngle = params.getAsDouble("A_SideAutoAngle", Tilt.sideAngle);
        double secondShotAngle = params.getAsDouble("A_RightSevenSecondShotAngle", 0.0);
        double firstTurnAngle = params.getAsDouble("A_RightSevenFirstTurnAngle", 35.0);
        double secondTurnAngle = params.getAsDouble("A_RightSevenSecondTurnAngle", 55.0);
        double distanceToCenter = params.getAsDouble("A_RightSevenDistanceToCenter", 50.0);
        double distanceToCenterSpeed = params.getAsDouble("A_RightSevenFirstSpeed", 1.0);
        double straddleDistance = params.getAsDouble("A_RightSevenStraddleDistance", 100.0);
        double straddleSpeed = params.getAsDouble("A_RightSevenStraddleSpeed", 0.8);
        double genericTimeout = 1.0;
        
        autoBuilder.clearCommands();
        autoBuilder.addAutonomousDelay(autoDelay);
        autoBuilder.addCommand(new AutonomousResetEncoders());
        autoBuilder.addCommand(new AutonomousShiftHigh());
        autoBuilder.addCommand(new AutonomousIntake());
        autoBuilder.addCommand(new AutonomousDriveStop());
        autoBuilder.addCommand(new AutonomousCustomTilt(firstShotAngle));
        autoBuilder.addCommand(new AutonomousSpinShooter());
        
        int numFires = 3;
        for(int i = 0; i < numFires; i++)
        {
            autoBuilder.addCommand(new AutonomousTiltDone(genericTimeout));
            autoBuilder.addCommand(new AutonomousShooterDone(genericTimeout));
            autoBuilder.addCommand(new AutonomousMagazineDone(genericTimeout));
            autoBuilder.addCommand(new AutonomousFireOnce());
        }
        
        autoBuilder.addCommand(new AutonomousWait(0.25));
        autoBuilder.addCommand(new AutonomousStopShooter());
        autoBuilder.addCommand(new AutonomousTiltParallel());
        autoBuilder.addCommand(new AutonomousDriveStraightHigh(distanceToCenter, distanceToCenterSpeed, false, 2.0));
        autoBuilder.addCommand(new AutonomousTiltDone(genericTimeout));
        autoBuilder.addCommand(new AutonomousMagazineLoad());
        autoBuilder.addCommand(new AutonomousTurn(firstTurnAngle, 2.0, 0.5));
        autoBuilder.addCommand(new AutonomousDriveStraightHigh(straddleDistance, straddleSpeed, false, 3.5));
        autoBuilder.addCommand(new AutonomousDriveStop());
        autoBuilder.addCommand(new AutonomousSpinShooter());
        autoBuilder.addCommand(new AutonomousTurn(secondTurnAngle, 2.5, 0.5));
        autoBuilder.addCommand(new AutonomousDriveStop());
        autoBuilder.addCommand(new AutonomousOuttake());
        autoBuilder.addCommand(new AutonomousMagazineStop());
        autoBuilder.addCommand(new AutonomousWait(0.125));
        autoBuilder.addCommand(new AutonomousCustomTilt(secondShotAngle));
        autoBuilder.addCommand(new AutonomousWait(0.5));
        
        numFires = 4;
        for(int i = 0; i < numFires; i++)
        {
            autoBuilder.addCommand(new AutonomousTiltDone(0.25));
            autoBuilder.addCommand(new AutonomousShooterDone(0.25));
            autoBuilder.addCommand(new AutonomousMagazineDone(Magazine.deltaTimeForward + Magazine.deltaTimeReverse));
            autoBuilder.addCommand(new AutonomousFireOnce());
        }
        
        autoBuilder.addCommand(new AutonomousWait(0.25));
        autoBuilder.addCommand(new AutonomousStopShooter());
        autoBuilder.addCommand(new AutonomousTiltParallel());
        autoBuilder.addCommand(new AutonomousStopAll());
        autoBuilder.addCommand(new AutonomousStop());
    }
    
    public void leftCenterAuto()
    {
        double firstShotAngle = params.getAsDouble("A_SideAutoAngle", Tilt.sideAngle);
        double secondShotAngle = params.getAsDouble("A_LeftSevenSecondShotAngle", Tilt.sideAngle);
        double firstTurnAngle = params.getAsDouble("A_LeftSevenFirstTurnAngle", -35.0);
        double secondTurnAngle = params.getAsDouble("A_LeftSevenSecondTurnAngle", -55.0);
        double distanceToCenter = params.getAsDouble("A_LeftSevenDistanceToCenter", 50.0);
        double distanceToCenterSpeed = params.getAsDouble("A_LeftSevenFirstSpeed", 1.0);
        double straddleDistance = params.getAsDouble("A_LeftSevenStraddleDistance", 100.0);
        double straddleSpeed = params.getAsDouble("A_LeftSevenStraddleSpeed", 0.8);
        double genericTimeout = 1.0;
        
        autoBuilder.clearCommands();
        autoBuilder.addAutonomousDelay(autoDelay);
        autoBuilder.addCommand(new AutonomousResetGyro());
        autoBuilder.addCommand(new AutonomousShiftHigh());
        autoBuilder.addCommand(new AutonomousIntake());
        autoBuilder.addCommand(new AutonomousDriveStop());
        autoBuilder.addCommand(new AutonomousCustomTilt(firstShotAngle));
        autoBuilder.addCommand(new AutonomousSpinShooter());
        
        int numFires = 3;
        for(int i = 0; i < numFires; i++)
        {
            autoBuilder.addCommand(new AutonomousTiltDone(genericTimeout));
            autoBuilder.addCommand(new AutonomousShooterDone(genericTimeout));
            autoBuilder.addCommand(new AutonomousMagazineDone(genericTimeout));
            autoBuilder.addCommand(new AutonomousFireOnce());
        }
        
        autoBuilder.addCommand(new AutonomousWait(0.25));
        autoBuilder.addCommand(new AutonomousStopShooter());
        autoBuilder.addCommand(new AutonomousTiltParallel());
        autoBuilder.addCommand(new AutonomousDriveStraightHigh(distanceToCenter, distanceToCenterSpeed, false, 2.0));
        autoBuilder.addCommand(new AutonomousTiltDone(genericTimeout));
        autoBuilder.addCommand(new AutonomousMagazineLoad());
        autoBuilder.addCommand(new AutonomousTurn(firstTurnAngle, 2.0, 3.0));
        autoBuilder.addCommand(new AutonomousDriveStraightHigh(straddleDistance, straddleSpeed, false, 3.0));
        autoBuilder.addCommand(new AutonomousDriveStop());
        autoBuilder.addCommand(new AutonomousOuttake());
        autoBuilder.addCommand(new AutonomousMagazineStop());
        autoBuilder.addCommand(new AutonomousWait(0.125));
        autoBuilder.addCommand(new AutonomousCustomTilt(secondShotAngle));
        autoBuilder.addCommand(new AutonomousSpinShooter());
        autoBuilder.addCommand(new AutonomousTurn(secondTurnAngle, 2.0, 0.5));
        autoBuilder.addCommand(new AutonomousDriveStop());
        
        numFires = 4;
        for(int i = 0; i < numFires; i++)
        {
            autoBuilder.addCommand(new AutonomousTiltDone(genericTimeout));
            autoBuilder.addCommand(new AutonomousShooterDone(genericTimeout));
            autoBuilder.addCommand(new AutonomousMagazineDone(genericTimeout));
            autoBuilder.addCommand(new AutonomousFireOnce());
        }
        
        autoBuilder.addCommand(new AutonomousWait(0.25));
        autoBuilder.addCommand(new AutonomousStopShooter());
        autoBuilder.addCommand(new AutonomousTiltParallel());
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
        autoBuilder.addCommand(new AutonomousShiftHigh());
        autoBuilder.addVariableFireSequence(4, sideTiltAngle, Elevator.elevatorBottomPosition, 1.0);
        autoBuilder.addCommand(new AutonomousDriveStraightHigh(distance, speed, false, 3.0));
        autoBuilder.addCommand(new AutonomousStopAll());
        autoBuilder.addCommand(new AutonomousStop());
    }
    
}
