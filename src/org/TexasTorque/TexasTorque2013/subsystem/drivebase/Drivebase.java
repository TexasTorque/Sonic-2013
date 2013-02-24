package org.TexasTorque.TexasTorque2013.subsystem.drivebase;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.TexasTorque.TexasTorque2013.TorqueSubsystem;
import org.TexasTorque.TexasTorque2013.constants.Constants;
import org.TexasTorque.TorqueLib.controlLoop.FeedforwardPIV;
import org.TexasTorque.TorqueLib.controlLoop.SimPID;
import org.TexasTorque.TorqueLib.controlLoop.TrajectorySmoother;

public class Drivebase extends TorqueSubsystem
{   
    private static Drivebase instance;
    
    private SimPID gyroPID;
    
    private TrajectorySmoother leftTrajectory;
    private TrajectorySmoother rightTrajectory;
    private FeedforwardPIV leftFeedForward;
    private FeedforwardPIV rightFeedForward;
    
    private double leftDriveSpeed;
    private double rightDriveSpeed;
    private double desiredGyroAngle;
    
    private double previousTime;
    
    public static double highSensitivity;
    public static double lowSensitivity;
    public static double leftMaxVelocity;
    public static double leftMaxAcceleration;
    public static double rightMaxVelocity;
    public static double rightMaxAcceleration;
    
    public static Drivebase getInstance()
    {
        return (instance == null) ? instance = new Drivebase() : instance;
    }
            
    private Drivebase()
    {
        super();
        
        gyroPID = new SimPID();
        
        leftFeedForward = new FeedforwardPIV();
        rightFeedForward = new FeedforwardPIV();
        
        leftDriveSpeed = Constants.MOTOR_STOPPED;
        rightDriveSpeed = Constants.MOTOR_STOPPED;
        desiredGyroAngle = 0.0;
        
        previousTime = Timer.getFPGATimestamp();
    }
    
    public void run()
    {
        double currentTime = Timer.getFPGATimestamp();
        double dt = currentTime - previousTime;
        previousTime = currentTime;
        
        if(!dashboardManager.getDS().isAutonomous())
        {
           //mixChannels(driverInput.getThrottle(), driverInput.getTurn());
           /*if(driverInput.shootVisionHigh() && SmartDashboard.getBoolean("found", false))
           {
               horizontallyTrack();
           }
           if(driverInput.shiftHighGear())
           {
               robotOutput.setShifters(true);
           }*/
            
            if(driverInput.runIntake())
            {
                double leftError = 100 - sensorInput.getLeftDriveEncoder();
                double rightError = 100 - sensorInput.getRightDriveEncoder();
                double leftVelocity = sensorInput.getLeftDriveEncoderRate();
                double rightVelocity = sensorInput.getRightDriveEncoderRate();
                
                leftTrajectory.update(leftError, leftVelocity, 0.0, dt);
                rightTrajectory.update(rightError, rightVelocity, 0.0, dt);
                
                leftDriveSpeed = leftFeedForward.calculate(leftTrajectory, leftError, leftVelocity, dt);
                rightDriveSpeed = rightFeedForward.calculate(rightTrajectory, rightError, rightVelocity, dt);
                
                robotOutput.setDriveMotors(leftDriveSpeed, rightDriveSpeed);
                SmartDashboard.putNumber("LeftError", leftError);
                SmartDashboard.putNumber("RightError", rightError);
                System.err.println("LP: " + sensorInput.getLeftDriveEncoder());
                System.err.println("RP: " + sensorInput.getRightDriveEncoder());
            }
            else
            {
                leftDriveSpeed = 0.0;
                rightDriveSpeed = 0.0;
                robotOutput.setDriveMotors(0.0, 0.0);
            }
            
            SmartDashboard.putNumber("LeftSpeed", leftDriveSpeed);
            SmartDashboard.putNumber("RightSpeed", rightDriveSpeed);
            SmartDashboard.putNumber("LeftVelocity", sensorInput.getLeftDriveEncoderRate());
            SmartDashboard.putNumber("RightVelocity", sensorInput.getRightDriveEncoderRate());
            SmartDashboard.putNumber("LeftGoalVelocity", leftTrajectory.getVelocity());
            SmartDashboard.putNumber("RightGoalVelocity", rightTrajectory.getVelocity());
            SmartDashboard.putNumber("LeftPosition", sensorInput.getLeftDriveEncoder());
            SmartDashboard.putNumber("RightPosition", sensorInput.getRightDriveEncoder());
            SmartDashboard.putNumber("Setpoint", 300);
            SmartDashboard.putNumber("LeftGoalAcceleration", leftTrajectory.getAcceleration());
            SmartDashboard.putNumber("RightGoalAcceleration", rightTrajectory.getAcceleration());
            SmartDashboard.putNumber("LeftAcceleration", sensorInput.getLeftDriveEncoderAcceleration());
            SmartDashboard.putNumber("RightAcceleration", sensorInput.getRightDriveEncoderAcceleration());
            
        }
        //robotOutput.setDriveMotors(leftDriveSpeed, rightDriveSpeed);
    }
    
    public synchronized String logData()
    {
        String data = leftDriveSpeed + ",";
        data += sensorInput.getLeftDriveEncoder() + ",";
        data += sensorInput.getLeftDriveEncoderRate() + ",";
        
        data += rightDriveSpeed + ",";
        data += sensorInput.getRightDriveEncoder() + ",";
        data += sensorInput.getRightDriveEncoderRate() + ",";
        
        data += sensorInput.getGyroAngle() + ",";
        
        return data;
    }
    
    public synchronized String getKeyNames()
    {
        String names = "LeftDriveSpeed,LeftDriveEncoderPosition,LeftDriveEncoderVelocity,"
                + "RightDriveSpeed,RightDriveEncoderPosition,RightDriveEncoderVelocity,"
                + "GyroAngle,";
        
        return names;
    }
    
    public synchronized void loadParameters()
    {
        highSensitivity = params.getAsDouble("D_HighSensitivity", Constants.DEFAULT_HIGH_SENSITIVITY);
        lowSensitivity = params.getAsDouble("D_LowSensitivity", Constants.DEFAULT_LOW_SENSITIVITY);
        leftMaxVelocity = params.getAsDouble("D_LeftMaxVelocity", 0.0);
        leftMaxAcceleration = params.getAsDouble("D_LeftMaxAcceleration", 0.0);
        rightMaxVelocity = params.getAsDouble("D_RightMaxVelocity", 0.0);
        rightMaxAcceleration = params.getAsDouble("D_RightMaxAcceleration", 0.0);
        
        double p = params.getAsDouble("D_GyroP", 0.0);
        double i = params.getAsDouble("D_GyroI", 0.0);
        double d =  params.getAsDouble("D_GyroD", 0.0);
        double e = params.getAsDouble("D_GyroEpsilon", 0.0);
        
        gyroPID.setConstants(p, i, d);
        gyroPID.setErrorEpsilon(e);
        gyroPID.resetErrorSum();
        gyroPID.resetPreviousVal();
        
        p = params.getAsDouble("D_LeftP", 0.0);
        i = params.getAsDouble("D_LeftI", 0.0);
        double v = params.getAsDouble("D_LeftV", 0.0);
        e = params.getAsDouble("D_LeftEpsilon", 0.0);
        double ffv = params.getAsDouble("D_LeftFFV", 0.0);
        double ffa = params.getAsDouble("D_LeftFFA", 0.0);
        
        leftFeedForward.setParams(p, i, v, ffv, ffa);
        leftFeedForward.setSetpoint(100);
        
        p = params.getAsDouble("D_RightP", 0.0);
        i = params.getAsDouble("D_RightI", 0.0);
        v = params.getAsDouble("D_RightV", 0.0);
        e = params.getAsDouble("D_RightEpsilon", 0.0);
        ffv = params.getAsDouble("D_RightFFV", 0.0);
        ffa = params.getAsDouble("D_RightFFA", 0.0);
        
        rightFeedForward.setParams(p, i, v, ffv, ffa);
        rightFeedForward.setSetpoint(100);
        
        loadNewTrajectory();
    }
    
    private synchronized void loadNewTrajectory()
    {
        leftTrajectory = new TrajectorySmoother(leftMaxAcceleration, leftMaxVelocity);
        rightTrajectory = new TrajectorySmoother(rightMaxAcceleration, rightMaxVelocity);
    }
    
    private synchronized void calcGyroPID()
    {
        desiredGyroAngle = (sensorInput.getGyroAngle() + SmartDashboard.getNumber("azimuth", 0.0));
        gyroPID.setDesiredValue(desiredGyroAngle);
        
        double motorOutput = gyroPID.calcPID(sensorInput.getGyroAngle());
        
        leftDriveSpeed = motorOutput;
        rightDriveSpeed = -motorOutput;
    }
    
    public synchronized void horizontallyTrack()
    {
        calcGyroPID();
    }
    
    public synchronized boolean isHorizontallyLocked()
    {
        return gyroPID.isDone();
    }
    
    private double applySqrtCurve(double axisValue)
    {
        int sign = (axisValue > 0) ? 1 : -1;
        axisValue = Math.sqrt(Math.abs(axisValue)) * sign;
        return axisValue;
    }
    
    private void mixChannels(double yAxis, double xAxis)
    {
        yAxis = driverInput.applyDeadband(yAxis, Constants.SPEED_AXIS_DEADBAND);
        xAxis = driverInput.applyDeadband(xAxis, Constants.TURN_AXIS_DEADBAND);
        
        simpleDrive(yAxis, xAxis);
        //cheesyDrive(yAxis, xAxis);
    }
    
    private void simpleDrive(double yAxis, double xAxis)
    {
        yAxis = applySqrtCurve(yAxis);
        xAxis = applySqrtCurve(xAxis);
        
        leftDriveSpeed = yAxis + xAxis;
        rightDriveSpeed = yAxis - xAxis;
    }
    
    private void cheesyDrive(double throttle, double turn)
    {
        throttle = applySqrtCurve(throttle);
        turn = applySqrtCurve(turn);
        
        double power;
        double trueSpeed;
        double RadiusOuter = Constants.MAX_DIAMETER / 2;
        double RadiusInner = (Constants.MAX_DIAMETER - Constants.ROBOT_WIDTH) / 2;
        double SpeedInner = 0.0;
        if(!driverInput.shiftHighGear())
        {
            turn = turn * lowSensitivity;
        }
        else
        {
            turn = turn * highSensitivity;
        }
        if(turn == 0.0)
        {
            SpeedInner = throttle;
        }
        else
        {
            SpeedInner = throttle * (RadiusInner / RadiusOuter);
        }
        power = SpeedInner;
        trueSpeed = throttle;
        if(power > 1.0)
        {
            trueSpeed -= (power - 1.0);
            power = 1.0;
        }
        if(trueSpeed > 1.0)
        {
            power -= (trueSpeed - 1.0);
            trueSpeed = 1.0;
        }
        if(power < -1.0)
        {
            trueSpeed += (1.0 + power);
            power = -1.0;
        }
        if(trueSpeed < -1.0)
        {
            power += (-1.0 - trueSpeed);
            trueSpeed = -1.0;
        }
        if(throttle == 0 && Math.abs(turn) > 0.5)
        {
            power = turn;
            leftDriveSpeed = power;
            rightDriveSpeed = -power;
        }
        else if(throttle > 0)
        {
            if(turn > 0)
            {
                leftDriveSpeed = -power;
                rightDriveSpeed = -trueSpeed;
            }
            else
            {
                leftDriveSpeed = -trueSpeed;
                rightDriveSpeed = -power;
            }
        }
        else
        {
            if(turn > 0)
            {
                leftDriveSpeed = -trueSpeed;
                rightDriveSpeed = -power;
            }
            else
            {
                leftDriveSpeed = -power;
                rightDriveSpeed = -trueSpeed;
            }
        }
        
    }
}
