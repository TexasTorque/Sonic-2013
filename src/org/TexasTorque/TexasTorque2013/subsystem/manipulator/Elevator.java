package org.TexasTorque.TexasTorque2013.subsystem.manipulator;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.TexasTorque.TexasTorque2013.constants.Constants;
import org.TexasTorque.TexasTorque2013.io.DriverInput;
import org.TexasTorque.TexasTorque2013.io.RobotOutput;
import org.TexasTorque.TexasTorque2013.io.SensorInput;
import org.TexasTorque.TorqueLib.util.DashboardManager;
import org.TexasTorque.TorqueLib.util.Parameters;
import org.TexasTorque.TorqueLib.util.SimPID;
import org.TexasTorque.TorqueLib.util.TorqueLogging;
import org.TexasTorque.TorqueLib.util.TorqueTrapezoidal;

public class Elevator
{
    
    private static Elevator instance;
    private RobotOutput robotOutput;
    private DriverInput driverInput;
    private SensorInput sensorInput;
    private TorqueLogging logging;
    private Parameters params;
    private DashboardManager dashboardManager;
    
    private double elevatorMotorSpeed;
    private int desiredElevatorPosition;
    private boolean isLocking; //R/ Tommy not sure if you need this

    private TorqueTrapezoidal elevatorTrapezoidal;
    private SimPID elevatorLockPID;
    private int elevatorState;
    
    private int elevatorTopPosition;
    private int elevatorBottomPosition;
    
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
        elevatorTrapezoidal = new TorqueTrapezoidal(sensorInput.elevatorEncoder());
        
        double p = params.getAsDouble("E_ElevatorLockP", 0.0);
        double i = params.getAsDouble("E_ElevatorLockI", 0.0);
        double d = params.getAsDouble("E_ElevatorLockD", 0.0);
        double e = params.getAsDouble("E_ElevatorLockEpsilon", 0.0);
        
        elevatorLockPID = new SimPID(p, i, d, e);
        
        p = params.getAsDouble("E_ElevatorTrapP", 0.0);
        i = params.getAsDouble("E_ElevatorTrapI", 0.0);
        d = params.getAsDouble("E_ElevatorTrapD", 0.0);
        e = params.getAsDouble("E_ElevatorTrapEpsilon", 0.0);
        
        elevatorTrapezoidal.setPIDOptions(p, i, d, e);
        
        double xEpsilon = params.getAsDouble("E_ElevatorTrapXEpsilon", 0.0);
        double maxV = params.getAsDouble("E_ElevatorTrapMaxVelocity", 0.0);
        double maxA = params.getAsDouble("E_ElevatorTrapMaxAcceleration", 0.0);
        double vFactor = params.getAsDouble("E_ElevatorTrapVelocityFactor", 0.0);
        double aFactor = params.getAsDouble("E_ElevatorTrapAccelerationFactor", 0.0);
        
        elevatorTrapezoidal.setTrapezoidOptions(xEpsilon, maxV, maxA, vFactor, aFactor);
        
        p = params.getAsDouble("E_ElevatorLockP", 0.0);
        i = params.getAsDouble("E_ElevatorLockI", 0.0);
        d = params.getAsDouble("E_ElevatorLockD", 0.0);
        e = params.getAsDouble("E_ElevatorLockEpsilon", 0.0);
        
        elevatorLockPID = new SimPID(p, i, d, e);
        
        elevatorMotorSpeed = Constants.MOTOR_STOPPED;
        
        elevatorTopPosition = params.getAsInt("E_ElevatorTopPosition", Constants.DEFAULT_ELEVATOR_TOP_POSITION);
        elevatorBottomPosition = params.getAsInt("E_ElevatorBottomPosition", Constants.DEFAULT_ELEVATOR_BOTTOM_POSITION);
        isLocking = false;
        
        elevatorUpPID.setDesiredValue(elevatorTopPosition);
        elevatorDownPID.setDesiredValue(elevatorBottomPosition);
        
        desiredElevatorPosition = elevatorBottomPosition;
        elevatorState = 0;
        elevatorTrapezoidal.start();
    }
    
    public void run()
    {
        
        elevatorTopPosition = params.getAsInt("E_ElevatorTopPosition", Constants.DEFAULT_ELEVATOR_TOP_POSITION);
        elevatorBottomPosition = params.getAsInt("E_ElevatorBottomPosition", Constants.DEFAULT_ELEVATOR_BOTTOM_POSITION);
        
//R/        int encoderPosition = sensorInput.getElevatorEncoder();
//R/        
//R/        if(desiredElevatorPosition == elevatorTopPosition)
//R/        {
//R/            if(elevatorUpPID.isDone() && encoderPosition > 740)
//R/            {
//R/                isLocking = true;
//R/            }
//R/            if(isLocking)
//R/            {
//R/                elevatorLockPID.setDesiredValue(desiredElevatorPosition);
//R/                elevatorMotorSpeed = elevatorLockPID.calcPID(sensorInput.getElevatorEncoder());
//R/            }
//R/            else
//R/            {
//R/                isLocking = false;
//R/                elevatorUpPID.setDesiredValue(desiredElevatorPosition);
//R/                elevatorMotorSpeed = elevatorUpPID.calcPID(sensorInput.getElevatorEncoder());
        if(elevatorState == Constants.ELEVATOR_MOVING_STATE)
        {
            elevatorMotorSpeed = elevatorTrapezoidal.getMotorOutput();
            if(elevatorTrapezoidal.isFinished())
            {
                elevatorState = Constants.ELEVATOR_LOCKED_STATE;
                elevatorLockPID.setDesiredValue(sensorInput.getElevatorEncoder());
            }
        }
        else if(elevatorState == Constants.ELEVATOR_LOCKED_STATE)
        {
//R/            isLocking = false;
//R/            elevatorDownPID.setDesiredValue(desiredElevatorPosition);
//R/            elevatorMotorSpeed = elevatorDownPID.calcPID(sensorInput.getElevatorEncoder());
            elevatorMotorSpeed = elevatorLockPID.calcPID(sensorInput.getElevatorEncoder());
        }
        
        robotOutput.setElevatorMotors(elevatorMotorSpeed);
        System.err.println(isLocking);
    }

    public synchronized void logData() {
        logging.logValue("DesiredElevatorPosition", desiredElevatorPosition);
        SmartDashboard.putNumber("DesiredElevatorPosition", desiredElevatorPosition);

        logging.logValue("ElevatorMotorSpeed", elevatorMotorSpeed);
        SmartDashboard.putNumber("ElevatorMotorSpeed", elevatorMotorSpeed);

        logging.logValue("ActualElevatorPosition", sensorInput.getElevatorEncoder());
        SmartDashboard.putNumber("ActualElevatorPosition", sensorInput.getElevatorEncoder());

        logging.logValue("IsLocking", isLocking);
        SmartDashboard.putBoolean("IsLocking", isLocking);
    }

    public synchronized void reset()
    {
        isLocking = false; //R/ Tommy not sure if you need this
    }
    
    public synchronized void loadElevatorLockPID()
    {
        double p = params.getAsDouble("E_ElevatorLockP", 0.0);
        double i = params.getAsDouble("E_ElevatorLockI", 0.0);
        double d = params.getAsDouble("E_ElevatorLockD", 0.0);
        double e = params.getAsDouble("E_ElevatorLockEpsilon", 0.0);
        
        elevatorLockPID = new SimPID(p, i, d, e);
    }
    
    public synchronized void loadElevatorTrapOptions()
    {
        double xEpsilon = params.getAsDouble("E_ElevatorTrapXEpsilon", 0.0);
        double maxV = params.getAsDouble("E_ElevatorTrapMaxVelocity", 0.0);
        double maxA = params.getAsDouble("E_ElevatorTrapMaxAcceleration", 0.0);
        double vFactor = params.getAsDouble("E_ElevatorTrapVelocityFactor", 0.0);
        double aFactor = params.getAsDouble("E_ElevatorTrapAccelerationFactor", 0.0);
        
        elevatorTrapezoidal.setTrapezoidOptions(xEpsilon, maxV, maxA, vFactor, aFactor);
        
        double p = params.getAsDouble("E_ElevatorTrapP", 0.0);
        double i = params.getAsDouble("E_ElevatorTrapI", 0.0);
        double d = params.getAsDouble("E_ElevatorTrapD", 0.0);
        double e = params.getAsDouble("E_ElevatorTrapEpsilon", 0.0);
        
//R/        elevatorDownPID.setConstants(p, i, d);
//R/        elevatorDownPID.setErrorEpsilon(e);
//R/        
//R/        p = params.getAsDouble("E_ElevatorLockP", 0.0);
//R/        i = params.getAsDouble("E_ElevatorLockI", 0.0);
//R/        d = params.getAsDouble("E_ElevatorLockD", 0.0);
//R/        e = params.getAsDouble("E_ElevatorLockEpsilon", 0.0);
//R/        
//R/        elevatorLockPID.setConstants(p, i, d);
//R/        elevatorLockPID.setErrorEpsilon(e);
        elevatorTrapezoidal.setPIDOptions(p, i, d, e);
    }
    
    public synchronized void setDesiredPosition(int position)
    {
        if(position != desiredElevatorPosition)
        {
            desiredElevatorPosition = position;
            
            elevatorTrapezoidal.setGoalDistance(desiredElevatorPosition);
            elevatorState = Constants.ELEVATOR_MOVING_STATE;
        }
    }
    
    public synchronized boolean elevatorAtTop()
    {
        return (desiredElevatorPosition == elevatorTopPosition && elevatorTrapezoidal.isFinished() && elevatorLockPID.isDone());
    }
    
    public synchronized boolean elevatorAtBottom()
    {
        return (desiredElevatorPosition == elevatorBottomPosition && elevatorTrapezoidal.isFinished() && elevatorLockPID.isDone());
    }
}
