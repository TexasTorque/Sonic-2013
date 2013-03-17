package org.TexasTorque.TexasTorque2013.subsystem.manipulator;

import org.TexasTorque.TexasTorque2013.TorqueSubsystem;
import org.TexasTorque.TexasTorque2013.constants.Constants;
import org.TexasTorque.TorqueLib.controlLoop.SimPID;

public class Tilt extends TorqueSubsystem
{
    
    private static Tilt instance;
    
    private SimPID tiltPID;
    
    private double desiredTiltAngle;
    public double tiltMotorSpeed;
    
    public static double lowAngle;
    public static double highAngle;
    public static double sideAngle;
    public static double feederStationAngle;
    public static double autonomousLowAngle;
    
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
        
        if(desiredTiltAngle == 0.0 && tiltPID.isDone())
        {
            tiltMotorSpeed = 0.0;
        }
    }
    
    public void setToRobot()
    {
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
        lowAngle = params.getAsDouble("T_ShootLowAngle", 0.0);
        highAngle = params.getAsDouble("T_ShootHighAngle", 0.0);
        sideAngle = params.getAsDouble("T_ShootSideAngle", 0.0);
        feederStationAngle = params.getAsDouble("T_FeederStationAngle", 0.0);
        autonomousLowAngle = params.getAsDouble("A_RearLowAngle", lowAngle);
        
        double p = params.getAsDouble("T_TiltP", 0.0);
        double i = params.getAsDouble("T_TiltI", 0.0);
        double d = params.getAsDouble("T_TiltD", 0.0);
        double e = params.getAsDouble("T_TiltEpsilon", 0.0);
        double r = params.getAsDouble("T_TiltDoneRange", 0.0);
        double maxOutput = params.getAsDouble("T_MaxOutput", 0.4);
        
        tiltPID.setConstants(p, i, d);
        tiltPID.setErrorEpsilon(e);
        tiltPID.setDoneRange(r);
        tiltPID.resetErrorSum();
        tiltPID.resetPreviousVal();
        tiltPID.setMaxOutput(maxOutput);
    }
}
