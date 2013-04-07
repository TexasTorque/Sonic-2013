package org.TexasTorque.TexasTorque2013.autonomous.drive;

import edu.wpi.first.wpilibj.Timer;
import org.TexasTorque.TexasTorque2013.autonomous.AutonomousCommand;
import org.TexasTorque.TorqueLib.controlLoop.SimPID;

public class AutonomousDriveStraight2 extends AutonomousCommand
{
    private double driveDistance;
    private double timeoutSecs;
    
    private double distanceSetpoint;
    private double angleSetpoint;
    private boolean zeroLock;
    
    private SimPID encoderPID;
    private SimPID gyroPID;
    
    private Timer timeoutTimer;
    
    public AutonomousDriveStraight2(double distance, double speed, boolean zeroAngle, double timeout)
    {
        super();
        
        encoderPID = new SimPID();
        gyroPID = new SimPID();
        
        encoderPID.setMaxOutput(speed);
        encoderPID.setMinDoneCycles(10);
        gyroPID.setMinDoneCycles(10);
        
        driveDistance = distance;
        
        timeoutSecs = timeout;
        timeoutTimer = new Timer();
    }
    
    public void reset()
    {
        double p = params.getAsDouble("D_DriveEncoderP", 0.05);
        double i = params.getAsDouble("D_DriveEncoderI", 0.0);
        double d = params.getAsDouble("D_DriveEncoderD", 0.0);
        double e = params.getAsDouble("D_DriveEncoderEpsilon", 0.0);
        double r = params.getAsDouble("D_DriveEncoderDoneRange", 0.0);
        
        encoderPID.setConstants(p, i, d);
        encoderPID.setErrorEpsilon(e);
        encoderPID.setDoneRange(r);
        encoderPID.resetErrorSum();
        encoderPID.resetPreviousVal();
        
        p = params.getAsDouble("D_DriveGyroP", 0.0);
        i = params.getAsDouble("D_DriveGyroI", 0.0);
        d = params.getAsDouble("D_DriveGyroD", 0.0);
        e = params.getAsDouble("D_DriveGyroEpsilon", 0.0);
        r = params.getAsDouble("D_DriveGyroDoneRange", 0.0);
        
        gyroPID.setConstants(p, i, d);
        gyroPID.setErrorEpsilon(e);
        gyroPID.setDoneRange(r);
        gyroPID.resetErrorSum();
        gyroPID.resetPreviousVal();
        
        distanceSetpoint = ((sensorInput.getLeftDriveEncoder() + sensorInput.getRightDriveEncoder()) / 2) + driveDistance;
        
        if(zeroLock)
        {
            angleSetpoint = 0.0;
        }
        else
        {
            angleSetpoint = sensorInput.getGyroAngle();
        }
        
        encoderPID.setDesiredValue(distanceSetpoint);
        gyroPID.setDesiredValue(angleSetpoint);
        
        timeoutTimer.reset();
        timeoutTimer.start();
    }
    
    public boolean run()
    {
        double averageDistance = (sensorInput.getLeftDriveEncoder() + sensorInput.getRightDriveEncoder()) / 2.0;
        double currentAngle = sensorInput.getGyroAngle();
        
        double y = encoderPID.calcPID(averageDistance);
        double x = -gyroPID.calcPID(currentAngle);
        
        double leftSpeed = y + x;
        double rightSpeed = y - x;
        
        drivebase.setDriveSpeeds(leftSpeed, rightSpeed);
        
        if(encoderPID.isDone() && gyroPID.isDone())
        {
            return true;
        }
        
        if(timeoutTimer.get() > timeoutSecs)
        {
            return true;
        }
        
        return false;
    }
}
