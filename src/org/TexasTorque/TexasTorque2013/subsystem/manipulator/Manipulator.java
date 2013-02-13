package org.TexasTorque.TexasTorque2013.subsystem.manipulator;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.TexasTorque.TexasTorque2013.constants.Constants;
import org.TexasTorque.TexasTorque2013.io.DriverInput;
import org.TexasTorque.TexasTorque2013.io.RobotOutput;
import org.TexasTorque.TexasTorque2013.io.SensorInput;
import org.TexasTorque.TexasTorque2013.subsystem.drivebase.Drivebase;
import org.TexasTorque.TorqueLib.util.DashboardManager;
import org.TexasTorque.TorqueLib.util.Parameters;

public class Manipulator
{
    
    private static Manipulator instance;
    private DashboardManager dashboardManager;
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
        dashboardManager = DashboardManager.getInstance();
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
        if(!driverInput.override())
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
        else
        {
            calcOverrides();
        }
    }
    
    public synchronized void logData()
    {
        intake.logData();
    }
    
    public void calcOverrides()
    {
        //----- Intake -----
        if(driverInput.intakeOverride())
        {
            robotOutput.setIntakeMotor(params.getAsDouble("I_IntakeSpeed", 1.0));
        }
        else if(driverInput.outtakeOverride())
        {
            robotOutput.setIntakeMotor(params.getAsDouble("I_OuttakeSpeed", -1.0));
        }
        else
        {
            robotOutput.setIntakeMotor(Constants.MOTOR_STOPPED);
        }
        //----- Elevator -----
        if(driverInput.elevatorTopOverride())
        {
            double speed = params.getAsDouble("E_ElevatorOverrideSpeed", 0.5);
            robotOutput.setElevatorMotors(speed);
        }
        else if(driverInput.elevatorBottomOverride())
        {
            double speed = -1 * params.getAsDouble("E_ElevatorOverrideSpeed", 0.5) * 0.8; // Accounts for gravity
            robotOutput.setElevatorMotors(speed);
        }
        else
        {
            robotOutput.setElevatorMotors(Constants.MOTOR_STOPPED);
        }
        //----- Tilt -----
        if(driverInput.tiltUpOverride())
        {
            double speed = params.getAsDouble("S_TiltOverrideSpeed", 0.5);
            robotOutput.setShooterTiltMotor(speed);
        }
        else if(driverInput.tiltDownOverride())
        {
            double speed = -1 * params.getAsDouble("S_TiltOverrideSpeed", 0.5);
            robotOutput.setShooterTiltMotor(speed);
        }
        else
        {
            robotOutput.setShooterTiltMotor(Constants.MOTOR_STOPPED);
        }
        //----- Shooter -----
        if(driverInput.shooterOverride())
        {
            double frontSpeed = params.getAsDouble("S_FrontShooterOverrideSpeed", 0.7);
            double rearSpeed  = params.getAsDouble("S_RearShooterOverrideSpeed", 0.5);
            robotOutput.setShooterMotors(frontSpeed, rearSpeed);
        }
        else
        {
            robotOutput.setShooterMotors(Constants.MOTOR_STOPPED, Constants.MOTOR_STOPPED);
        }
        //----- Magazine -----
        if(driverInput.magazineShootOverride())
        {
            robotOutput.setLoaderSolenoid(false);
        }
        else if(driverInput.intakeOverride())
        {
            robotOutput.setFrisbeeLifter(false);
        }
        else
        {
            robotOutput.setFrisbeeLifter(true);
            robotOutput.setLoaderSolenoid(true);
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
            if((driverInput.fireFrisbee() || dashboardManager.getDS().isAutonomous()) && shooter.isReadyToFire() && Drivebase.getInstance().isHorizontallyLocked())
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
