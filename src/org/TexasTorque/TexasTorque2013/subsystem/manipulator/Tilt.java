package org.TexasTorque.TexasTorque2013.subsystem.manipulator;

import org.TexasTorque.TexasTorque2013.TorqueSubsystem;
import org.TexasTorque.TexasTorque2013.constants.Constants;
import org.TexasTorque.TorqueLib.controlLoop.TorquePID;

public class Tilt extends TorqueSubsystem
{
    
    private static Tilt instance;
    
    private TorquePID tiltPID;
    
    private double desiredTiltAngle;
    private double tiltMotorSpeed;
    
    private double incrementSize;
    private double decrementSize;
    
    public static double lowAngle;
    public static double highAngle;
    public static double sideAngle;
    public static double feederStationAngle;
    public static double autonomousLowAngle;
    public static double shootLowAdditive;
    
    public static Tilt getInstance()
    {
        return (instance == null) ? instance = new Tilt() : instance;
    }
    
    public Tilt()
    {
        super();
        
        tiltPID = new TorquePID();
        
        desiredTiltAngle = 0.0;
        tiltMotorSpeed = Constants.MOTOR_STOPPED;
        
        incrementSize = 0.5;
        decrementSize = 0.5;
    }
    
    public void run()
    {   
        double currentAngle = sensorInput.getTiltAngle();
        tiltMotorSpeed = tiltPID.calculate(currentAngle);
        
        if(desiredTiltAngle == 0.0 && tiltPID.isDone())
        {
            tiltMotorSpeed = 0.0;
        }
    }
    
    public void setToRobot()
    {
        robotOutput.setTiltMotor(tiltMotorSpeed);
    }
    
    public void tiltAdjustments(boolean increment, boolean decrement)
    {
        if(increment)
        {
            lowAngle += incrementSize;
            sideAngle += incrementSize;
            highAngle += incrementSize;
            incrementSize = 0.0;
        }
        else
        {
            incrementSize = 0.5;
        }

        if(decrement)
        {
            lowAngle -= decrementSize;
            sideAngle -= decrementSize;
            highAngle -= decrementSize;
            decrementSize = 0.0;
        }
        else
        {
            decrementSize = 0.5;
        }
    }
    
    public void setTiltAngle(double angle)
    {
        if(angle != desiredTiltAngle)
        {
            desiredTiltAngle = angle;
            tiltPID.setSetpoint(desiredTiltAngle);
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
        shootLowAdditive = params.getAsDouble("T_ShootLowAdditive", 0.0);
        
        double p = params.getAsDouble("T_TiltP", 0.0);
        double i = params.getAsDouble("T_TiltI", 0.0);
        double d = params.getAsDouble("T_TiltD", 0.0);
        double e = params.getAsDouble("T_TiltEpsilon", 0.0);
        double r = params.getAsDouble("T_TiltDoneRange", 0.0);
        double maxOutput = params.getAsDouble("T_MaxOutput", 0.4);
        
        tiltPID.setPIDGains(p, i, d);
        tiltPID.setEpsilon(e);
        tiltPID.setDoneRange(r);
        tiltPID.setMaxOutput(maxOutput);
        tiltPID.setMinDoneCycles(0);
        tiltPID.reset();
        
        incrementSize = 0.5;
        decrementSize = 0.5;
    }
}
