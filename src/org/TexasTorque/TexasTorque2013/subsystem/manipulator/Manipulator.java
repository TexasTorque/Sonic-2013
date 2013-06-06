package org.TexasTorque.TexasTorque2013.subsystem.manipulator;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.TexasTorque.TexasTorque2013.TorqueSubsystem;
import org.TexasTorque.TexasTorque2013.constants.Constants;
import org.TexasTorque.TexasTorque2013.subsystem.drivebase.Drivebase;

public class Manipulator extends TorqueSubsystem
{   
    private static Manipulator instance;
    
    private Drivebase drivebase;
    private Shooter shooter;
    private Elevator elevator;
    private Intake intake;
    private Magazine magazine;
    private Tilt tilt;
    private Climber climber;
    
    private double tempAngle;
    private double pastElevation;
    private int visionWait;
    private int visionCycleDelay;
    private int initialDelay;
    private int maxInitialDelay;
    
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
        drivebase = Drivebase.getInstance();
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
            else if(driverInput.shootHigh())
            {
                shootHigh();
            }
            else if(driverInput.shootSide())
            {
                shootSide();
            }
            else if(driverInput.shootLow())
            {
                shootLow();
            }
            else if(driverInput.shootFar() || driverInput.shootClose())
            {
                if(elevator.getDesiredPosition() == Elevator.elevatorBottomPosition)
                {
                    shootClose();
                }
                else
                {
                    shootFar();
                }
            }
            else if(driverInput.gotoSlotHeight())
            {
                feedFromSlot();
            }
            else if(driverInput.getAutoTargeting())
            {
                shootVision();
            }
            else
            {
                intake.setIntakeSpeed(Constants.MOTOR_STOPPED);
                shooter.stopShooter();
                magazine.setDesiredState(Constants.MAGAZINE_READY_STATE);
                tilt.setTiltAngle(0.0);
                tempAngle = 0.0;
                initialDelay = maxInitialDelay;
                setLightsNormal();
            }
            
            
            boolean incrementAngle = driverInput.incrementAngle();
            boolean decrementAngle = driverInput.decrementAngle();
            tilt.tiltAdjustments(incrementAngle, decrementAngle);
            
            magazine.setFireOverride(false);
            
            boolean climbPressed = driverInput.passiveHang();
            climber.setClimbMode(climbPressed);
            
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
        visionCycleDelay = params.getAsInt("V_CycleDelay", 5);
        maxInitialDelay = params.getAsInt("V_InitialDelay", 80);
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
        else if(driverInput.shootLowOverride())
        {
            double frontSpeed = Shooter.frontShooterRate;
            double rearSpeed = Shooter.rearShooterRate;
            double angle = Tilt.lowAngle;
            
            shooter.setShooterRates(frontSpeed, rearSpeed);
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
        else if(driverInput.shootSideOverride())
        {
            double frontSpeed = Shooter.frontShooterRate;
            double rearSpeed = Shooter.rearShooterRate;
            double angle = Tilt.sideAngle;
            
            shooter.setShooterRates(frontSpeed, rearSpeed);
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
        else if(driverInput.shootFarOverride())
        {
            double frontSpeed = Shooter.frontFarRate;
            double rearSpeed = Shooter.rearFarRate;
            double angle = Tilt.farAngle;
            
            shooter.setShooterRates(frontSpeed, rearSpeed);
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
        else if(driverInput.fireUnjam())
        {
            magazine.setFireOverride(true);
            magazine.setDesiredState(Constants.MAGAZINE_SHOOTING_STATE);
        }
        else if(driverInput.restoreToDefaultOverride())
        {
            intake.setIntakeSpeed(Constants.MOTOR_STOPPED);
            magazine.setFireOverride(false);
            magazine.setDesiredState(Constants.MAGAZINE_READY_STATE);
            shooter.stopShooter();
            tilt.setTiltAngle(0.0);
        }
        else
        {
            intake.setIntakeSpeed(Constants.MOTOR_STOPPED);
            shooter.stopShooter();
            magazine.setFireOverride(false);
            magazine.setDesiredState(Constants.MAGAZINE_READY_STATE);
        }
        
        //----- Manual Elevator Controls -----
        if(driverInput.elevatorTopOverride())
        {
            robotOutput.setElevatorMotors(-1 * driverInput.getElevatorJoystick());
        }
        else if(driverInput.elevatorBottomOverride())
        {
            robotOutput.setElevatorMotors(-1 * driverInput.getElevatorJoystick());
        }
        else
        {
            robotOutput.setElevatorMotors(Constants.MOTOR_STOPPED);
        }
        
        if(driverInput.getMadtownUnjam())
        {
            tilt.setTiltAngle(Tilt.madtownAngle);
            if(Math.abs(Tilt.madtownAngle - sensorInput.getTiltAngle()) < 15)
            {
                 magazine.setDesiredState(Constants.MAGAZINE_LOADING_STATE);
            }
            else
            {
                magazine.setDesiredState(Constants.MAGAZINE_READY_STATE);
            }
        }
        
        boolean climbPressed = driverInput.passiveHang();
        climber.setClimbMode(climbPressed);
        
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
    
    public void shootHigh()
    {
        intake.setIntakeSpeed(Constants.MOTOR_STOPPED);
        shooter.setShooterRates(Shooter.frontShooterRate, Shooter.rearShooterRate);
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
            if(driverInput.fireFrisbee())
            {
                magazine.setDesiredState(Constants.MAGAZINE_SHOOTING_STATE);
            }
        }
    }
    
    public void shootVision()
    {
        visionWait = (visionWait + 1) % visionCycleDelay;
        if(initialDelay > 0)
        {
            initialDelay--;
        }
        intake.setIntakeSpeed(Constants.MOTOR_STOPPED);
        magazine.setDesiredState(Constants.MAGAZINE_READY_STATE);
        elevator.setDesiredPosition(Elevator.elevatorBottomPosition);
        shooter.setShooterRates(Shooter.frontShooterRate, Shooter.rearShooterRate);
        
        if(tempAngle == 0.0)
        {
            tempAngle = Tilt.lowAngle;
            tilt.setTiltAngle(tempAngle);
        }
        else if(SmartDashboard.getBoolean("found",false) && visionWait == 0 && initialDelay == 0)
        {
            double currentAngle = sensorInput.getTiltAngle();
            double distance = SmartDashboard.getNumber("range", 100.0);
            if(distance < 0)
            {
                distance = 100.0;
            }
            double elevation = SmartDashboard.getNumber("elevation", 0.0);
            
            if(elevation > 180)
            {
                elevation -= 360;// elevation = -(360 - elevation);
            }
            
            if (pastElevation != elevation)
            {
            //double additive = Tilt.visionAdditive; //Additive could be a function of Distance?
            
            //double preFunctionAngle = currentAngle + elevation;
            //double funcAdditive = .3467* preFunctionAngle + -13.25;
            
            //double funcAdditive = (currentAngle * Tilt.visionAdditiveConst) + Tilt.visionAdditiveB;
            
            /*double funcAdditive = (distance * distance * distance * distance * distance * Tilt.visionAdditiveFifth);
            funcAdditive += (distance * distance * distance * distance * Tilt.visionAdditiveFourth);
            funcAdditive += (distance * distance * distance * Tilt.visionAdditiveThird);
            funcAdditive += (distance * distance * Tilt.visionAdditiveSecond);
            funcAdditive += (distance * Tilt.visionAdditiveFirst);
            funcAdditive += Tilt.visionAdditive;*/
                
            //double funcAdditive = Tilt.visionTanA / (Math.tan(Tilt.visionTanB * distance * Math.PI / 180 + Tilt.visionTanC)) + Tilt.visionTanD;
            //double funcAdditive = 0;
                
            //tempAngle = currentAngle + elevation + funcAdditive;
       
                tempAngle = SmartDashboard.getNumber("setpoint", tempAngle);
                tilt.setTiltAngle(tempAngle);
            
            pastElevation = elevation;
            }
        }
        
        setLightsToChecks();
        
        if(driverInput.fireFrisbee())
        {
            magazine.setDesiredState(Constants.MAGAZINE_SHOOTING_STATE);
        }
    }
    
    
    public void shootLow()
    {
        intake.setIntakeSpeed(Constants.MOTOR_STOPPED);
        magazine.setDesiredState(Constants.MAGAZINE_READY_STATE);
        elevator.setDesiredPosition(Elevator.elevatorBottomPosition);
        shooter.setShooterRates(Shooter.frontShooterRate, Shooter.rearShooterRate);
        tilt.setTiltAngle(Tilt.lowAngle);
        
        setLightsToChecks();
        
        if(driverInput.fireFrisbee())
        {
            magazine.setDesiredState(Constants.MAGAZINE_SHOOTING_STATE);
        }
    }
    
    public void shootFar()
    {
        intake.setIntakeSpeed(Constants.MOTOR_STOPPED);
        magazine.setDesiredState(Constants.MAGAZINE_READY_STATE);
        elevator.setDesiredPosition(Elevator.elevatorFeedPosition);
        shooter.setShooterRates(Shooter.frontFarRate, Shooter.rearFarRate);
        tilt.setTiltAngle(Tilt.farAngle);
        
        setLightsToChecks();
        
        if(driverInput.fireFrisbee())
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
            shooter.setShooterRates(Shooter.frontShooterRate, Shooter.rearShooterRate);
            
            if(shooter.isSpunUp())
            {
                magazine.setDesiredState(Constants.MAGAZINE_SHOOTING_STATE);
            }
        }
    }
    
    public void shootSide()
    {
        intake.setIntakeSpeed(Constants.MOTOR_STOPPED);
        shooter.setShooterRates(Shooter.frontShooterRate, Shooter.rearShooterRate);
        magazine.setDesiredState(Constants.MAGAZINE_READY_STATE);
        elevator.setDesiredPosition(Elevator.elevatorBottomPosition);
        tilt.setTiltAngle(Tilt.sideAngle);
        
        setLightsToChecks();
        
        if(elevator.atDesiredPosition())
        {
            if(driverInput.fireFrisbee())
            {
                magazine.setDesiredState(Constants.MAGAZINE_SHOOTING_STATE);
            }
        }
    }
    
    public void shootClose()
    {
        intake.setIntakeSpeed(Constants.MOTOR_STOPPED);
        shooter.setShooterRates(Shooter.frontShooterRate, Shooter.rearShooterRate);
        magazine.setDesiredState(Constants.MAGAZINE_READY_STATE);
        elevator.setDesiredPosition(Elevator.elevatorBottomPosition);
        tilt.setTiltAngle(Tilt.closeAngle);
        
        setLightsToChecks();
        
        if(elevator.atDesiredPosition())
        {
            if(driverInput.fireFrisbee())
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
            if(sensorInput.getPSI() < Constants.PRESSURE_THRESHOLD)
            {
                robotOutput.setLightsState(Constants.YELLOW_RED_ALLIANCE);
            }
            else
            {
                robotOutput.setLightsState(Constants.RED_SOLID);
            }
        }
        else if(currentAlliance == Constants.BLUE_ALLIANCE)
        {
            if(sensorInput.getPSI() < Constants.PRESSURE_THRESHOLD)
            {
                robotOutput.setLightsState(Constants.YELLOW_BLUE_ALLIANCE);
            }
            else
            {
                robotOutput.setLightsState(Constants.BLUE_SOLID);
            }
        }
    }
    
    private void setLightsToChecks()
    {   
        if(elevator.atDesiredPosition() && shooter.isSpunUp() && tilt.isLocked() && drivebase.isLocked())
        {
            setLightsLocked();
        }
        else
        {
            setLightsTracking();
        }
        
        if(sensorInput.getPSI() < Constants.PRESSURE_THRESHOLD || SmartDashboard.getBoolean("found", false))
        {
            robotOutput.setLightsState(Constants.YELLOW_RED_ALLIANCE);
        }
       
    }
}
