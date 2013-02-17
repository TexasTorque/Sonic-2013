package org.TexasTorque.TexasTorque2013.subsystem.manipulator;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.TexasTorque.TexasTorque2013.constants.Constants;
import org.TexasTorque.TexasTorque2013.io.DriverInput;
import org.TexasTorque.TexasTorque2013.io.RobotOutput;
import org.TexasTorque.TexasTorque2013.io.SensorInput;
import org.TexasTorque.TexasTorque2013.subsystem.drivebase.Drivebase;
import org.TexasTorque.TorqueLib.util.DashboardManager;
import org.TexasTorque.TorqueLib.util.Parameters;
import org.TexasTorque.TorqueLib.util.TorqueLogging;

public class Manipulator
{
    
    private static Manipulator instance;
    private DashboardManager dashboardManager;
    private RobotOutput robotOutput;
    private SensorInput sensorInput;
    private DriverInput driverInput;
    private TorqueLogging logging;
    private Parameters params;
    private Shooter shooter;
    private Elevator2 elevator;
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
        logging = TorqueLogging.getInstance();
        params = Parameters.getInstance();
        shooter = Shooter.getInstance();
        elevator = Elevator2.getInstance();
        intake = Intake.getInstance();
        magazine = Magazine.getInstance();
    }
    
    public void run()
    {
        if(!driverInput.override())
        {
            /*if(driverInput.reverseIntake())
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
            }*/
            
            /*if(driverInput.runIntake())
            {
                elevator.setDesiredPosition(params.getAsInt("E_ElevatorTopPosition", Constants.DEFAULT_ELEVATOR_TOP_POSITION));
                elevator.run();
            }
            else if(driverInput.reverseIntake())
            {
                elevator.setDesiredPosition(params.getAsInt("E_ElevatorBottomPosition", Constants.DEFAULT_ELEVATOR_BOTTOM_POSITION));
                elevator.run();
            }
            else
            {
                robotOutput.setElevatorMotors(0.0);
            }*/
            
            if(driverInput.runIntake() && sensorInput.getElevatorEncoder() < 800)
            {
                robotOutput.setElevatorMotors(0.5);
            }
            else
            {
                robotOutput.setElevatorMotors(0.0);
            }
            
            //shooter.run();
            //elevator.run();
            //intake.run();
            //magazine.run();
            //robotOutput.setElevatorMotors(0.0);
            //robotOutput.setShooterTiltMotor(0.0);
        }
        else
        {
            calcOverrides();
        }
    }
    
    public synchronized void logData()
    {
        logging.logValue("InOverrideState", driverInput.override());
        intake.logData();
        shooter.logData();
        elevator.logData();
        magazine.logData();
    }
    
    private void calcOverrides()
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
            robotOutput.setFrisbeeLifter(Constants.MAGAZINE_STORED);
        }
        else if(driverInput.intakeOverride())
        {
            robotOutput.setFrisbeeLifter(Constants.MAGAZINE_LOADING);
        }
        else
        {
            robotOutput.setFrisbeeLifter(Constants.MAGAZINE_STORED);
            robotOutput.setLoaderSolenoid(true);
        }
    }
    
    public void calcReverseIntake()
    {
        double tiltAngle = params.getAsDouble("S_TiltStandardAngle", 0.0);
        shooter.setTiltAngle(tiltAngle);
        shooter.setShooterRates(Constants.SHOOTER_STOPPED_RATE, Constants.SHOOTER_STOPPED_RATE);
        magazine.setDesiredState(Constants.MAGAZINE_READY_STATE);
        if(shooter.isAtStandardPosition())
        {
            int elevatorBottomPosition = params.getAsInt("E_ElevatorBottomPosition", Constants.DEFAULT_ELEVATOR_BOTTOM_POSITION);
            
            elevator.setDesiredPosition(elevatorBottomPosition);
            if(elevator.elevatorAtBottom())
            {
                intake.setIntakeSpeed(params.getAsDouble("I_OuttakeSpeed", -1.0));
            }
        }
    }
    
    public void calcIntake()
    {
        double tiltAngle = params.getAsDouble("S_TiltStandardAngle", 0.0);
        shooter.setTiltAngle(tiltAngle);
        shooter.setShooterRates(Constants.SHOOTER_STOPPED_RATE, Constants.SHOOTER_STOPPED_RATE);
        magazine.setDesiredState(Constants.MAGAZINE_LOADING_STATE);
        if(shooter.isAtStandardPosition())
        {
            int elevatorBottomPosition = params.getAsInt("E_ElevatorBottomPosition", Constants.DEFAULT_ELEVATOR_BOTTOM_POSITION);
            
            elevator.setDesiredPosition(elevatorBottomPosition);
            if(elevator.elevatorAtBottom())
            {
                intake.setIntakeSpeed(params.getAsDouble("I_IntakeSpeed", Constants.MOTOR_STOPPED));
            }
        }
    }
    
    public void shootHighWithVision()
    {
        double tiltAngle = params.getAsDouble("S_TiltStandardAngle", Constants.DEFAULT_STANDARD_TILT_POSITION);
        int elevatorTopPosition = params.getAsInt("E_ElevatorTopPosition", Constants.DEFAULT_ELEVATOR_TOP_POSITION);
        
        shooter.setTiltAngle(tiltAngle);
        elevator.setDesiredPosition(elevatorTopPosition);
        intake.setIntakeSpeed(Constants.MOTOR_STOPPED);
        magazine.setDesiredState(Constants.MAGAZINE_READY_STATE);
        if(elevator.elevatorAtTop() && SmartDashboard.getBoolean("found", false))
        {
            double frontRate = params.getAsDouble("S_FrontShooterRate", Constants.DEFAULT_FRONT_SHOOTER_RATE);
            double rearRate = params.getAsDouble("S_RearShooterRate", Constants.DEFAULT_REAR_SHOOTER_RATE);
            double elevation = sensorInput.getTiltAngle() + SmartDashboard.getNumber("elevation", Constants.DEFAULT_STANDARD_TILT_POSITION);
            
            shooter.setShooterRates(frontRate, rearRate);
            shooter.setTiltAngle(elevation);
            if((driverInput.fireFrisbee() || dashboardManager.getDS().isAutonomous()) && shooter.isReadyToFire() && Drivebase.getInstance().isHorizontallyLocked())
            {
                magazine.setDesiredState(Constants.MAGAZINE_SHOOTING_STATE);
            }
        }
    }
    
    public void restoreDefaultPositions()
    {
        double tiltAngle = params.getAsDouble("S_TiltStandardAngle", Constants.DEFAULT_STANDARD_TILT_POSITION);
        shooter.setShooterRates(Constants.SHOOTER_STOPPED_RATE, Constants.SHOOTER_STOPPED_RATE);
        shooter.setTiltAngle(tiltAngle);
        intake.setIntakeSpeed(Constants.MOTOR_STOPPED);
        magazine.setDesiredState(Constants.MAGAZINE_READY_STATE);
        if(shooter.isAtStandardPosition())
        {
            int elevatorBottomPosition = params.getAsInt("E_ElevatorBottomPosition", Constants.DEFAULT_ELEVATOR_BOTTOM_POSITION);
            
            elevator.setDesiredPosition(elevatorBottomPosition);
        }
    }
    
    public void pullNewPIDGains()
    {
        shooter.loadFrontShooterPID();
        shooter.loadRearShooterPID();
        shooter.loadTiltLockPID();
        shooter.loadTiltPIV();
        elevator.loadElevatorLockPID();
        elevator.loadElevatorPIV();
        magazine.setDeltaTime(params.getAsDouble("M_DeltaTime", Constants.MAGAZINE_DELTA_TIME));
    }
    
}
