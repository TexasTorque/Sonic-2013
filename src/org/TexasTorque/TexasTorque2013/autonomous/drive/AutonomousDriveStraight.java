package org.TexasTorque.TexasTorque2013.autonomous.drive;

import org.TexasTorque.TexasTorque2013.autonomous.AutonomousCommand;
import org.TexasTorque.TorqueLib.controlLoop.SimPID;

public class AutonomousDriveStraight extends AutonomousCommand
{
    private double driveDistance;
    
    private SimPID encoderPID;
    private SimPID gyroPID;
    
    public AutonomousDriveStraight(double distance, double speed)
    {
        super();
        
        encoderPID = new SimPID();
        gyroPID = new SimPID();
        
        encoderPID.setDesiredValue(driveDistance);
        gyroPID.setDesiredValue(0.0);
        
        driveDistance = distance;
        
        reset();
    }
    
    public void reset()
    {
        double p = params.getAsDouble("D_DriveEncoderP", 0.0);
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
    }
    
    public boolean run()
    {
        
        
        
        if(encoderPID.isDone() && gyroPID.isDone())
        {
            return true;
        }
        return false;
    }
}
