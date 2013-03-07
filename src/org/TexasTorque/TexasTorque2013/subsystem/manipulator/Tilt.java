package org.TexasTorque.TexasTorque2013.subsystem.manipulator;

import org.TexasTorque.TexasTorque2013.TorqueSubsystem;
import org.TexasTorque.TexasTorque2013.constants.Constants;

public class Tilt extends TorqueSubsystem
{
    
    private static Tilt instance;
    
    private double desiredTiltAngle;
    private double tiltMotorSpeed;
    
    public static Tilt getInstance()
    {
        return (instance == null) ? instance = new Tilt() : instance;
    }
    
    public Tilt()
    {
        super();
        
        desiredTiltAngle = 0.0;
        tiltMotorSpeed = Constants.MOTOR_STOPPED;
    }
    
    public void run()
    {
        robotOutput.setTiltMotor(tiltMotorSpeed);
    }
    
    public void setTiltAngle(double angle)
    {
        if(angle != desiredTiltAngle)
        {
            desiredTiltAngle = angle;
        }
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
        
    }
}
