package org.TexasTorque.TexasTorque2013.subsystem.manipulator;

import edu.wpi.first.wpilibj.Timer;
import org.TexasTorque.TexasTorque2013.TorqueSubsystem;
import org.TexasTorque.TexasTorque2013.constants.Constants;
import org.TexasTorque.TorqueLib.controlLoop.SimPID;
import org.TexasTorque.TorqueLib.controlLoop.TrajectorySmoother;

public class Elevator extends TorqueSubsystem
{
    private TrajectorySmoother trajectory;
    private SimPID elevatorPID;
    
    private int desiredPosition;
    private double elevatorMotorSpeed;
    private double previousTime;
    
    public static double elevatorOverrideSpeed;
    public static int elevatorTopPosition;
    public static int elevatorBottomPosition;
    public static double maxElevatorVelocity;
    public static double maxElevatorAcceleration;
    
    public static TorqueSubsystem getInstance()
    {
        return (instance == null) ? instance = new Elevator() : instance;
    }
    
    private Elevator()
    {
        super();
        
        elevatorPID = new SimPID();
        
        loadNewTrajectory();
        
        desiredPosition = 0;
        elevatorMotorSpeed = 0.0;
        previousTime = Timer.getFPGATimestamp();
    }
    
    public void run()
    {
        double currentTime = Timer.getFPGATimestamp();
        double dt = currentTime - previousTime;
        previousTime = currentTime;
        
        double error = desiredPosition - sensorInput.getElevatorEncoder();
        double velocity = sensorInput.getElevatorEncoderVelocity();
        
        trajectory.update(error, velocity , 0.0, dt);
        elevatorPID.setDesiredValue(trajectory.getVelocity());
        
        elevatorMotorSpeed = elevatorPID.calcPID(sensorInput.getElevatorEncoderVelocity());
        
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
        double d = params.getAsDouble("E_ElevatorD", 0.0);
        int e = params.getAsInt("E_ElevatorEpsilon", 0);
        
        elevatorPID.setConstants(p, i, d);
        elevatorPID.setErrorEpsilon(e);
        elevatorPID.resetErrorSum();
        elevatorPID.resetPreviousVal();
        
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
            loadNewTrajectory();
        }
    }
    
    public synchronized boolean elevatorAtTop()
    {
        return (desiredPosition == elevatorTopPosition && elevatorPID.isDone());
    }
    
    public synchronized boolean elevatorAtBottom()
    {
        return (desiredPosition == elevatorBottomPosition && elevatorPID.isDone());
    }
}
