package org.TexasTorque.TexasTorque2013.subsystem.manipulator;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.TexasTorque.TexasTorque2013.TorqueSubsystem;
import org.TexasTorque.TexasTorque2013.constants.Constants;
import org.TexasTorque.TorqueLib.controlLoop.TorquePID;

public class Tilt extends TorqueSubsystem
{
    
    private static Tilt instance;
    
    private TorquePID tiltPID;
    
    private double desiredTiltAngle;
    private double tiltMotorSpeed;
    private boolean gateState;
    
    private double tiltThreshold;
    
    private double incrementSize;
    private double decrementSize;
    
    public static double lowAngle;
    public static double highAngle;
    public static double sideAngle;
    public static double farAngle;
    public static double closeAngle;
    public static double feederStationAngle;
    public static double autonomousLowAngle;
    public static double madtownAngle;
    public static double visionAdditive;
    public static double visionAdditiveConst;
    public static double visionAdditiveB;
    public static double visionAdditiveThird;
    public static double visionAdditiveSecond;
    public static double visionAdditiveFirst;
    public static double visionAdditiveFourth;
    public static double visionAdditiveFifth;
    public static double visionTanA;
    public static double visionTanB;
    public static double visionTanC;
    public static double visionTanD;
    
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
        gateState = Constants.GATE_EXTENDED;
        
        tiltThreshold = 0.0;
        
        incrementSize = 0.5;
        decrementSize = 0.5;
    }
    
    public void run()
    {   
        double currentAngle = sensorInput.getTiltAngle();
        tiltMotorSpeed = tiltPID.calculate(currentAngle);
        
        if(desiredTiltAngle != 0.0)
        {
            tiltMotorSpeed += tiltThreshold;
        }
        
        if(desiredTiltAngle != 0.0 && Math.abs(desiredTiltAngle - currentAngle) <= 15)
        {
            gateState = Constants.GATE_RETRACTED;
        }
        else
        {
            gateState = Constants.GATE_EXTENDED;
        }
        
        if(driverInput.fireFrisbee())
        {
            gateState = Constants.GATE_RETRACTED;
        }
    }
    
    public void setToRobot()
    {
        robotOutput.setTiltMotor(tiltMotorSpeed);
        robotOutput.setGate(gateState);
    }
    
    public void tiltAdjustments(boolean increment, boolean decrement)
    {
        if(increment)
        {
            lowAngle += incrementSize;
            sideAngle += incrementSize;
            highAngle += incrementSize;
            farAngle += incrementSize / 2;
            closeAngle += incrementSize;
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
            farAngle -= decrementSize;
            closeAngle -= decrementSize / 2;
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
    
    public double getSetpoint()
    {
        return tiltPID.getSetpoint();
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
        farAngle = params.getAsDouble("T_ShootFarAngle", 0.0);
        closeAngle = params.getAsDouble("T_ShootCloseAngle", 0.0);
        feederStationAngle = params.getAsDouble("T_FeederStationAngle", 0.0);
        autonomousLowAngle = params.getAsDouble("A_RearLowAngle", lowAngle);
        madtownAngle = params.getAsDouble("T_MadtownAngle", lowAngle);
        visionAdditive = params.getAsDouble("T_VisionAdditive", 0.0);
        visionAdditiveFirst = params.getAsDouble("T_VisionAdditiveFirst", 0.0);
        visionAdditiveSecond = params.getAsDouble("T_VisionAdditiveSecond", 0.0);
        visionAdditiveThird = params.getAsDouble("T_VisionAdditiveThird", 0.0);
        visionAdditiveFourth = params.getAsDouble("T_VisionAdditiveFourth", 0.0);
        visionAdditiveFifth = params.getAsDouble("T_VisionAdditiveFifth", 0.0);
        SmartDashboard.putNumber("T_VisionAdditive", visionAdditive);
        SmartDashboard.putNumber("T_VisionAdditiveFirst", visionAdditiveFirst);
        SmartDashboard.putNumber("T_VisionAdditiveSecond", visionAdditiveSecond);
        SmartDashboard.putNumber("T_VisionAdditiveThird", visionAdditiveThird);
        SmartDashboard.putNumber("T_VisionAdditiveFourth", visionAdditiveFourth);
        SmartDashboard.putNumber("T_VisionAdditiveFifth", visionAdditiveFifth);
        
        tiltThreshold = params.getAsDouble("T_TiltMotorAdditive", 0.15);
        
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
