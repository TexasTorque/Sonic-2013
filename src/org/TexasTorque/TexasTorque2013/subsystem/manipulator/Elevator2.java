package org.TexasTorque.TexasTorque2013.subsystem.manipulator;

import edu.wpi.first.wpilibj.Timer;
import org.TexasTorque.TexasTorque2013.constants.Constants;
import org.TexasTorque.TexasTorque2013.io.DriverInput;
import org.TexasTorque.TexasTorque2013.io.RobotOutput;
import org.TexasTorque.TexasTorque2013.io.SensorInput;
import org.TexasTorque.TorqueLib.util.DashboardManager;
import org.TexasTorque.TorqueLib.util.FeedforwardPIV;
import org.TexasTorque.TorqueLib.util.Parameters;
import org.TexasTorque.TorqueLib.util.SimPID;
import org.TexasTorque.TorqueLib.util.TorqueLogging;
import org.TexasTorque.TorqueLib.util.TrajectorySmoother;

public class Elevator2 extends FeedforwardPIV
{
    private static Elevator2 instance;
    private RobotOutput robotOutput;
    private DriverInput driverInput;
    private SensorInput sensorInput;
    private TorqueLogging logging;
    private Parameters params;
    private DashboardManager dashboardManager;
    
    private TrajectorySmoother trajectory;
    private SimPID elevatorLockPID;
    
    private int desiredPosition;
    private double elevatorMotorSpeed;
    private int elevatorEpsilon;
    private int elevatorState;
    private double previousTime;
    private boolean firstIteration;
    
    public static Elevator2 getInstance()
    {
        return (instance == null) ? instance = new Elevator2() : instance;
    }
    
    public Elevator2()
    {
        robotOutput = RobotOutput.getInstance();
        driverInput = DriverInput.getInstance();
        sensorInput = SensorInput.getInstance();
        logging = TorqueLogging.getInstance();
        params = Parameters.getInstance();
        dashboardManager = DashboardManager.getInstance();
        
        elevatorLockPID = new SimPID();
        
        loadElevatorLockPID();
        loadElevatorPIV();
        
        desiredPosition = 0;
        elevatorMotorSpeed = 0.0;
        elevatorState = Constants.ELEVATOR_MOVING_STATE;
        previousTime = Timer.getFPGATimestamp();
        firstIteration = true;
    }
    
    public void run()
    {
        double currentTime = Timer.getFPGATimestamp();
        double dt = currentTime - previousTime;
        previousTime = currentTime;
        if(elevatorState == Constants.ELEVATOR_MOVING_STATE && !firstIteration)
        {
            double position  = sensorInput.getElevatorEncoder();
            double velocity = sensorInput.getElevatorEncoderVelocity();
            
            trajectory.update(position, velocity, Constants.MOTOR_STOPPED, dt);
            
            elevatorMotorSpeed = calculate(trajectory, position, velocity, dt);
            
            if(onTarget(elevatorEpsilon))
            {
                elevatorState = Constants.ELEVATOR_LOCKED_STATE;
                elevatorLockPID.resetErrorSum();
                elevatorLockPID.resetPreviousVal();
            }
        }
        else if(elevatorState == Constants.ELEVATOR_LOCKED_STATE)
        {
            elevatorLockPID.setDesiredValue(desiredPosition);
            elevatorMotorSpeed = elevatorLockPID.calcPID(sensorInput.getElevatorEncoder());
        }
        else
        {
            firstIteration = false;
            elevatorMotorSpeed = 0.0;
        }
        robotOutput.setElevatorMotors(elevatorMotorSpeed);
    }
    
    public synchronized void reset()
    {
        
    }
    
    public synchronized void logData()
    {
        
    }
    
    public synchronized void loadElevatorLockPID()
    {
        double p = params.getAsDouble("E_ElevatorLockP", 0.0);
        double i = params.getAsDouble("E_ElevatorLockI", 0.0);
        double d = params.getAsDouble("E_ElevatorLockD", 0.0);
        double e = params.getAsDouble("E_ElevatorLockEpsilon", 0.0);
        
        elevatorLockPID.setConstants(p, i, d);
        elevatorLockPID.setErrorEpsilon(e);
        elevatorLockPID.resetErrorSum();
        elevatorLockPID.resetPreviousVal();
    }
    
    public synchronized void loadElevatorPIV()
    {
        double maxAccel = params.getAsDouble("E_MaxAcceleration", 0.0);
        double maxVel = params.getAsDouble("E_MaxVelocity", 0.0);
        trajectory = new TrajectorySmoother(maxAccel, maxVel);
        double p = params.getAsDouble("E_ElevatorP", 0.0);
        double i = params.getAsDouble("E_ElevatorI", 0.0);
        double v = params.getAsDouble("E_ElevatorV", 0.0);
        double ffv = params.getAsDouble("E_ElevatorFFV", 0.0);
        double ffa = params.getAsDouble("E_ElevatorFFA", 0.0);
        elevatorEpsilon = params.getAsInt("E_ElevatorEpsilon", 0);
        
        setParams(p, i, v, ffa, ffa);
    }
    
    public synchronized void setDesiredPosition(int position)
    {
        if(position != desiredPosition)
        {
            desiredPosition = position;
            elevatorState = Constants.ELEVATOR_MOVING_STATE;
        }
    }
    
    public synchronized boolean elevatorAtTop()
    {
        int topPosition  = params.getAsInt("E_ElevatorTopPosition", Constants.DEFAULT_ELEVATOR_TOP_POSITION);
        
        return (desiredPosition == topPosition && elevatorState == Constants.ELEVATOR_LOCKED_STATE && elevatorLockPID.isDone());
    }
    
    public synchronized boolean elevatorAtBottom()
    {
        int bottomPosition  = params.getAsInt("E_ElevatorBottomPosition", Constants.DEFAULT_ELEVATOR_BOTTOM_POSITION);
        
        return (desiredPosition == bottomPosition && elevatorState == Constants.ELEVATOR_LOCKED_STATE && elevatorLockPID.isDone());
    }
}
