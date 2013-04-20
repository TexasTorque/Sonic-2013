package org.TexasTorque.TexasTorque2013.autonomous.drive;

import edu.wpi.first.wpilibj.Timer;
import org.TexasTorque.TexasTorque2013.autonomous.AutonomousCommand;
import org.TexasTorque.TexasTorque2013.constants.Constants;
import org.TexasTorque.TorqueLib.controlLoop.TorquePID;

public class AutonomousDriveStraightLow extends AutonomousCommand
{
    private double driveDistance;
    private double timeoutSecs;
    
    private double distanceSetpoint;
    private double angleSetpoint;
    private boolean zeroLock;
    
    private TorquePID encoderPID;
    private TorquePID gyroPID;
    
    private Timer timeoutTimer;
    
    public AutonomousDriveStraightLow(double distance, double speed, boolean zeroAngle, double timeout)
    {
        super();
        
        encoderPID = new TorquePID();
        gyroPID = new TorquePID();
        
        encoderPID.setMaxOutput(speed);
        encoderPID.setMinDoneCycles(10);
        gyroPID.setMinDoneCycles(10);
        
        driveDistance = distance;
        
        timeoutSecs = timeout;
        timeoutTimer = new Timer();
    }
    
    public void reset()
    {
        double p = params.getAsDouble("D_DriveEncoderLowP", 0.05);
        double i = params.getAsDouble("D_DriveEncoderLowI", 0.0);
        double d = params.getAsDouble("D_DriveEncoderLowD", 0.0);
        double e = params.getAsDouble("D_DriveEncoderLowEpsilon", 0.0);
        double r = params.getAsDouble("D_DriveEncoderLowDoneRange", 0.0);
        
        encoderPID.setPIDGains(p, i, d);
        encoderPID.setEpsilon(e);
        encoderPID.setDoneRange(r);
        encoderPID.reset();
        
        p = params.getAsDouble("D_DriveGyroLowP", 0.0);
        i = params.getAsDouble("D_DriveGyroLowI", 0.0);
        d = params.getAsDouble("D_DriveGyroLowD", 0.0);
        e = params.getAsDouble("D_DriveGyroLowEpsilon", 0.0);
        r = params.getAsDouble("D_DriveGyroLowDoneRange", 0.0);
        
        gyroPID.setPIDGains(p, i, d);
        gyroPID.setEpsilon(e);
        gyroPID.setDoneRange(r);
        gyroPID.reset();
        
        distanceSetpoint = ((sensorInput.getLeftDriveEncoder() + sensorInput.getRightDriveEncoder()) / 2) + driveDistance;
        
        if(zeroLock)
        {
            angleSetpoint = 0.0;
        }
        else
        {
            angleSetpoint = sensorInput.getGyroAngle();
        }
        
        encoderPID.setSetpoint(distanceSetpoint);
        gyroPID.setSetpoint(angleSetpoint);
        
        timeoutTimer.reset();
        timeoutTimer.start();
    }
    
    public boolean run()
    {
        double averageDistance = (sensorInput.getLeftDriveEncoder() + sensorInput.getRightDriveEncoder()) / 2.0;
        double currentAngle = sensorInput.getGyroAngle();
        
        double y = encoderPID.calculate(averageDistance);
        double x = -gyroPID.calculate(currentAngle);
        
        double leftSpeed = y + x;
        double rightSpeed = y - x;
        
        drivebase.setShifters(Constants.LOW_GEAR);
        drivebase.setDriveSpeeds(leftSpeed, rightSpeed);
        
        if(encoderPID.isDone() && gyroPID.isDone())
        {
            return true;
        }
        
        if(timeoutTimer.get() > timeoutSecs)
        {
            System.err.println("Drive timed out");
            return true;
        }
        
        return false;
    }
}
