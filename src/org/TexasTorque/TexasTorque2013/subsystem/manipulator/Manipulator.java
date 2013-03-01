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
    }
    
    public void run()
    {
        if(!driverInput.overrideState())
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
            else if(driverInput.gotoSlotHeight())
            {
                feedFromSlot();
            }
            else
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
        SmartDashboard.putNumber("ElevatorPosition", sensorInput.getElevatorEncoder());
        SmartDashboard.putNumber("TiltSpeed", shooter.tiltMotorSpeed);
        SmartDashboard.putNumber("FrontRate", sensorInput.getFrontShooterRate());
        SmartDashboard.putNumber("RearRate", sensorInput.getRearShooterRate());
        
    }
    
    public String getKeyNames()
    {
        String names = "InOverrideState,";
        names += intake.getKeyNames();
        names += elevator.getKeyNames();
        names += magazine.getKeyNames();
        names += shooter.getKeyNames();
        
        return names;
    }
    
    public String logData()
    {
        String data = driverInput.overrideState() + ",";
        data += intake.logData();
        data += elevator.logData();
        data += magazine.logData();
        data += shooter.logData();
        
        return data;
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
        if(driverInput.intakeOverride())
        {
            intake.setIntakeSpeed(Intake.intakeSpeed);
            magazine.setDesiredState(Constants.MAGAZINE_LOADING_STATE);
            shooter.setShooterRates(Constants.SHOOTER_STOPPED_RATE, Constants.SHOOTER_STOPPED_RATE);
            shooter.setTiltAngle(0.0);
        }
        else if(driverInput.outtakeOverride())
        {
            intake.setIntakeSpeed(Intake.outtakeSpeed);
            magazine.setDesiredState(Constants.MAGAZINE_LOADING_STATE);
            shooter.setShooterRates(Constants.SHOOTER_STOPPED_RATE, Constants.SHOOTER_STOPPED_RATE);
            shooter.setTiltAngle(0.0);
        }
        else if(driverInput.shootLowWithoutVisionOverride())
        {
            double frontSpeed = Shooter.frontShooterRate;
            double rearSpeed = Shooter.rearShooterRate;
            double angle = Shooter.shootLowStandardAngle;
            
            shooter.setShooterRates(frontSpeed, rearSpeed);
            shooter.setTiltAngle(angle);
            intake.setIntakeSpeed(Constants.MOTOR_STOPPED);
            magazine.setDesiredState(Constants.MAGAZINE_READY_STATE);
            
            if(driverInput.magazineShootOverride())
            {
                magazine.setDesiredState(Constants.MAGAZINE_SHOOTING_STATE);
            }
        }
        else if(driverInput.restoreToDefaultOverride())
        {
            intake.setIntakeSpeed(Constants.MOTOR_STOPPED);
            magazine.setDesiredState(Constants.MAGAZINE_READY_STATE);
            shooter.setShooterRates(Constants.SHOOTER_STOPPED_RATE, Constants.SHOOTER_STOPPED_RATE);
            shooter.setTiltAngle(0.0);
        }
        else
        {
            intake.setIntakeSpeed(Constants.MOTOR_STOPPED);
            shooter.setShooterRates(Constants.SHOOTER_STOPPED_RATE, Constants.SHOOTER_STOPPED_RATE);
            magazine.setDesiredState(Constants.MAGAZINE_READY_STATE);
        }
        
        if(driverInput.elevatorTopOverride())
        {
            robotOutput.setElevatorMotors(Elevator.elevatorOverrideSpeed);
        }
        else if(driverInput.elevatorBottomOverride())
        {
            robotOutput.setElevatorMotors(-1 * Elevator.elevatorOverrideSpeed);
        }
        else
        {
            robotOutput.setElevatorMotors(Constants.MOTOR_STOPPED);
        }
        
        intake.run();
        magazine.run();
        shooter.run();
    }
    
    public void intakeFrisbees()
    {
        restoreDefaultPositions();
        
        intake.setIntakeSpeed(Intake.intakeSpeed);
        magazine.setDesiredState(Constants.MAGAZINE_LOADING_STATE);
    }
    
    public void reverseIntake()
    {
        restoreDefaultPositions();
        
        intake.setIntakeSpeed(Intake.outtakeSpeed);
        magazine.setDesiredState(Constants.MAGAZINE_LOADING_STATE);
    }
    
    public void shootHighWithVision()
    {
        intake.setIntakeSpeed(Constants.MOTOR_STOPPED);
        shooter.setShooterRates(Shooter.frontShooterRate, Shooter.rearShooterRate);
        elevator.setDesiredPosition(Elevator.elevatorTopPosition);
        magazine.setDesiredState(Constants.MAGAZINE_READY_STATE);
        
        if(sensorInput.getElevatorEncoder() > 100)
        {
            shooter.setTiltAngle(Shooter.shootHighStandardAngle);
        }
        else
        {
            shooter.setTiltAngle(0.0);
        }
        
        if(elevator.atDesiredPosition() && SmartDashboard.getBoolean("found", false))
        {
            double currentAngle = sensorInput.getTiltAngle();
            double elevation = SmartDashboard.getNumber("elevation", 0.0);
            elevation = sensorInput.limitGyroAngle(elevation);
            shooter.setTiltAngle(currentAngle + elevation + Shooter.shootLowAdditive);
            
            if(driverInput.fireFrisbee() || (shooter.isReadyToFire() && dashboardManager.getDS().isAutonomous()))
            {
                magazine.setDesiredState(Constants.MAGAZINE_SHOOTING_STATE);
            }
            else
            {
                magazine.setDesiredState(Constants.MAGAZINE_READY_STATE);
            }
        }
    }
    
    public void shootHighWithoutVision()
    {
        intake.setIntakeSpeed(Constants.MOTOR_STOPPED);
        shooter.setShooterRates(Shooter.frontShooterRate, Shooter.rearShooterRate);
        elevator.setDesiredPosition(Elevator.elevatorTopPosition);
        shooter.setTiltAngle(Shooter.shootHighStandardAngle);
        magazine.setDesiredState(Constants.MAGAZINE_READY_STATE);
        
        if(sensorInput.getElevatorEncoder() > 100)
        {
            shooter.setTiltAngle(Shooter.shootHighStandardAngle);
        }
        else
        {
            shooter.setTiltAngle(0.0);
        }
        
        if(elevator.atDesiredPosition())
        {
            if(driverInput.fireFrisbee() || (dashboardManager.getDS().isAutonomous() && shooter.isReadyToFire()))
            {
                magazine.setDesiredState(Constants.MAGAZINE_SHOOTING_STATE);
            }
            else
            {
                magazine.setDesiredState(Constants.MAGAZINE_READY_STATE);
            }
        }
    }
    
    public void shootLowWithVision()
    {
        intake.setIntakeSpeed(Constants.MOTOR_STOPPED);
        magazine.setDesiredState(Constants.MAGAZINE_READY_STATE);
        elevator.setDesiredPosition(Elevator.elevatorBottomPosition);
        shooter.setShooterRates(Shooter.frontShooterRate, Shooter.rearShooterRate);
        shooter.setTiltAngle(Shooter.shootLowStandardAngle);
        
        if(elevator.atDesiredPosition() && SmartDashboard.getBoolean("found", false))
        {
            double currentAngle = sensorInput.getTiltAngle();
            double elevation = SmartDashboard.getNumber("elevation", 0.0);
            elevation = sensorInput.limitGyroAngle(elevation);
            shooter.setTiltAngle(currentAngle + elevation);
            
            if(driverInput.fireFrisbee() || (shooter.isReadyToFire() && dashboardManager.getDS().isAutonomous()))
            {
                magazine.setDesiredState(Constants.MAGAZINE_SHOOTING_STATE);
            }
            else
            {
                magazine.setDesiredState(Constants.MAGAZINE_READY_STATE);
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
        
        if(elevator.atDesiredPosition())
        {   
            if(driverInput.fireFrisbee() || (dashboardManager.getDS().isAutonomous() && shooter.isReadyToFire()))
            {
                magazine.setDesiredState(Constants.MAGAZINE_SHOOTING_STATE);
            }
            else
            {
                magazine.setDesiredState(Constants.MAGAZINE_READY_STATE);
            }
        }
    }
    
    public void feedFromSlot()
    {
        intake.setIntakeSpeed(Constants.MOTOR_STOPPED);
        shooter.setShooterRates(Constants.SHOOTER_STOPPED_RATE, Constants.SHOOTER_STOPPED_RATE);
        magazine.setDesiredState(Constants.MAGAZINE_READY_STATE);
        
        elevator.setDesiredPosition(Elevator.elevatorFeedPosition);
        
        if(sensorInput.getElevatorEncoder() > 100)
        {
            shooter.setTiltAngle(Shooter.feederStationAngle);
        }
        else
        {
            shooter.setTiltAngle(0.0);
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
