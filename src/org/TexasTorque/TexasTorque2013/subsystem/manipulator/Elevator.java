package org.TexasTorque.TexasTorque2013.subsystem.manipulator;

import edu.wpi.first.wpilibj.Timer;
import org.TexasTorque.TexasTorque2013.constants.Constants;
import org.TexasTorque.TexasTorque2013.io.DriverInput;
import org.TexasTorque.TexasTorque2013.io.RobotOutput;
import org.TexasTorque.TexasTorque2013.io.SensorInput;
import org.TexasTorque.TorqueLib.controlLoop.SimPID;
import org.TexasTorque.TorqueLib.controlLoop.TrajectorySmoother;
import org.TexasTorque.TorqueLib.util.DashboardManager;
import org.TexasTorque.TorqueLib.util.Parameters;
import org.TexasTorque.TorqueLib.util.TorqueLogging;
import org.TexasTorque.TorqueLib.util.TorqueSubsystem;

public class Elevator implements TorqueSubsystem
{
    private static Elevator instance;
    private RobotOutput robotOutput;
    private DriverInput driverInput;
    private SensorInput sensorInput;
    private TorqueLogging logging;
    private Parameters params;
    private DashboardManager dashboardManager;
    
    private TrajectorySmoother trajectory;
    private SimPID elevatorPID;
    
    private int desiredPosition;
    private double elevatorMotorSpeed;
    private double previousTime;
    
    public static double elevatorOverrideSpeed;
    public static int elevatorTopPosition;
    public static int elevatorBottomPosition;
    
    public static Elevator getInstance()
    {
        return (instance == null) ? instance = new Elevator() : instance;
    }
    
    public Elevator()
    {
        robotOutput = RobotOutput.getInstance();
        driverInput = DriverInput.getInstance();
        sensorInput = SensorInput.getInstance();
        logging = TorqueLogging.getInstance();
        params = Parameters.getInstance();
        dashboardManager = DashboardManager.getInstance();
        
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
        String data = elevatorMotorSpeed + ","
                + sensorInput.getElevatorEncoderVelocity() + ","
                + sensorInput.getElevatorEncoder() + ",";
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
        double maxV = params.getAsDouble("E_ElevatorMaxVelocity", 0.0);
        double maxA = params.getAsDouble("E_ElevatorMaxAcceleration", 0.0);
        
        trajectory = new TrajectorySmoother(maxA, maxV);
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
