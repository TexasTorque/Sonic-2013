package org.TexasTorque.TexasTorque2013.subsystem.manipulator;

import org.TexasTorque.TexasTorque2013.constants.Constants;
import org.TexasTorque.TexasTorque2013.io.DriverInput;
import org.TexasTorque.TexasTorque2013.io.RobotOutput;
import org.TexasTorque.TexasTorque2013.io.SensorInput;
import org.TexasTorque.TexasTorque2013.subsystem.drivebase.Drivebase;
import org.TexasTorque.TorqueLib.util.Parameters;

public class Manipulator
{
    
    private static Manipulator instance;
    private RobotOutput robotOutput;
    private SensorInput sensorInput;
    private DriverInput driverInput;
    private Parameters params;
    private Shooter shooter;
    private Elevator elevator;
    private Intake intake;
    private Magazine magazine;
    
    public static Manipulator getInstance()
    {
        return (instance == null) ? instance = new Manipulator() : instance;
    }
    
    public Manipulator()
    {
        robotOutput = RobotOutput.getInstance();
        sensorInput = SensorInput.getInstance();
        driverInput = DriverInput.getInstance();
        shooter = Shooter.getInstance();
        elevator = Elevator.getInstance();
        intake = Intake.getInstance();
        magazine = Magazine.getInstance();
    }
    
    public void run()
    {
        if(driverInput.reverseIntake())
        {
            calcReverseIntake();
        }
        else if(driverInput.runIntake())
        {
            calcIntake();
        }
        else if(driverInput.shootVisionHigh())
        {
            shootHighWithVision();
        }
        else
        {
            restoreDefaultPositions();
        }
        shooter.run();
        elevator.run();
        intake.run();
        magazine.run();
    }
    
    public void calcReverseIntake()
    {
        shooter.setTiltAngle(Constants.TILT_PARALLEL_POSITION);
        shooter.setShooterRates(Constants.SHOOTER_STOPPED_RATE, Constants.SHOOTER_STOPPED_RATE);
        // stuff for the magazine
        if(shooter.isParallel())
        {
            elevator.setDesiredPosition(Constants.ELEVATOR_BOTTOM_POSITION);
            if(elevator.elevatorAtBottom())
            {
                intake.setIntakeSpeed(params.getAsDouble("I_OuttakeSpeed", -1.0));
            }
        }
    }
    
    public void calcIntake()
    {
        shooter.setTiltAngle(Constants.TILT_PARALLEL_POSITION);
        shooter.setShooterRates(Constants.SHOOTER_STOPPED_RATE, Constants.SHOOTER_STOPPED_RATE);
        // stuff for the magazine
        if(shooter.isParallel())
        {
            elevator.setDesiredPosition(Constants.ELEVATOR_BOTTOM_POSITION);
            if(elevator.elevatorAtBottom())
            {
                intake.setIntakeSpeed(params.getAsDouble("I_IntakeSpeed", 0.0));
            }
        }
    }
    
    public void shootHighWithVision()
    {
        elevator.setDesiredPosition(Constants.ELEVATOR_TOP_POSITION);
        intake.setIntakeSpeed(Constants.MOTOR_STOPPED);
        if(elevator.elevatorAtTop())
        {
            shooter.setShooterRates(params.getAsInt("S_FrontShooterRate", 0), params.getAsInt("S_RearShooterRate", 0));
            // stuff with the tilt
            if(shooter.isReadyToFire() && Drivebase.getInstance().isHorizontallyLocked())
            {
                // some stuff for the magazine
            }
        }
    }
    
    public void restoreDefaultPositions()
    {
        shooter.setShooterRates(Constants.SHOOTER_STOPPED_RATE, Constants.SHOOTER_STOPPED_RATE);
        shooter.setTiltAngle(Constants.TILT_PARALLEL_POSITION);
        intake.setIntakeSpeed(Constants.MOTOR_STOPPED);
        if(shooter.isParallel())
        {
            elevator.setDesiredPosition(Constants.ELEVATOR_BOTTOM_POSITION);
            if(elevator.elevatorAtBottom())
            {
                // stuff for the magazine
            }
            else
            {
                // stuff for the magazine
            }
        }
    }
    
}
