package org.TexasTorque.TexasTorque2013.subsystem.drivebase;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.TexasTorque.TexasTorque2013.TorqueSubsystem;
import org.TexasTorque.TexasTorque2013.constants.Constants;
import org.TexasTorque.TexasTorque2013.subsystem.manipulator.Climber;
import org.TexasTorque.TorqueLib.controlLoop.TorquePID;
import org.TexasTorque.TorqueLib.util.TorqueUtil;

public class Drivebase extends TorqueSubsystem
{   
    private static Drivebase instance;
    
    private TorquePID visionCorrect;
    private int visionWait;
    private int visionCycleDelay;
    private int visionInitialDelay;
    private int tempInitialDelay;
    private double turnAdditive;
    private boolean visionEnable;
    private double pastAzimuth;
    
    private Climber climber;
    
    private double leftDriveSpeed;
    private double rightDriveSpeed;
    
    private boolean shiftState;
    
    public static Drivebase getInstance()
    {
        return (instance == null) ? instance = new Drivebase() : instance;
    }
            
    private Drivebase()
    {
        super();
        
        climber = Climber.getInstance();
        
        leftDriveSpeed = Constants.MOTOR_STOPPED;
        rightDriveSpeed = Constants.MOTOR_STOPPED;
        
        shiftState = Constants.LOW_GEAR;
        
        visionCorrect = new TorquePID();
        visionCorrect.setSetpoint(0.0);
    }
    
    public void run()
    {
        if(dashboardManager.getDS().isOperatorControl())
        {
           
            if(!visionEnable || !driverInput.getAutoTargeting() || driverInput.hasInput())// Uncomment for vision lock left right
            {
                mixChannels(driverInput.getThrottle(), driverInput.getTurn());
                tempInitialDelay = visionInitialDelay;
            }
            else
            {
                if(tempInitialDelay > 0)
                {
                    tempInitialDelay --;
                    visionCorrect.setSetpoint(sensorInput.getGyroAngle());
                }
                visionWait = (visionWait + 1) % visionCycleDelay;
                if(SmartDashboard.getBoolean("found",false) && visionWait == 0 && tempInitialDelay == 0)
                {
                double az = SmartDashboard.getNumber("azimuth",0.0);
                if(az>180)
                {
                    az -= 360; //Angle correction Expanded: az = -(360 - az)
                }
                if(pastAzimuth != az)
                {
                    calcAngleCorrection(az);
                    pastAzimuth = az;
                }
                //double output = visionCorrect.calculate(az);
                //Should use the same style as verticle tracking, pulling every V_CycleDelay, 5 cycles and then adjusting via the gyro
                //mixTurn(output);
                }
                double output = calcAngleCorrection();
                mixTurn(output);
            }
           
           shiftState = driverInput.shiftHighGear();
           
           if(climber.isHanging())
           {
               shiftState = Constants.LOW_GEAR;
               
               leftDriveSpeed *= 0.4;
               rightDriveSpeed *= 0.4;
           }
        }
    }
    
    public void setPIDConstants(double p, double i, double d)
    {
        visionCorrect.setPIDGains(p, i, d);
    }
    
    public void calcAngleCorrection(double azimuth)
    {
        azimuth = -1 * azimuth;
        double desiredAngle = sensorInput.getGyroAngle() + azimuth + turnAdditive;//first check is to flip negative
        visionCorrect.setSetpoint(desiredAngle);
    }
    public double calcAngleCorrection()
    {
        return visionCorrect.calculate(sensorInput.getGyroAngle());
    }
    public boolean isLocked()
    {
        if(!visionEnable || !driverInput.getAutoTargeting() || driverInput.hasInput())
        {
            return true;
        }
        else
        {
            return visionCorrect.isDone();
        }
    }
    
    public void mixTurn(double t)
    {
        leftDriveSpeed = -t;
        rightDriveSpeed = t;
    }
    
    public void setToRobot()
    {
        robotOutput.setShifters(shiftState);
        robotOutput.setDriveMotors(leftDriveSpeed, rightDriveSpeed);
    }
    
    public void setDriveSpeeds(double leftSpeed, double rightSpeed)
    {
        leftDriveSpeed = leftSpeed;
        rightDriveSpeed = rightSpeed;
    }
    
    public void setShifters(boolean highGear)
    {
        if(highGear != shiftState)
        {
            shiftState = highGear;
        }
    }
    
    private void mixChannels(double yAxis, double xAxis)
    {
        yAxis = TorqueUtil.applyDeadband(yAxis, Constants.SPEED_AXIS_DEADBAND);
        xAxis = TorqueUtil.applyDeadband(xAxis, Constants.TURN_AXIS_DEADBAND);
        
        simpleDrive(yAxis, xAxis);
    }
    
    private void simpleDrive(double yAxis, double xAxis)
    {
        yAxis = TorqueUtil.sqrtHoldSign(yAxis);
        xAxis = TorqueUtil.sqrtHoldSign(xAxis);
        
        double leftSpeed = yAxis + xAxis;
        double rightSpeed = yAxis - xAxis;
        
        setDriveSpeeds(leftSpeed, rightSpeed);
    }
    
    public String getKeyNames()
    {
        String names = "LeftDriveSpeed,LeftDriveEncoderPosition,LeftDriveEncoderVelocity,"
                + "RightDriveSpeed,RightDriveEncoderPosition,RightDriveEncoderVelocity,"
                + "GyroAngle,ShiftState";
        
        return names;
    }
    
    public String logData()
    {
        String data = leftDriveSpeed + ",";
        data += sensorInput.getLeftDriveEncoder() + ",";
        data += sensorInput.getLeftDriveEncoderRate() + ",";
        
        data += rightDriveSpeed + ",";
        data += sensorInput.getRightDriveEncoder() + ",";
        data += sensorInput.getRightDriveEncoderRate() + ",";
        
        data += sensorInput.getGyroAngle() + ",";
        data += shiftState + ",";
        
        return data;
    }
    
    public void loadParameters()
    {   
        visionInitialDelay = params.getAsInt("V_InitialDelay", 70);
        visionCycleDelay = params.getAsInt("V_CycleDelay", 5);
        visionEnable = params.getAsBoolean("V_Horizontal", false);
        turnAdditive = params.getAsDouble("V_TurnAdditive", 0.0);
        
        double p = params.getAsDouble("V_TurnP", 0.0);
        double i = params.getAsDouble("V_TurnI", 0.0);
        double d = params.getAsDouble("V_TurnD", 0.0);
        double e = params.getAsDouble("V_TurnEpsilon", 0.0);
        double r = params.getAsDouble("V_TurnDoneRange", 0.0);
        
        visionCorrect.setPIDGains(p, i, d);
        visionCorrect.setEpsilon(e);
        visionCorrect.setDoneRange(r);
        visionCorrect.reset();
        
    }
}
