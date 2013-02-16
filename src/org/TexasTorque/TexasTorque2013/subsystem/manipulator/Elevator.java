package org.TexasTorque.TexasTorque2013.subsystem.manipulator;

import org.TexasTorque.TexasTorque2013.constants.Constants;
import org.TexasTorque.TexasTorque2013.io.DriverInput;
import org.TexasTorque.TexasTorque2013.io.RobotOutput;
import org.TexasTorque.TexasTorque2013.io.SensorInput;
import org.TexasTorque.TorqueLib.util.DashboardManager;
import org.TexasTorque.TorqueLib.util.Parameters;
import org.TexasTorque.TorqueLib.util.SimPID;
import org.TexasTorque.TorqueLib.util.TorqueLogging;

public class Elevator
{
    
    private static Elevator instance;
    private RobotOutput robotOutput;
    private DriverInput driverInput;
    private SensorInput sensorInput;
    private TorqueLogging logging;
    private Parameters params;
    private DashboardManager dashboardManager;
    
    private SimPID elevatorUpPID;
    private SimPID elevatorDownPID;
    private SimPID elevatorLockPID;
    private double elevatorMotorSpeed;
    private int desiredElevatorPosition;
    private boolean isLocking;
    
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
        
        double p = params.getAsDouble("E_ElevatorUpP", 0.0);
        double i = params.getAsDouble("E_ElevatorUpI", 0.0);
        double d = params.getAsDouble("E_ElevatorUpD", 0.0);
        double e = params.getAsDouble("E_ElevatorUpEpsilon", 0.0);
        
        elevatorUpPID = new SimPID(p, i, d, e);
        
        p = params.getAsDouble("E_ElevatorDownP", 0.0);
        i = params.getAsDouble("E_ElevatorDownI", 0.0);
        d = params.getAsDouble("E_ElevatorDownD", 0.0);
        e = params.getAsDouble("E_ElevatorDownEpsilon", 0.0);
        
        elevatorDownPID = new SimPID(p, i, d, e);
        
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
    }
    
    public void run()
    {
        elevatorTopPosition = params.getAsInt("E_ElevatorTopPosition", Constants.DEFAULT_ELEVATOR_TOP_POSITION);
        elevatorBottomPosition = params.getAsInt("E_ElevatorBottomPosition", Constants.DEFAULT_ELEVATOR_BOTTOM_POSITION);
        
        int encoderPosition = sensorInput.getElevatorEncoder();
        
        if(desiredElevatorPosition == elevatorTopPosition)
        {
            if(elevatorUpPID.isDone() && encoderPosition > 740)
            {
                isLocking = true;
            }
            if(isLocking)
            {
                elevatorLockPID.setDesiredValue(desiredElevatorPosition);
                elevatorMotorSpeed = elevatorLockPID.calcPID(sensorInput.getElevatorEncoder());
            }
            else
            {
                isLocking = false;
                elevatorUpPID.setDesiredValue(desiredElevatorPosition);
                elevatorMotorSpeed = elevatorUpPID.calcPID(sensorInput.getElevatorEncoder());
            }
        }
        else if(desiredElevatorPosition == elevatorBottomPosition)
        {
            isLocking = false;
            elevatorDownPID.setDesiredValue(desiredElevatorPosition);
            elevatorMotorSpeed = elevatorDownPID.calcPID(sensorInput.getElevatorEncoder());
        }
        robotOutput.setElevatorMotors(elevatorMotorSpeed);
        System.err.println(isLocking);
    }
    
    public synchronized void logData()
    {
        logging.logValue("DesiredElevatorPosition", desiredElevatorPosition);
        logging.logValue("ElevatorMotorSpeed", elevatorMotorSpeed);
        logging.logValue("ActualElevatorPosition", sensorInput.getElevatorEncoder());
        logging.logValue("IsLocking", isLocking);
    }
    
    public synchronized void reset()
    {
        isLocking = false;
    }
    
    public synchronized void setDesiredPosition(int position)
    {
        desiredElevatorPosition = position;
    }
    
    public synchronized void loadElevatorPID()
    {
        double p = params.getAsDouble("E_ElevatorUpP", 0.0);
        double i = params.getAsDouble("E_ElevatorUpI", 0.0);
        double d = params.getAsDouble("E_ElevatorUpD", 0.0);
        double e = params.getAsDouble("E_ElevatorUpEpsilon", 0.0);
        
        elevatorUpPID.setConstants(p, i, d);
        elevatorUpPID.setErrorEpsilon(e);
        
        p = params.getAsDouble("E_ElevatorDownP", 0.0);
        i = params.getAsDouble("E_ElevatorDownI", 0.0);
        d = params.getAsDouble("E_ElevatorDownD", 0.0);
        e = params.getAsDouble("E_ElevatorDownEpsilon", 0.0);
        
        elevatorDownPID.setConstants(p, i, d);
        elevatorDownPID.setErrorEpsilon(e);
        
        p = params.getAsDouble("E_ElevatorLockP", 0.0);
        i = params.getAsDouble("E_ElevatorLockI", 0.0);
        d = params.getAsDouble("E_ElevatorLockD", 0.0);
        e = params.getAsDouble("E_ElevatorLockEpsilon", 0.0);
        
        elevatorLockPID.setConstants(p, i, d);
        elevatorLockPID.setErrorEpsilon(e);
    }
    
    public synchronized boolean elevatorAtTop()
    {
        return (desiredElevatorPosition == elevatorTopPosition && elevatorLockPID.isDone() && isLocking);
    }
    
    public synchronized boolean elevatorAtBottom()
    {
        return (desiredElevatorPosition == elevatorBottomPosition && elevatorDownPID.isDone());
    }
}
