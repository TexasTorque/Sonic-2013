package org.TexasTorque.TexasTorque2013.subsystem.manipulator;

import org.TexasTorque.TexasTorque2013.constants.Constants;
import org.TexasTorque.TexasTorque2013.io.DriverInput;
import org.TexasTorque.TexasTorque2013.io.RobotOutput;
import org.TexasTorque.TexasTorque2013.io.SensorInput;
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
    
    private SimPID elevatorUpPID;
    private SimPID elevatorDownPID;
    private double elevatorMotorSpeed;
    private int desiredElevatorPosition;
    
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
        
        double p = params.getAsDouble("E_ElevatorUpP", 0.0);
        double i = params.getAsDouble("E_ElevatorUpI", 0.0);
        double d = params.getAsDouble("E_ElevatorUpD", 0.0);
        int e = params.getAsInt("E_ElevatorUpEpsilon", 0);
        
        elevatorUpPID = new SimPID(p, i, d, e);
        
        p = params.getAsDouble("E_ElevatorDownP", 0.0);
        i = params.getAsDouble("E_ElevatorDownI", 0.0);
        d = params.getAsDouble("E_ElevatorDownD", 0.0);
        e = params.getAsInt("E_ElevatorDownEpsilon", 0);
        
        elevatorDownPID = new SimPID(p, i, d, e);
        
        elevatorMotorSpeed = Constants.MOTOR_STOPPED;
        desiredElevatorPosition = params.getAsInt("E_ElevatorBottomPosition", Constants.DEFAULT_ELEVATOR_BOTTOM_POSITION);
    }
    
    public void run()
    {
        if(desiredElevatorPosition == params.getAsInt("E_ElevatorTopPosition", Constants.DEFAULT_ELEVATOR_TOP_POSITION))
        {
            elevatorUpPID.setDesiredValue(desiredElevatorPosition);
            elevatorMotorSpeed = elevatorUpPID.calcPID(sensorInput.getElevatorEncoder());
        }
        else if(desiredElevatorPosition == params.getAsInt("E_ElevatorBottomPosition", Constants.DEFAULT_ELEVATOR_BOTTOM_POSITION))
        {
            elevatorDownPID.setDesiredValue(desiredElevatorPosition);
            elevatorMotorSpeed = elevatorDownPID.calcPID(sensorInput.getElevatorEncoder());
        }
        robotOutput.setElevatorMotors(elevatorMotorSpeed);
    }
    
    public synchronized void logData()
    {
        logging.logValue("DesiredElevatorPosition", desiredElevatorPosition);
        logging.logValue("ElevatorMotorSpeed", elevatorMotorSpeed);
        logging.logValue("ActualElevatorPosition", sensorInput.getElevatorEncoder());
        logging.logValue("ElevatorAtTop", elevatorAtTop());
        logging.logValue("ElevatorAtBottom", elevatorAtBottom());
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
        int e = params.getAsInt("E_ElevatorUpEpsilon", 0);
        
        elevatorUpPID.setConstants(p, i, d);
        elevatorUpPID.setErrorEpsilon(e);
        
        p = params.getAsDouble("E_ElevatorDownP", 0.0);
        i = params.getAsDouble("E_ElevatorDownI", 0.0);
        d = params.getAsDouble("E_ElevatorDownD", 0.0);
        e = params.getAsInt("E_ElevatorDownEpsilon", 0);
        
        elevatorDownPID.setConstants(p, i, d);
        elevatorDownPID.setErrorEpsilon(e);
    }
    
    public synchronized boolean elevatorAtTop()
    {
        return (desiredElevatorPosition == params.getAsInt("E_ElevatorTopPosition", Constants.DEFAULT_ELEVATOR_TOP_POSITION) && elevatorUpPID.isDone());
    }
    
    public synchronized boolean elevatorAtBottom()
    {
        return (desiredElevatorPosition == params.getAsInt("E_ElevatorDownPosition", Constants.DEFAULT_ELEVATOR_BOTTOM_POSITION) && elevatorDownPID.isDone());
    }
}
