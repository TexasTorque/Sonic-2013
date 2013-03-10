package org.TexasTorque.TexasTorque2013.subsystem.manipulator;

import org.TexasTorque.TexasTorque2013.TorqueSubsystem;
import org.TexasTorque.TexasTorque2013.constants.Constants;
import org.TexasTorque.TorqueLib.controlLoop.SimPID;

public class Tilt extends TorqueSubsystem
{
    
    private static Tilt instance;
    
    private SimPID tiltPID;
    
    private double desiredTiltAngle;
    private double tiltMotorSpeed;
    
    public static double lowAngle;
    public static double highAngle;
    public static double feederStationAngle;
    
    public static Tilt getInstance()
    {
        return (instance == null) ? instance = new Tilt() : instance;
    }
    
    public Tilt()
    {
        super();
        
        tiltPID = new SimPID();
        
        desiredTiltAngle = 0.0;
        tiltMotorSpeed = Constants.MOTOR_STOPPED;
    }
    
    public void run()
    {
        double currentAngle = sensorInput.getTiltAngle();
        
        tiltMotorSpeed = tiltPID.calcPID(currentAngle);
        
        robotOutput.setTiltMotor(tiltMotorSpeed);
    }
    
    public void setTiltAngle(double angle)
    {
        if(angle != desiredTiltAngle)
        {
            desiredTiltAngle = angle;
            tiltPID.setDesiredValue(desiredTiltAngle);
        }
    }
    
    public boolean isLocked()
    {
        return tiltPID.isDone();
    }
    
    public String getKeyNames()
    {
        String names = "DesiredTiltAngle,";
        names += "TiltMotorSpeed,";
        names += "ActualTiltAngle,";
        
        return names;
    }
    
    public String logData()
    {
        String data = desiredTiltAngle + ",";
        data += tiltMotorSpeed + ",";
        data += sensorInput.getTiltAngle() + ",";
        
        return data;
    }
    
    public void loadParameters()
    {
        double p = params.getAsDouble("S_TiltP", 0.0);
        double i = params.getAsDouble("S_TiltI", 0.0);
        double d = params.getAsDouble("S_TiltD", 0.0);
        double e = params.getAsDouble("S_TiltEpsilon", 0.0);
        double r = params.getAsDouble("S_TiltDoneRange", 0.0);
        
        tiltPID.setConstants(p, i, d);
        tiltPID.setErrorEpsilon(e);
        tiltPID.setDoneRange(r);
        tiltPID.resetErrorSum();
        tiltPID.resetPreviousVal();
    }
}
