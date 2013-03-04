package org.TexasTorque.TexasTorque2013.autonomous;

import edu.wpi.first.wpilibj.Timer;
import org.TexasTorque.TexasTorque2013.constants.Constants;
import org.TexasTorque.TexasTorque2013.subsystem.manipulator.Intake;
import org.TexasTorque.TorqueLib.controlLoop.SimPID;

public class FiveFrisbeeAuto extends AutonomousBase
{
    
    private final int FIRST_SHOOTING_STATE = 0;
    private final int WAITING_STATE = 1;
    private final int DRIVE_STATE = 2;
    private final int SECOND_SHOOTING_STATE = 3;
    
    private SimPID linearPID;
    private SimPID gyroPID;
    
    private Timer autoTimer;
    
    private int autonomousState;
    private int driveDistance;
    
    public FiveFrisbeeAuto()
    {
        super();
        
        linearPID = new SimPID();
        gyroPID = new SimPID();
        
        autoTimer = new Timer();
        
        autonomousState = 0;
    }
    
    public void init()
    {
        autonomousState = 0;
        
        driveDistance = (int) params.getAsDouble("A_FiveFrisbeeDriveDistance", 0.0);
        
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
    
    public void run()
    {
        if(autonomousState == FIRST_SHOOTING_STATE)
        {
            drivebase.setDriveSpeeds(Constants.MOTOR_STOPPED, Constants.MOTOR_STOPPED);

            manipulator.shootLowWithoutVision();

            if(autoTimer.get() > 5.0)
            {
                autonomousState = WAITING_STATE;
            }
        }
        else if(autonomousState == WAITING_STATE)
        {
            drivebase.setDriveSpeeds(Constants.MOTOR_STOPPED, Constants.MOTOR_STOPPED);

            manipulator.restoreDefaultPositions();

            if(autoTimer.get() > 7.0)
            {
                autonomousState = DRIVE_STATE;
            }
        }
        else if(autonomousState == DRIVE_STATE)
        {

            intake.setIntakeSpeed(Intake.intakeSpeed);

            double leftEncoder = sensorInput.getLeftDriveEncoder();
            double rightEncoder = sensorInput.getRightDriveEncoder();
            double average = (leftEncoder + rightEncoder) / 2;
            double gyroPosition = sensorInput.getGyroAngle();

            double x = linearPID.calcPID(average);
            double y = gyroPID.calcPID(gyroPosition);

            double leftSpeed = y + x;
            double rightSpeed = y - x;

            drivebase.setDriveSpeeds(leftSpeed, rightSpeed);

            intake.setIntakeSpeed(Intake.intakeSpeed);

            if(linearPID.isDone() && gyroPID.isDone())
            {
                drivebase.setDriveSpeeds(Constants.MOTOR_STOPPED, Constants.MOTOR_STOPPED);
                autonomousState = SECOND_SHOOTING_STATE;
            }
        }
        else if(autonomousState == SECOND_SHOOTING_STATE)
        {
            drivebase.setDriveSpeeds(Constants.MOTOR_STOPPED, Constants.MOTOR_STOPPED);

            manipulator.shootHighWithVision();
        }
    }
    
    public void end()
    {
        autoTimer.stop();
        autoTimer.reset();
    }
}
