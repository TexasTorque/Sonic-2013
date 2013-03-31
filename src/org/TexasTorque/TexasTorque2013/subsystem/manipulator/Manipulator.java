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
    private Tilt tilt;
    private Climber climber;
    
    private double savedTiltAngle;
    
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
        tilt = Tilt.getInstance();
        climber = Climber.getInstance();
    }
    
    public void run()
    {
        if(!driverInput.overrideState())
        {   
            //----- Normal Ops -----
            
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
            else if(driverInput.shootHighWithoutVision())
            {
                shootHighWithoutVision();
            }
            else if(driverInput.shootSide())
            {
                shootSide();
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
                shooter.setShooterRates(Constants.SHOOTER_STOPPED_RATE, Constants.SHOOTER_STOPPED_RATE, Constants.SHOOTER_STOPPED_RATE);
                magazine.setDesiredState(Constants.MAGAZINE_READY_STATE);
                tilt.setTiltAngle(0.0);
                
                setLightsNormal();
            }
            
            
            boolean incrementAngle = driverInput.incrementAngle();
            boolean decrementAngle = driverInput.decrementAngle();
            tilt.tiltAdjustments(incrementAngle, decrementAngle);
            
            intake.run();
            shooter.run();
            elevator.run();
            magazine.run();
            climber.run();
        }
        else
        {
            calcOverrides();
        }
    }
    
    public void setToRobot()
    {
        elevator.setToRobot();
        intake.setToRobot();
        magazine.setToRobot();
        shooter.setToRobot();
        tilt.setToRobot();
        climber.setToRobot();
    }
    
    public String getKeyNames()
    {
        String names = "InOverrideState,";
        names += intake.getKeyNames();
        names += elevator.getKeyNames();
        names += magazine.getKeyNames();
        names += shooter.getKeyNames();
        names += tilt.getKeyNames();
        names += climber.getKeyNames();
        
        return names;
    }
    
    public String logData()
    {
        String data = driverInput.overrideState() + ",";
        data += intake.logData();
        data += elevator.logData();
        data += magazine.logData();
        data += shooter.logData();
        data += tilt.logData();
        data += climber.logData();
        
        return data;
    }
    
    public void loadParameters()
    {
        shooter.loadParameters();
        elevator.loadParameters();
        magazine.loadParameters();
        intake.loadParameters();
        tilt.loadParameters();
        climber.loadParameters();
        
        savedTiltAngle = 0.0;
    }
    
    private void calcOverrides()
    {
        if(driverInput.intakeOverride())
        {
            intakeFrisbees();
        }
        else if(driverInput.outtakeOverride())
        {
            reverseIntake();
        }
        else if(driverInput.shootLowWithoutVisionOverride())
        {
            double frontSpeed = Shooter.frontShooterRate;
            double middleSpeed = Shooter.middleShooterRate;
            double rearSpeed = Shooter.rearShooterRate;
            double angle = Tilt.lowAngle;
            
            shooter.setShooterRates(frontSpeed, middleSpeed, rearSpeed);
            tilt.setTiltAngle(angle);
            intake.setIntakeSpeed(Constants.MOTOR_STOPPED);
            
            if(driverInput.magazineShootOverride())
            {
                magazine.setDesiredState(Constants.MAGAZINE_SHOOTING_STATE);
            }
            else
            {
                magazine.setDesiredState(Constants.MAGAZINE_READY_STATE);
            }
        }
        else if(driverInput.restoreToDefaultOverride())
        {
            intake.setIntakeSpeed(Constants.MOTOR_STOPPED);
            magazine.setDesiredState(Constants.MAGAZINE_READY_STATE);
            shooter.setShooterRates(Constants.SHOOTER_STOPPED_RATE, Constants.SHOOTER_STOPPED_RATE, Constants.SHOOTER_STOPPED_RATE);
            magazine.setDesiredState(Constants.MAGAZINE_READY_STATE);
            tilt.setTiltAngle(0.0);
        }
        else
        {
            intake.setIntakeSpeed(Constants.MOTOR_STOPPED);
            shooter.setShooterRates(Constants.SHOOTER_STOPPED_RATE, Constants.SHOOTER_STOPPED_RATE, Constants.SHOOTER_STOPPED_RATE);
            magazine.setDesiredState(Constants.MAGAZINE_READY_STATE);
        }
        
        if(driverInput.elevatorTopOverride())
        {
            //robotOutput.setElevatorMotors(Elevator.elevatorOverrideSpeed);
        }
        else if(driverInput.elevatorBottomOverride())
        {
            //robotOutput.setElevatorMotors(-1 * Elevator.elevatorOverrideSpeed);
        }
        else
        {
            robotOutput.setElevatorMotors(Constants.MOTOR_STOPPED);
        }
        
        intake.run();
        magazine.run();
        shooter.run();
        tilt.run();
        climber.run();
    }
    
    public void intakeFrisbees()
    {
        intake.setIntakeSpeed(Intake.intakeSpeed);
        magazine.setDesiredState(Constants.MAGAZINE_LOADING_STATE);
        shooter.stopShooter();
        tilt.setTiltAngle(0.0);
        setLightsNormal();
    }
    
    public void reverseIntake()
    {
        intake.setIntakeSpeed(Intake.outtakeSpeed);
        magazine.setDesiredState(Constants.MAGAZINE_LOADING_STATE);
        shooter.stopShooter();
        tilt.setTiltAngle(0.0);
        setLightsNormal();
    }
    
    public void shootLowWithVision()
    {
        intake.setIntakeSpeed(Constants.MOTOR_STOPPED);
        shooter.setShooterRates(Shooter.frontShooterRate, Shooter.middleShooterRate, Shooter.rearShooterRate);
        elevator.setDesiredPosition(Elevator.elevatorBottomPosition);
        magazine.setDesiredState(Constants.MAGAZINE_READY_STATE);
        
        setLightsToChecks();
        
        tilt.setTiltAngle(Tilt.lowAngle);
        
        if(elevator.atDesiredPosition())
        {
            if(SmartDashboard.getBoolean("found", false))
            {
                savedTiltAngle = SmartDashboard.getNumber("elevation", 0.0);
            }
            
            double desiredPosition = sensorInput.getTiltAngle() + savedTiltAngle + Tilt.shootLowAdditive;
            
            tilt.setTiltAngle(desiredPosition);
            
            if(driverInput.fireFrisbee())
            {
                magazine.setDesiredState(Constants.MAGAZINE_SHOOTING_STATE);
            }
        }
    }
    
    public void shootHighWithoutVision()
    {
        intake.setIntakeSpeed(Constants.MOTOR_STOPPED);
        shooter.setShooterRates(Shooter.frontShooterRate, Shooter.middleShooterRate, Shooter.rearShooterRate);
        elevator.setDesiredPosition(Elevator.elevatorTopPosition);
        magazine.setDesiredState(Constants.MAGAZINE_READY_STATE);
        
        setLightsToChecks();
        
        if(sensorInput.getElevatorEncoder() > 100)
        {
            tilt.setTiltAngle(Tilt.highAngle);
        }
        else
        {
            tilt.setTiltAngle(0.0);
        }
        
        if(elevator.atDesiredPosition())
        {
            if(driverInput.fireFrisbee() || (dashboardManager.getDS().isAutonomous() && shooter.isSpunUp() && tilt.isLocked()))
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
        shooter.setShooterRates(Shooter.frontShooterRate, Shooter.middleShooterRate, Shooter.rearShooterRate);
        tilt.setTiltAngle( (dashboardManager.getDS().isAutonomous()) ? Tilt.autonomousLowAngle : Tilt.lowAngle );
        
        setLightsToChecks();
        
        if(driverInput.fireFrisbee() || (dashboardManager.getDS().isAutonomous() && shooter.isSpunUp() && tilt.isLocked()))
        {
            magazine.setDesiredState(Constants.MAGAZINE_SHOOTING_STATE);
        }
    }
    
    public void feedFromSlot()
    {
        intake.setIntakeSpeed(Constants.MOTOR_STOPPED);
        shooter.stopShooter();
        magazine.setDesiredState(Constants.MAGAZINE_READY_STATE);
        setLightsNormal();
        
        elevator.setDesiredPosition(Elevator.elevatorFeedPosition);
        
        if(sensorInput.getElevatorEncoder() > 100)
        {
            tilt.setTiltAngle(Tilt.feederStationAngle);
        }
        else
        {
            tilt.setTiltAngle(0.0);
        }
        
        if(driverInput.fireFrisbee())
        {
            setLightsToChecks();
            shooter.setShooterRates(Shooter.frontShooterRate, Shooter.middleShooterRate, Shooter.rearShooterRate);
            
            if(shooter.isSpunUp())
            {
                magazine.setDesiredState(Constants.MAGAZINE_SHOOTING_STATE);
            }
        }
    }
    
    public void shootSide()
    {
        intake.setIntakeSpeed(Constants.MOTOR_STOPPED);
        shooter.setShooterRates(Shooter.frontShooterRate, Shooter.middleShooterRate, Shooter.rearShooterRate);
        magazine.setDesiredState(Constants.MAGAZINE_READY_STATE);
        elevator.setDesiredPosition(Elevator.elevatorBottomPosition);
        tilt.setTiltAngle(Tilt.sideAngle);
        
        setLightsToChecks();
        
        if(elevator.atDesiredPosition())
        {
            if(driverInput.fireFrisbee() || (dashboardManager.getDS().isAutonomous() && shooter.isSpunUp() && tilt.isLocked()))
            {
                magazine.setDesiredState(Constants.MAGAZINE_SHOOTING_STATE);
            }
        }
    }
    
    public void restoreDefaultPositions()
    {
        intake.setIntakeSpeed(Constants.MOTOR_STOPPED);
        shooter.stopShooter();
        magazine.setDesiredState(Constants.MAGAZINE_READY_STATE);
        tilt.setTiltAngle(0.0);
        setLightsNormal();
        
        elevator.setDesiredPosition(Elevator.elevatorBottomPosition);
    }
    
    public void setLightsTracking()
    {
        double currentAlliance = dashboardManager.getDS().getAlliance().value;
        
        if(currentAlliance == Constants.RED_ALLIANCE)
        {
            robotOutput.setLightsState(Constants.TRACKING_RED_ALLIANCE);
        }
        else if(currentAlliance == Constants.BLUE_ALLIANCE)
        {
            robotOutput.setLightsState(Constants.TRACKING_BLUE_ALLIANCE);
        }
    }
    
    public void setLightsLocked()
    {
        double currentAlliance = dashboardManager.getDS().getAlliance().value;
        
        if(currentAlliance == Constants.RED_ALLIANCE)
        {
            robotOutput.setLightsState(Constants.LOCKED_RED_ALLIANCE);
        }
        else if(currentAlliance == Constants.BLUE_ALLIANCE)
        {
            robotOutput.setLightsState(Constants.LOCKED_BLUE_ALLIANCE);
        }
    }
    
    public void setLightsNormal()
    {
        double currentAlliance = dashboardManager.getDS().getAlliance().value;
        
        if(currentAlliance == Constants.RED_ALLIANCE)
        {
            robotOutput.setLightsState(Constants.RED_SOLID);
        }
        else if(currentAlliance == Constants.BLUE_ALLIANCE)
        {
            robotOutput.setLightsState(Constants.BLUE_SOLID);
        }
    }
    
    private void setLightsToChecks()
    {
        if(elevator.atDesiredPosition() && shooter.isSpunUp() && tilt.isLocked())
        {
            setLightsLocked();
        }
        else
        {
            setLightsTracking();
        }
    }
}
