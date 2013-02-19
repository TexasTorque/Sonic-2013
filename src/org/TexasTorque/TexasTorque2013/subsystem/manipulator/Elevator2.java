package org.TexasTorque.TexasTorque2013.subsystem.manipulator;

import edu.wpi.first.wpilibj.Timer;
import org.TexasTorque.TexasTorque2013.io.DriverInput;
import org.TexasTorque.TexasTorque2013.io.RobotOutput;
import org.TexasTorque.TexasTorque2013.io.SensorInput;
import org.TexasTorque.TorqueLib.util.DashboardManager;
import org.TexasTorque.TorqueLib.util.Parameters;
import org.TexasTorque.TorqueLib.util.SimPID;
import org.TexasTorque.TorqueLib.util.TorqueLogging;
import org.TexasTorque.TorqueLib.util.TrajectorySmoother;

public class Elevator2
{
    private static Elevator2 instance;
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
        
        elevatorPID = new SimPID();
        loadElevatorPID();
        
        desiredPosition = 0;
        elevatorMotorSpeed = 0.0;
        previousTime = Timer.getFPGATimestamp();
        firstIteration = true;
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
    
    public synchronized void reset()
    {
    }
    
    public synchronized void logData()
    {
    }
    
    public synchronized void loadNewTrajectory()
    {
        double maxV = params.getAsDouble("E_ElevatorMaxVelocity", 0.0);
        double maxA = params.getAsDouble("E_ElevatorMaxAcceleration", 0.0);
        
        trajectory = new TrajectorySmoother(maxA, maxV);
    }
    
    public synchronized void loadElevatorPID()
    {
        double p = params.getAsDouble("E_ElevatorP", 0.0);
        double i = params.getAsDouble("E_ElevatorI", 0.0);
        double d = params.getAsDouble("E_ElevatorD", 0.0);
        int e = params.getAsInt("E_ElevatorEpsilon", 0);
        
        elevatorPID.setConstants(p, i, d);
        elevatorPID.setErrorEpsilon(e);
        elevatorPID.resetErrorSum();
        elevatorPID.resetPreviousVal();
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
        return false;
    }
    
    public synchronized boolean elevatorAtBottom()
    {
        return false;
    }
}
