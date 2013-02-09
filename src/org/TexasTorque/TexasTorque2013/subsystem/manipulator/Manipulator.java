package org.TexasTorque.TexasTorque2013.subsystem.manipulator;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
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
        params = Parameters.getInstance();
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
        else if(driverInput.override())
        {
            calcOverrides();
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
    
    public void calcOverrides()
    {
        //----- Intake -----
        if(driverInput.intakeOverride())
        {
            intake.setIntakeSpeed(params.getAsDouble("I_IntakeSpeed", Constants.MOTOR_STOPPED));
            magazine.setDesiredState(Constants.MAGAZINE_LOADING_STATE);
        }
        else if(driverInput.outtakeOverride())
        {
            intake.setIntakeSpeed(params.getAsDouble("I_OuttakeSpeed", -1.0));
        }
        //----- Elevator -----
        if(driverInput.elevatorTopOverride())
        {
            elevator.setDesiredPosition(params.getAsInt("E_ElevatorTopPosition", Constants.DEFAULT_ELEVATOR_TOP_POSITION));
        }
        else if(driverInput.elevatorBottomOverride())
        {
            elevator.setDesiredPosition(params.getAsInt("E_ElevatorBottomPosition", Constants.DEFAULT_ELEVATOR_BOTTOM_POSITION));
        }
        //----- Tilt -----
        if(driverInput.tiltOverride())
        {
            
        }
        //----- Shooter -----
        if(driverInput.shooterOverride())
        {
            double frontRate = params.getAsDouble("S_FrontShooterRate", Constants.DEFAULT_FRONT_SHOOTER_RATE);
            double rearRate = params.getAsDouble("S_RearShooterRate", Constants.DEFAULT_REAR_SHOOTER_RATE);
            shooter.setShooterRates(frontRate, rearRate);
        }
        //----- Magazine -----
        if(driverInput.magazineShootOverride())
        {
            magazine.setDesiredState(Constants.MAGAZINE_SHOOTING_STATE);
        }
    }
    
    public void calcReverseIntake()
    {
        shooter.setTiltAngle(Constants.TILT_PARALLEL_POSITION);
        shooter.setShooterRates(Constants.SHOOTER_STOPPED_RATE, Constants.SHOOTER_STOPPED_RATE);
        magazine.setDesiredState(Constants.MAGAZINE_READY_STATE);
        if(shooter.isParallel())
        {
            elevator.setDesiredPosition(params.getAsInt("E_ElevatorBottomPosition", Constants.DEFAULT_ELEVATOR_BOTTOM_POSITION));
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
        magazine.setDesiredState(Constants.MAGAZINE_LOADING_STATE);
        if(shooter.isParallel())
        {
            elevator.setDesiredPosition(params.getAsInt("E_ElevatorBottomPosition", Constants.DEFAULT_ELEVATOR_BOTTOM_POSITION));
            if(elevator.elevatorAtBottom())
            {
                intake.setIntakeSpeed(params.getAsDouble("I_IntakeSpeed", Constants.MOTOR_STOPPED));
            }
        }
    }
    
    public void shootHighWithVision()
    {
        elevator.setDesiredPosition(params.getAsInt("E_ElevatorTopPosition", Constants.DEFAULT_ELEVATOR_TOP_POSITION));
        intake.setIntakeSpeed(Constants.MOTOR_STOPPED);
        magazine.setDesiredState(Constants.MAGAZINE_READY_STATE);
        if(elevator.elevatorAtTop())
        {
            double frontRate = params.getAsDouble("S_FrontShooterRate", Constants.DEFAULT_FRONT_SHOOTER_RATE);
            double rearRate = params.getAsDouble("S_RearShooterRate", Constants.DEFAULT_REAR_SHOOTER_RATE);
            shooter.setShooterRates(frontRate, rearRate);
            double elevation = SmartDashboard.getNumber("elevation", Constants.TILT_PARALLEL_POSITION);
            shooter.setTiltAngle(elevation);
            if(shooter.isReadyToFire() && Drivebase.getInstance().isHorizontallyLocked())
            {
                magazine.setDesiredState(Constants.MAGAZINE_SHOOTING_STATE);
            }
        }
    }
    
    public void restoreDefaultPositions()
    {
        shooter.setShooterRates(Constants.SHOOTER_STOPPED_RATE, Constants.SHOOTER_STOPPED_RATE);
        shooter.setTiltAngle(Constants.TILT_PARALLEL_POSITION);
        intake.setIntakeSpeed(Constants.MOTOR_STOPPED);
        magazine.setDesiredState(Constants.MAGAZINE_READY_STATE);
        if(shooter.isParallel())
        {
            elevator.setDesiredPosition(params.getAsInt("E_ElevatorBottomPosition", Constants.DEFAULT_ELEVATOR_BOTTOM_POSITION));
        }
    }
    
    public void pullNewPIDGains()
    {
        shooter.loadFrontShooterPID();
        shooter.loadRearShooterPID();
        shooter.loadTiltPID();
        elevator.loadElevatorPID();
    }
    
}
