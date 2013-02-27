package org.TexasTorque.TexasTorque2013.subsystem.manipulator;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.TexasTorque.TexasTorque2013.TorqueSubsystem;
import org.TexasTorque.TexasTorque2013.constants.Constants;

public class Manipulator extends TorqueSubsystem
{   
    private static Manipulator instance;
    
    private Shooter shooter;
    private Elevator elevator;
    private Intake intake;
    private Magazine magazine;
    
    public static Manipulator getInstance()
    {
        return (instance == null) ? instance = new Manipulator() : instance;
    }
    
    private Manipulator()
    {
        super();
        
        shooter = Shooter.getInstance();
        elevator = Elevator.getInstance();
        intake = Intake.getInstance();
        magazine = Magazine.getInstance();
        
        SmartDashboard.putNumber("DesiredDegree", 0.0);
    }
    
    public void run()
    {
        if(!driverInput.override())
        {
            if(driverInput.restoreToDefault())
            {
                restoreDefaultPositions();
            }
            else if(driverInput.runIntake())
            {
                intakeFrisbees();
            }
            else if(driverInput.reverseIntake())
            {
                reverseIntake();
            }
            else if(driverInput.shootHighWithVision())
            {
                shootHighWithVision();
            }
            else if(driverInput.shootHighWithoutVision())
            {
                shootHighWithoutVision();
            }
            else if(driverInput.shootLowWithVision())
            {
                shootLowWithVision();
            }
            else if(driverInput.shootLowWithoutVision())
            {
                shootLowWithoutVision();
            }
            else
            {
                intake.setIntakeSpeed(Constants.MOTOR_STOPPED);
                shooter.setShooterRates(Constants.SHOOTER_STOPPED_RATE, Constants.SHOOTER_STOPPED_RATE);
                magazine.setDesiredState(Constants.MAGAZINE_READY_STATE);
            }
            
            intake.run();
            shooter.run();
            elevator.run();
            magazine.run();
        }
        else
        {
            calcOverrides();
        }
        
        SmartDashboard.putNumber("TiltAngle", sensorInput.getTiltAngle());
        
    }
    
    public synchronized String logData()
    {
        String data = driverInput.override() + ",";
        data += intake.logData();
        data += elevator.logData();
        data += magazine.logData();
        data += shooter.logData();
        
        return data;
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
    
    public void reverseIntake()
    {
        restoreDefaultPositions();
        
        intake.setIntakeSpeed(Intake.outtakeSpeed);
        magazine.setDesiredState(Constants.MAGAZINE_LOADING_STATE);
    }
    
    public void intakeFrisbees()
    {
        restoreDefaultPositions();
        
        intake.setIntakeSpeed(Intake.intakeSpeed);
        magazine.setDesiredState(Constants.MAGAZINE_LOADING_STATE);
    }
    
    public void shootHighWithVision()
    {
        intake.setIntakeSpeed(Constants.MOTOR_STOPPED);
        shooter.setShooterRates(Shooter.frontShooterRate, Shooter.rearShooterRate);
        elevator.setDesiredPosition(Elevator.elevatorTopPosition);
        
        if(sensorInput.getElevatorEncoder() > 100)
        {
            shooter.setTiltAngle(Shooter.standardTiltPosition);
        }
        else
        {
            shooter.setTiltAngle(0.0);
        }
        
        if(elevator.elevatorAtTop() && SmartDashboard.getBoolean("found", false))
        {
            double currentAngle = sensorInput.getTiltAngle();
            double elevation = SmartDashboard.getNumber("elevation", 0.0);
            elevation = sensorInput.limitGyroAngle(elevation);
            shooter.setTiltAngle(currentAngle + elevation);
            
            if(shooter.isReadyToFire() && (driverInput.fireFrisbee() || dashboardManager.getDS().isAutonomous()))
            {
                magazine.setDesiredState(Constants.MAGAZINE_SHOOTING_STATE);
            }
        }
    }
    
    public void shootHighWithoutVision()
    {
        intake.setIntakeSpeed(Constants.MOTOR_STOPPED);
        shooter.setShooterRates(Shooter.frontShooterRate, Shooter.rearShooterRate);
        elevator.setDesiredPosition(Elevator.elevatorTopPosition);
        shooter.setTiltAngle(Shooter.shootHighStandardAngle);
        
        if(sensorInput.getElevatorEncoder() > 100)
        {
            shooter.setTiltAngle(Shooter.standardTiltPosition);
        }
        else
        {
            shooter.setTiltAngle(0.0);
        }
        
        if(elevator.elevatorAtTop())
        {
            if(shooter.isReadyToFire() && (driverInput.fireFrisbee() || dashboardManager.getDS().isAutonomous()))
            {
                magazine.setDesiredState(Constants.MAGAZINE_SHOOTING_STATE);
            }
        }
    }
    
    public void shootLowWithVision()
    {
        intake.setIntakeSpeed(Constants.MOTOR_STOPPED);
        magazine.setDesiredState(Constants.MAGAZINE_READY_STATE);
        elevator.setDesiredPosition(Elevator.elevatorBottomPosition);
        shooter.setShooterRates(Shooter.frontShooterRate, Shooter.rearShooterRate);
        shooter.setTiltAngle(Shooter.standardTiltPosition);
        
        if(elevator.elevatorAtBottom() && SmartDashboard.getBoolean("found", false))
        {
            double currentAngle = sensorInput.getTiltAngle();
            double elevation = SmartDashboard.getNumber("elevation", 0.0);
            elevation = sensorInput.limitGyroAngle(elevation);
            shooter.setTiltAngle(currentAngle + elevation);
            
            if(shooter.isReadyToFire() && (driverInput.fireFrisbee() || dashboardManager.getDS().isAutonomous()))
            {
                magazine.setDesiredState(Constants.MAGAZINE_SHOOTING_STATE);
            }
        }
        
    }
    
    public void shootLowWithoutVision()
    {
        intake.setIntakeSpeed(Constants.MOTOR_STOPPED);
        magazine.setDesiredState(Constants.MAGAZINE_READY_STATE);
        elevator.setDesiredPosition(Elevator.elevatorBottomPosition);
        shooter.setShooterRates(Shooter.frontShooterRate, Shooter.rearShooterRate);
        shooter.setTiltAngle(Shooter.shootLowStandardAngle);
        
        if(elevator.elevatorAtBottom())
        {   
            if(shooter.isReadyToFire() && (driverInput.fireFrisbee() || dashboardManager.getDS().isAutonomous()))
            {
                magazine.setDesiredState(Constants.MAGAZINE_SHOOTING_STATE);
            }
        }
    }
    
    public void restoreDefaultPositions()
    {
        intake.setIntakeSpeed(Constants.MOTOR_STOPPED);
        shooter.setShooterRates(Constants.SHOOTER_STOPPED_RATE, Constants.SHOOTER_STOPPED_RATE);
        magazine.setDesiredState(Constants.MAGAZINE_READY_STATE);
        
        if(sensorInput.getElevatorEncoder() > 100)
        {
            shooter.setTiltAngle(Shooter.standardTiltPosition);
        }
        else
        {
            shooter.setTiltAngle(0.0);
        }
        
        elevator.setDesiredPosition(Elevator.elevatorBottomPosition);
    }
}
