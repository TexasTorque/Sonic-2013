package org.TexasTorque.TexasTorque2013.subsystem.manipulator;

import edu.wpi.first.wpilibj.Timer;
import org.TexasTorque.TexasTorque2013.constants.Constants;
import org.TexasTorque.TexasTorque2013.io.DriverInput;
import org.TexasTorque.TexasTorque2013.io.RobotOutput;
import org.TexasTorque.TexasTorque2013.io.SensorInput;
import org.TexasTorque.TorqueLib.util.FeedforwardPIV;
import org.TexasTorque.TorqueLib.util.Parameters;
import org.TexasTorque.TorqueLib.util.SimPID;
import org.TexasTorque.TorqueLib.util.TorqueLogging;
import org.TexasTorque.TorqueLib.util.TrajectorySmoother;

public class Shooter extends FeedforwardPIV
{
    
    private static Shooter instance;
    private RobotOutput robotOutput;
    private DriverInput driverInput;
    private SensorInput sensorInput;
    private TorqueLogging logging;
    private Parameters params;
    
    private SimPID frontShooterPID;
    private SimPID rearShooterPID;
    
    private SimPID tiltLockPID;
    private TrajectorySmoother trajectory;
    
    private double frontMotorSpeed;
    private double rearMotorSpeed;
    private double desiredFrontShooterRate;
    private double desiredRearShooterRate;
    
    private double tiltMotorSpeed;
    private double desiredTiltPosition;
    private int tiltState;
    private double tiltEpsilon;
    
    private double previousTime;
    private boolean firstIteration;
    private double previousAngle;
    private double tiltVelocity;
    
    public static Shooter getInstance()
    {
        return (instance == null) ? instance = new Shooter() : instance;
    }
    
    public Shooter()
    {
        robotOutput = RobotOutput.getInstance();
        driverInput = DriverInput.getInstance();
        sensorInput = SensorInput.getInstance();
        logging = TorqueLogging.getInstance();
        params = Parameters.getInstance();
        
        frontShooterPID = new SimPID();
        rearShooterPID = new SimPID();
        
        tiltLockPID = new SimPID();
        
        loadTiltLockPID();
        loadTiltPIV();
        loadFrontShooterPID();
        loadRearShooterPID();
        
        frontMotorSpeed = Constants.MOTOR_STOPPED;
        rearMotorSpeed = Constants.MOTOR_STOPPED;
        desiredFrontShooterRate = Constants.SHOOTER_STOPPED_RATE;
        desiredRearShooterRate = Constants.SHOOTER_STOPPED_RATE;
        
        desiredTiltPosition = Constants.DEFAULT_STANDARD_TILT_POSITION;
        tiltMotorSpeed = Constants.MOTOR_STOPPED;
        tiltState = Constants.TILT_MOVING_STATE;
        
        previousTime = Timer.getFPGATimestamp();
        previousAngle = 0.0;
        firstIteration = true;
    }
    
    public void run()
    {
        double currentTime = Timer.getFPGATimestamp();
        double dt = currentTime - previousTime;
        previousTime = currentTime;
        
        if(tiltState == Constants.TILT_MOVING_STATE && !firstIteration)
        {
            double position = sensorInput.getTiltAngle();
            double dAngle = position - previousAngle;
            double velocity = dAngle / dt;
            previousAngle = position;
            
            tiltVelocity = velocity;
            
            trajectory.update(position, velocity, Constants.MOTOR_STOPPED, dt);
            
            tiltMotorSpeed = calculate(trajectory, position, velocity, dt);
            
            if(onTarget(tiltEpsilon))
            {
                tiltState = Constants.TILT_LOCKED_STATE;
                
                tiltLockPID.resetErrorSum();
                tiltLockPID.resetPreviousVal();
            }
        }
        else if(tiltState == Constants.TILT_LOCKED_STATE)
        {
            tiltLockPID.setDesiredValue(desiredTiltPosition);
            tiltMotorSpeed = tiltLockPID.calcPID(sensorInput.getTiltAngle());
        }
        else
        {
            firstIteration = false;
            tiltMotorSpeed = 0.0;
        }

        frontShooterPID.setDesiredValue(desiredFrontShooterRate);
        rearShooterPID.setDesiredValue(desiredRearShooterRate);
        
        double frontSpeed = frontShooterPID.calcPID(sensorInput.getFrontShooterRate());
        double rearSpeed = rearShooterPID.calcPID(sensorInput.getRearShooterRate());
        
        frontMotorSpeed = limitShooterSpeed(frontSpeed);
        rearMotorSpeed = limitShooterSpeed(rearSpeed);
        
        robotOutput.setShooterMotors(frontMotorSpeed, rearMotorSpeed);
        robotOutput.setShooterTiltMotor(tiltMotorSpeed);
    }
    
    public synchronized void logData()
    {
        logging.logValue("DesiredFrontShooterRate", desiredFrontShooterRate);
        logging.logValue("FrontShooterMotorSpeed", frontMotorSpeed);
        logging.logValue("ActualFrontShooterRate", sensorInput.getFrontShooterRate());
        logging.logValue("DesiredRearShooterRate", desiredRearShooterRate);
        logging.logValue("RearShooterMotorSpeed", rearMotorSpeed);
        logging.logValue("ActualRearShooterRate", sensorInput.getRearShooterRate());
        
        logging.logValue("TiltMotorSpeed", tiltMotorSpeed);
        logging.logValue("CurrentAngle", sensorInput.getTiltAngle());
        logging.logValue("DesiredAngle", desiredTiltPosition);
        logging.logValue("TiltVelocity", tiltVelocity);
        logging.logValue("GoalTiltVelocity", getSetpoint());
    }
    
    public synchronized void setShooterRates(double frontRate, double rearRate)
    {
        desiredFrontShooterRate = frontRate;
        desiredRearShooterRate = rearRate;
    }
    
    public synchronized void setTiltAngle(double degrees)
    {
        if(degrees != desiredTiltPosition)
        {
            desiredTiltPosition = degrees;
            tiltState = Constants.TILT_MOVING_STATE;
        }
    }
    
    public synchronized void loadFrontShooterPID()
    {
        double p = params.getAsDouble("S_FrontShooterP", 0.0);
        double i = params.getAsDouble("S_FrontShooterI", 0.0);
        double d = params.getAsDouble("S_FrontShooterD", 0.0);
        double e = params.getAsDouble("S_FrontShooterEpsilon", 0.0);
        
        frontShooterPID.setConstants(p, i, d);
        frontShooterPID.setErrorEpsilon(e);
        frontShooterPID.resetErrorSum();
        frontShooterPID.resetPreviousVal();
    }
    
    public synchronized void loadRearShooterPID()
    {
        double p = params.getAsDouble("S_RearShooterP", 0.0);
        double i = params.getAsDouble("S_RearShooterI", 0.0);
        double d = params.getAsDouble("S_RearShooterD", 0.0);
        double e = params.getAsDouble("S_RearShooterEpsilon", 0.0);
        
        rearShooterPID.setConstants(p, i, d);
        rearShooterPID.setErrorEpsilon(e);
        rearShooterPID.resetErrorSum();
        rearShooterPID.resetPreviousVal();
    }
    
    public synchronized void loadTiltLockPID()
    {
        double p = params.getAsDouble("S_TiltLockP", 0.0);
        double i = params.getAsDouble("S_TiltLockI", 0.0);
        double d = params.getAsDouble("S_TiltLockD", 0.0);
        double e = params.getAsDouble("S_TiltLockEpsilon", 0.0);
        
        tiltLockPID.setConstants(p, i, d);
        tiltLockPID.setErrorEpsilon(e);
        tiltLockPID.resetErrorSum();
        tiltLockPID.resetPreviousVal();
    }
    
    public synchronized void loadTiltPIV()
    {
        double maxAccel = params.getAsDouble("S_TiltMaxAcceleration", 0.0);
        double maxVel = params.getAsDouble("S_TiltVelocity", 0.0);
        
        trajectory = new TrajectorySmoother(maxAccel, maxVel);
        
        double p = params.getAsDouble("S_TiltP", 0.0);
        double i = params.getAsDouble("S_TiltI", 0.0);
        double v = params.getAsDouble("S_TiltD", 0.0);
        double ffv = params.getAsDouble("S_TiltFFV", 0.0);
        double ffa = params.getAsDouble("S_TiltFFA", 0.0);
        
        tiltEpsilon = params.getAsDouble("S_TiltEpsilon", 0.0);
        
        setParams(p, i, v, ffv, ffa);
    }
    
    public synchronized boolean isVerticallyLocked()
    {
        return (tiltState == Constants.TILT_LOCKED_STATE && tiltLockPID.isDone());
    }
    
    public synchronized boolean isAtStandardPosition()
    {
        double standardPosition = params.getAsDouble("S_TiltStandardAngle", 0.0);
        
        return (desiredTiltPosition == standardPosition && isVerticallyLocked());
    }
    
    public synchronized boolean isReadyToFire()
    {
        return (isVerticallyLocked() && frontShooterPID.isDone() && rearShooterPID.isDone());
    }
    
    private double limitShooterSpeed(double shooterSpeed)
    {
        if(shooterSpeed < 0.0)
        {
            return Constants.MOTOR_STOPPED;
        }
        else
        {
            return shooterSpeed;
        }
    }
    
}
