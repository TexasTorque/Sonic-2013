package org.TexasTorque.TexasTorque2013.subsystem.manipulator;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.TexasTorque.TexasTorque2013.TorqueSubsystem;
import org.TexasTorque.TexasTorque2013.constants.Constants;
import org.TexasTorque.TexasTorque2013.subsystem.drivebase.Drivebase;

public class Manipulator extends TorqueSubsystem
{   
    private Shooter shooter;
    private Elevator elevator;
    private Intake intake;
    private Magazine magazine;
    
    public static TorqueSubsystem getInstance()
    {
        return (instance == null) ? instance = new Manipulator() : instance;
    }
    
    private Manipulator()
    {
        super();
        
        shooter = (Shooter) Shooter.getInstance();
        elevator = (Elevator) Elevator.getInstance();
        intake = (Intake) Intake.getInstance();
        magazine = (Magazine) Magazine.getInstance();
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
    
    public synchronized String getKeyNames()
    {
        String names = "InOverrideState,";
        names += intake.getKeyNames();
        names += elevator.getKeyNames();
        names += magazine.getKeyNames();
        names += shooter.getKeyNames();
        
        return names;
    }
    
    public void loadParameters()
    {
        shooter.loadParameters();
        elevator.loadParameters();
        magazine.loadParameters();
        intake.loadParameters();
    }
    
    private void calcOverrides()
    {
        //----- Intake -----
        if(driverInput.intakeOverride())
        {
            double intakeSpeed = Intake.intakeSpeed;
            
            robotOutput.setIntakeMotor(intakeSpeed);
        }
        else if(driverInput.outtakeOverride())
        {
            double outtakeSpeed = Intake.outtakeSpeed;
            
            robotOutput.setIntakeMotor(outtakeSpeed);
        }
        else
        {
            robotOutput.setIntakeMotor(Constants.MOTOR_STOPPED);
        }
        //----- Elevator -----
        if(driverInput.elevatorTopOverride())
        {
            double speed = Elevator.elevatorOverrideSpeed;
            
            robotOutput.setElevatorMotors(speed);
        }
        else if(driverInput.elevatorBottomOverride())
        {
            double speed = -0.8 * Elevator.elevatorOverrideSpeed;
            
            robotOutput.setElevatorMotors(speed);
        }
        else
        {
            robotOutput.setElevatorMotors(Constants.MOTOR_STOPPED);
        }
        //----- Tilt -----
        if(driverInput.tiltUpOverride())
        {
            double speed = Shooter.tiltOverrideSpeed;
            
            robotOutput.setTiltMotor(speed);
        }
        else if(driverInput.tiltDownOverride())
        {
            double speed = -1 * Shooter.tiltOverrideSpeed;
            
            robotOutput.setTiltMotor(speed);
        }
        else
        {
            robotOutput.setTiltMotor(Constants.MOTOR_STOPPED);
        }
        //----- Shooter -----
        if(driverInput.shooterOverride())
        {
            double frontSpeed = Shooter.frontShooterOverrideSpeed;
            double rearSpeed  = Shooter.rearShooterOverrideSpeed;
            
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
        double tiltAngle = Shooter.standardTiltPosition;
        
        shooter.setTiltAngle(tiltAngle);
        shooter.setShooterRates(Constants.SHOOTER_STOPPED_RATE, Constants.SHOOTER_STOPPED_RATE);
        magazine.setDesiredState(Constants.MAGAZINE_READY_STATE);
        
        if(shooter.isAtStandardPosition())
        {
            int elevatorBottomPosition = Elevator.elevatorBottomPosition;
            
            elevator.setDesiredPosition(elevatorBottomPosition);
            
            if(elevator.elevatorAtBottom())
            {
                double intakeSpeed = Intake.intakeSpeed;
                
                intake.setIntakeSpeed(intakeSpeed);
            }
        }
    }
    
    public void calcIntake()
    {
        double tiltAngle = Shooter.standardTiltPosition;
        
        shooter.setTiltAngle(tiltAngle);
        shooter.setShooterRates(Constants.SHOOTER_STOPPED_RATE, Constants.SHOOTER_STOPPED_RATE);
        magazine.setDesiredState(Constants.MAGAZINE_LOADING_STATE);
        
        if(shooter.isAtStandardPosition())
        {
            int elevatorBottomPosition = Elevator.elevatorBottomPosition;
            
            elevator.setDesiredPosition(elevatorBottomPosition);
            
            if(elevator.elevatorAtBottom())
            {
                double outtakeSpeed = Intake.outtakeSpeed;
                
                intake.setIntakeSpeed(outtakeSpeed);
            }
        }
    }
    
    public void shootHighWithVision()
    {
        double tiltAngle = Shooter.standardTiltPosition;
        int elevatorTopPosition = Elevator.elevatorTopPosition;
        
        shooter.setTiltAngle(tiltAngle);
        elevator.setDesiredPosition(elevatorTopPosition);
        intake.setIntakeSpeed(Constants.MOTOR_STOPPED);
        magazine.setDesiredState(Constants.MAGAZINE_READY_STATE);
        
        if(elevator.elevatorAtTop() && SmartDashboard.getBoolean("found", false))
        {
            double frontRate = Shooter.frontShooterRate;
            double rearRate = Shooter.rearShooterRate;
            double elevation = sensorInput.getTiltAngle() + SmartDashboard.getNumber("elevation", Constants.DEFAULT_STANDARD_TILT_POSITION);
            
            shooter.setShooterRates(frontRate, rearRate);
            shooter.setTiltAngle(elevation);
            
            if((driverInput.fireFrisbee() || dashboardManager.getDS().isAutonomous()) && shooter.isReadyToFire() && ((Drivebase) Drivebase.getInstance()).isHorizontallyLocked())
            {
                magazine.setDesiredState(Constants.MAGAZINE_SHOOTING_STATE);
            }
        }
    }
    
    public void restoreDefaultPositions()
    {
        double tiltAngle = Shooter.standardTiltPosition;
        
        shooter.setShooterRates(Constants.SHOOTER_STOPPED_RATE, Constants.SHOOTER_STOPPED_RATE);
        shooter.setTiltAngle(tiltAngle);
        intake.setIntakeSpeed(Constants.MOTOR_STOPPED);
        magazine.setDesiredState(Constants.MAGAZINE_READY_STATE);
        
        if(shooter.isAtStandardPosition())
        {
            int elevatorBottomPosition = Elevator.elevatorBottomPosition;
            
            elevator.setDesiredPosition(elevatorBottomPosition);
        }
    }
}
