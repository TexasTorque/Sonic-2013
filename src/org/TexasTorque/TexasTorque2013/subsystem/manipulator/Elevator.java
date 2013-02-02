package org.TexasTorque.TexasTorque2013.subsystem.manipulator;

import org.TexasTorque.TexasTorque2013.constants.Constants;
import org.TexasTorque.TexasTorque2013.io.DriverInput;
import org.TexasTorque.TexasTorque2013.io.RobotOutput;
import org.TexasTorque.TexasTorque2013.io.SensorInput;
import org.TexasTorque.TorqueLib.util.Parameters;
import org.TexasTorque.TorqueLib.util.SimPID;

public class Elevator
{
    
    private static Elevator instance;
    private RobotOutput robotOutput;
    private DriverInput driverInput;
    private SensorInput sensorInput;
    private Parameters params;
    private SimPID elevatorPID;
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
        params = Parameters.getInstance();
        
        double p = params.getAsDouble("ElevatorP", 0.0);
        double i = params.getAsDouble("ElevatorI", 0.0);
        double d = params.getAsDouble("ElevatorD", 0.0);
        int e = params.getAsInt("ElevatorEpsilon", 0);
        
        elevatorPID = new SimPID(p, i, d, e);
        
        elevatorMotorSpeed = 0.0;
        desiredElevatorPosition = Constants.ELEVATOR_BOTTOM_POSITION;
    }
    
    public void run()
    {
        if((driverInput.runIntake() || driverInput.sendElevatorBottom()) && Shooter.getInstance().isParallel())
        {
            desiredElevatorPosition = Constants.ELEVATOR_BOTTOM_POSITION;
        }
        else if(driverInput.sendElevatorTop())
        {
            desiredElevatorPosition = Constants.ELEVATOR_TOP_POSITION;
        }
        elevatorPID.setDesiredValue(desiredElevatorPosition);
        elevatorMotorSpeed = elevatorPID.calcPID(sensorInput.getElevatorEncoder());
        robotOutput.setElevatorMotors(elevatorMotorSpeed);
    }
    
    public synchronized void loadElevatorPID()
    {
        double p = params.getAsDouble("ElevatorP", 0.0);
        double i = params.getAsDouble("ElevatorI", 0.0);
        double d = params.getAsDouble("ElevatorD", 0.0);
        
        elevatorPID.setConstants(p, i, d);
        
        elevatorPID.setErrorEpsilon(params.getAsInt("ElevatorEpsilon", 0));
    }
    
    public synchronized boolean elevatorAtTop()
    {
        return (desiredElevatorPosition == Constants.ELEVATOR_TOP_POSITION && elevatorPID.isDone());
    }
    
    public synchronized boolean elevatorAtBottom()
    {
        return (desiredElevatorPosition == Constants.ELEVATOR_BOTTOM_POSITION && elevatorPID.isDone());
    }
}
