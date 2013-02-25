package org.TexasTorque.TexasTorque2013.subsystem.manipulator;

import edu.wpi.first.wpilibj.Timer;
import org.TexasTorque.TexasTorque2013.TorqueSubsystem;
import org.TexasTorque.TexasTorque2013.constants.Constants;
import org.TexasTorque.TorqueLib.controlLoop.FeedforwardPIV;
import org.TexasTorque.TorqueLib.controlLoop.TrajectorySmoother;

public class Elevator extends TorqueSubsystem
{
    private static Elevator instance;
    
    public TrajectorySmoother trajectory;
    private FeedforwardPIV feedForward;
    
    private int desiredPosition;
    public double elevatorMotorSpeed;
    private double previousTime;
    private int elevatorEpsilon;
    
    public static double elevatorOverrideSpeed;
    public static int elevatorTopPosition;
    public static int elevatorBottomPosition;
    public static double maxElevatorVelocity;
    public static double maxElevatorAcceleration;
    
    public static Elevator getInstance()
    {
        return (instance == null) ? instance = new Elevator() : instance;
    }
    
    private Elevator()
    {
        super();
        
        feedForward = new FeedforwardPIV();
        
        loadNewTrajectory();
        
        previousTime = Timer.getFPGATimestamp();
        
        desiredPosition = Constants.DEFAULT_ELEVATOR_BOTTOM_POSITION;
        elevatorMotorSpeed = Constants.MOTOR_STOPPED;
        elevatorEpsilon = 0;
    }
    
    public void run()
    {
        double currentTime = Timer.getFPGATimestamp();
        double dt = currentTime - previousTime;
        previousTime = currentTime;
        
        double error = desiredPosition - sensorInput.getElevatorEncoder();
        double velocity = sensorInput.getElevatorEncoderVelocity();
        
        trajectory.update(error, velocity , 0.0, dt);
        
        elevatorMotorSpeed = feedForward.calculate(trajectory, error, velocity, dt);
        
        robotOutput.setElevatorMotors(elevatorMotorSpeed);
    }
    
    public synchronized String logData()
    {
        String data = elevatorMotorSpeed + ",";
        data += sensorInput.getElevatorEncoderVelocity() + ",";
        data += sensorInput.getElevatorEncoder() + ",";
        if(trajectory != null)
        {
            data += trajectory.getVelocity() + ",";
        }
        else
        {
            data += "Trajectory is null,";
        }
        return data;
    }
    
    public synchronized String getKeyNames()
    {
        String names = "ElevatorMotorSpeed,ElevatorVelocity,ElevatorPosition,ElevatorGoalVelocity,";
        
        return names;
    }
    
    public synchronized void loadParameters()
    {
        elevatorOverrideSpeed = params.getAsDouble("E_ElevatorOverrideSpeed", 0.7);
        elevatorTopPosition = params.getAsInt("E_ElevatorTopPosition", Constants.DEFAULT_ELEVATOR_TOP_POSITION);
        elevatorBottomPosition = params.getAsInt("E_ElevatorBottomPosition", Constants.DEFAULT_ELEVATOR_BOTTOM_POSITION);
        
        maxElevatorVelocity = params.getAsDouble("E_ElevatorMaxVelocity", 0.0);
        maxElevatorAcceleration = params.getAsDouble("E_ElevatorMaxAcceleration", 0.0);
        
        double p = params.getAsDouble("E_ElevatorP", 0.0);
        double i = params.getAsDouble("E_ElevatorI", 0.0);
        double v = params.getAsDouble("E_ElevatorV", 0.0);
        int e = params.getAsInt("E_ElevatorEpsilon", 0);
        double ffv = params.getAsDouble("E_ElevatorFFV", 0.0);
        double ffa = params.getAsDouble("E_ElevatorFFA", 0.0);
        
        feedForward.setParams(p, i, v, ffv, ffa);
        
        elevatorEpsilon = e;
        
        loadNewTrajectory();
    }
    
    private synchronized void loadNewTrajectory()
    {
        trajectory = new TrajectorySmoother(maxElevatorAcceleration, maxElevatorVelocity);
    }
    
    public synchronized void setDesiredPosition(int position)
    {
        if(position != desiredPosition)
        {
            desiredPosition = position;
            feedForward.setSetpoint(desiredPosition);
            loadNewTrajectory();
        }
    }
    
    public synchronized boolean elevatorAtTop()
    {
        return (desiredPosition == elevatorTopPosition && feedForward.onTarget(elevatorEpsilon));
    }
    
    public synchronized boolean elevatorAtBottom()
    {
        return (desiredPosition == elevatorBottomPosition && feedForward.onTarget(elevatorEpsilon));
    }
}
