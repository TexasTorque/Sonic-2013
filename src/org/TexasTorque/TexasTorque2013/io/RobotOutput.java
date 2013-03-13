package org.TexasTorque.TexasTorque2013.io;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Victor;
import java.util.Vector;
import org.TexasTorque.TexasTorque2013.constants.Ports;
import org.TexasTorque.TorqueLib.util.AdaFruitLights;
import org.TexasTorque.TorqueLib.util.Motor;

public class RobotOutput
{
    private static RobotOutput instance;
    
    private AdaFruitLights lights;
    private Vector lightsVector;
    
    //----- Pneumatics -----
    private Compressor compressor;
    private Solenoid driveShifter;
    private Solenoid firingPin;
    private DoubleSolenoid frisbeeLifter;
    private DoubleSolenoid passiveClimber;
    //----- Drive Motors -----
    private Motor frontLeftDriveMotor;
    private Motor middleLeftDriveMotor;
    private Motor rearLeftDriveMotor;
    private Motor frontRightDriveMotor;
    private Motor middleRightDriveMotor;
    private Motor rearRightDriveMotor;
    //----- Shooter Motors -----
    private Motor frontShooterMotor;
    private Motor middleShooterMotor;
    private Motor rearShooterMotor;
    private Motor shooterTiltMotor;
    //----- Misc Motors -----
    private Motor intakeMotor;
    private Motor elevatorMotorLeft;
    private Motor elevatorMotorRight;
    
    public RobotOutput()
    {   
        
        lightsVector = new Vector();
        lightsVector.addElement(new DigitalOutput(Ports.SIDECAR_TWO, Ports.LIGHTS_A_PORT));
        lightsVector.addElement(new DigitalOutput(Ports.SIDECAR_TWO, Ports.LIGHTS_B_PORT));
        lightsVector.addElement(new DigitalOutput(Ports.SIDECAR_TWO, Ports.LIGHTS_C_PORT));
        lightsVector.addElement(new DigitalOutput(Ports.SIDECAR_TWO, Ports.LIGHTS_D_PORT));
        lights = new AdaFruitLights(lightsVector);
        //----- Pneumatics -----
        compressor = new Compressor(Ports.SIDECAR_ONE, Ports.PRESSURE_SWITCH_PORT, Ports.SIDECAR_ONE, Ports.COMPRESSOR_RELAY_PORT);
        driveShifter = new Solenoid(Ports.DRIVE_SHIFTER_PORT);
        firingPin = new Solenoid(Ports.LOADER_SOLENOID_PORT);
        frisbeeLifter = new DoubleSolenoid(Ports.FRISBEE_LIFTER_SOLENOID_A_PORT, Ports.FRISBEE_LIFTER_SOLENOID_B_PORT);
        passiveClimber = new DoubleSolenoid(Ports.PASSIVE_CLIMBER_A_PORT, Ports.PASSIVE_CLIMBER_B_PORT);
        //----- Drive Motors -----
        frontLeftDriveMotor = new Motor(new Victor(Ports.SIDECAR_TWO, Ports.FRONT_LEFT_DRIVE_MOTOR_PORT), false, true);
        middleLeftDriveMotor = new Motor(new Victor(Ports.SIDECAR_TWO, Ports.MIDDLE_LEFT_DRIVE_MOTOR_PORT), false, true);
        rearLeftDriveMotor = new Motor(new Victor(Ports.SIDECAR_TWO, Ports.REAR_LEFT_DRIVE_MOTOR_PORT), false, true);
        frontRightDriveMotor = new Motor(new Victor(Ports.SIDECAR_ONE, Ports.FRONT_RIGHT_DRIVE_MOTOR_PORT), true, true);
        middleRightDriveMotor = new Motor(new Victor(Ports.SIDECAR_ONE, Ports.MIDDLE_RIGHT_DRIVE_MOTOR_PORT), true, true);
        rearRightDriveMotor = new Motor(new Victor(Ports.SIDECAR_ONE, Ports.REAR_RIGHT_DRIVE_MOTOR_PORT), true, true);
        //----- Shooter Subsystem Motors-----
        frontShooterMotor = new Motor(new Victor(Ports.SIDECAR_TWO, Ports.FRONT_SHOOTER_MOTOR_PORT), false, true);
        middleShooterMotor = new Motor(new Victor(Ports.SIDECAR_TWO, Ports.MIDDLE_SHOOTER_PORT), false, true);
        rearShooterMotor = new Motor(new Victor(Ports.SIDECAR_TWO, Ports.REAR_SHOOTER_PORT), true, true);
        shooterTiltMotor = new Motor(new Victor(Ports.SIDECAR_TWO, Ports.SHOOTER_TILT_MOTOR_PORT), false, true);
        //----- Misc Motors -----
        intakeMotor = new Motor(new Victor(Ports.SIDECAR_ONE, Ports.INTAKE_MOTOR_PORT), false, true);
        elevatorMotorLeft = new Motor(new Victor(Ports.SIDECAR_ONE, Ports.ELEVATOR_MOTOR_PORT_LEFT), false, true);
        elevatorMotorRight = new Motor(new Victor(Ports.SIDECAR_TWO, Ports.ELEVATOR_MOTOR_PORT_RIGHT), true, true);
        compressor.start();
    }
 
    public synchronized static RobotOutput getInstance()
    {
        return (instance == null) ? instance = new RobotOutput() : instance;
    }
    
    public void setLightsState(int state)
    {
        lights.setDesiredState(state);
    }
    
    public void runLights()
    {
        lights.run();
    }
    
    public void setDriveMotors(double leftSpeed, double rightSpeed)
    {
        frontLeftDriveMotor.Set(leftSpeed);
        middleLeftDriveMotor.Set(leftSpeed * 0.8);
        rearLeftDriveMotor.Set(leftSpeed);
        frontRightDriveMotor.Set(rightSpeed);
        middleRightDriveMotor.Set(rightSpeed * 0.8);
        rearRightDriveMotor.Set(rightSpeed);
    }
    
    public void setShooterMotors(double frontSpeed, double middleSpeed, double rearSpeed)
    {
        frontShooterMotor.Set(frontSpeed);
        middleShooterMotor.Set(middleSpeed);
        rearShooterMotor.Set(rearSpeed);
    }
    
    public void setIntakeMotor(double speed)
    {
        intakeMotor.Set(speed);
    }
    
    public void setElevatorMotors(double speed)
    {
        elevatorMotorLeft.Set(speed);
        elevatorMotorRight.Set(speed);
    }
    
    public void setTiltMotor(double speed)
    {
        shooterTiltMotor.Set(speed);
    }
    
    public void setFiringPin(boolean ready)
    {
        firingPin.set(!ready);
    }
    
    public void setShifters(boolean highGear)
    {
        driveShifter.set(highGear);
    }
    
    public void setFrisbeeLifter(boolean retracted)
    {
        if(retracted)
        {
            frisbeeLifter.set(DoubleSolenoid.Value.kForward);
        }
        else
        {
            frisbeeLifter.set(DoubleSolenoid.Value.kReverse);
        }
    }
    
    public void setPassiveClimber(boolean raised)
    {
        if(raised)
        {
            passiveClimber.set(DoubleSolenoid.Value.kForward);
        }
        else
        {
            passiveClimber.set(DoubleSolenoid.Value.kReverse);
        }
    }
}
