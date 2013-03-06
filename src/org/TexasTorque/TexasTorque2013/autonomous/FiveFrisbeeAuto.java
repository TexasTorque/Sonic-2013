package org.TexasTorque.TexasTorque2013.autonomous;

import edu.wpi.first.wpilibj.Timer;
import org.TexasTorque.TexasTorque2013.constants.Constants;
import org.TexasTorque.TexasTorque2013.subsystem.manipulator.Elevator;
import org.TexasTorque.TexasTorque2013.subsystem.manipulator.Shooter;
import org.TexasTorque.TorqueLib.controlLoop.SimPID;

public class FiveFrisbeeAuto extends AutonomousBase
{
    
    private final int FIRST_SHOOTING_STATE = 0;
    private final int WAITING_STATE = 1;
    private final int DRIVE_STATE = 2;
    private final int SECOND_WAITING_STATE = 3;
    private final int SECOND_SHOOTING_STATE = 4;
    
    private SimPID linearPID;
    private SimPID gyroPID;
    
    private Timer autoTimer;
    
    private int autonomousState;
    private int driveDistance;
    private double shootTime;
    private double tempTime;
    
    public FiveFrisbeeAuto()
    {
        super();
        
        linearPID = new SimPID();
        gyroPID = new SimPID();
        
        autoTimer = new Timer();
        
        autonomousState = FIRST_SHOOTING_STATE;
    }
    
    public void init()
    {
        autonomousState = FIRST_SHOOTING_STATE;
        
        driveDistance = (int) params.getAsDouble("A_FiveFrisbeeDriveDistance", 0.0);
        shootTime = params.getAsDouble("A_RearPyramidAutonomousStopTime", 5.0);
        
        linearPID.setDesiredValue(driveDistance);
        gyroPID.setDesiredValue(0.0);
        
        double p = params.getAsDouble("A_FiveFrisbeeLinearP", 0.0);
        double i = params.getAsDouble("A_FiveFrisbeeLinearI", 0.0);
        double d = params.getAsDouble("A_FiveFrisbeeLinearD", 0.0);
        double e = params.getAsDouble("A_FiveFrisbeeLinearEpsilon", 0.0);
        double r = params.getAsDouble("A_FiveFrisbeeLinearDoneRange", 0.0);
        
        linearPID.setConstants(p, i, d);
        linearPID.setErrorEpsilon(e);
        linearPID.setDoneRange(r);
        linearPID.resetErrorSum();
        linearPID.resetPreviousVal();
        
        p = params.getAsDouble("A_FiveFrisbeeGyroP", 0.0);
        i = params.getAsDouble("A_FiveFrisbeeGyroI", 0.0);
        d = params.getAsDouble("A_FiveFrisbeeGyroD", 0.0);
        e = params.getAsDouble("A_FiveFrisbeeGryoEpsilon", 0.0);
        r = params.getAsDouble("A_FiveFrisbeeGyroDoneRange", 0.0);
        
        gyroPID.setConstants(p, i, d);
        gyroPID.setErrorEpsilon(e);
        gyroPID.setDoneRange(r);
        gyroPID.resetErrorSum();
        gyroPID.resetPreviousVal();
        
        autoTimer.reset();
        autoTimer.start();
    }
    
    public boolean run()
    {
        if(autonomousState == FIRST_SHOOTING_STATE)
        {
            drivebase.setDriveSpeeds(Constants.MOTOR_STOPPED, Constants.MOTOR_STOPPED);

            manipulator.shootLowWithoutVision();

            if(autoTimer.get() > shootTime)
            {
                autonomousState = WAITING_STATE;
            }
        }
        else if(autonomousState == WAITING_STATE)
        {
            drivebase.setDriveSpeeds(Constants.MOTOR_STOPPED, Constants.MOTOR_STOPPED);

            manipulator.restoreDefaultPositions();

            if(autoTimer.get() > shootTime + 1.5)
            {
                autonomousState = DRIVE_STATE;
            }
        }
        else if(autonomousState == DRIVE_STATE)
        {
            double leftEncoder = sensorInput.getLeftDriveEncoder();
            double rightEncoder = sensorInput.getRightDriveEncoder();
            double average = (leftEncoder + rightEncoder) / 2;
            double gyroPosition = sensorInput.getGyroAngle();
            
            double y = linearPID.calcPID(average);
            double x = -gyroPID.calcPID(gyroPosition);

            double leftSpeed = y + x;
            double rightSpeed = y - x;

            drivebase.setDriveSpeeds(leftSpeed, rightSpeed);
            manipulator.intakeFrisbees();

            if(linearPID.isDone() && gyroPID.isDone())
            {
                drivebase.setDriveSpeeds(Constants.MOTOR_STOPPED, Constants.MOTOR_STOPPED);
                autonomousState = SECOND_WAITING_STATE;
                tempTime = autoTimer.get();
            }
        }
        else if(autonomousState == SECOND_WAITING_STATE)
        {
            drivebase.setDriveSpeeds(Constants.MOTOR_STOPPED, Constants.MOTOR_STOPPED);
            manipulator.restoreDefaultPositions();
            
            if(autoTimer.get() > tempTime + 1.5)
            {
                autonomousState = SECOND_SHOOTING_STATE;
            }
        }
        else if(autonomousState == SECOND_SHOOTING_STATE)
        {
            drivebase.setDriveSpeeds(Constants.MOTOR_STOPPED, Constants.MOTOR_STOPPED);
            intake.setIntakeSpeed(Constants.MOTOR_STOPPED);
            magazine.setDesiredState(Constants.MAGAZINE_READY_STATE);
            elevator.setDesiredPosition(Elevator.elevatorTopPosition);
            shooter.setShooterRates(Shooter.frontShooterRate, Shooter.rearShooterRate);
            
            double tiltAngle = params.getAsDouble("A_FiveFrisbeeAutoSecondShotAngle", Shooter.shootHighStandardAngle);
            
            shooter.setTiltAngle(tiltAngle);
            
            if(elevator.atDesiredPosition() && shooter.isReadyToFire())
            {
                magazine.setDesiredState(Constants.MAGAZINE_SHOOTING_STATE);
            }
            
        }
        
        intake.run();
        shooter.run();
        magazine.run();
        elevator.run();
        drivebase.run();
        return false;
    }
    
    public void end()
    {
        autoTimer.stop();
        autoTimer.reset();
    }
}
