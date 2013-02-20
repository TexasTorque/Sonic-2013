package org.TexasTorque.TexasTorque2013.io;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Victor;
import org.TexasTorque.TexasTorque2013.constants.Ports;
import org.TexasTorque.TorqueLib.util.Motor;

public class RobotOutput
{
    private static RobotOutput instance;
    //----- Pneumatics -----
    private Compressor compressor;
    private Solenoid driveShifter;
    private Solenoid loader;
    private DoubleSolenoid frisbeeLifter;
    private DoubleSolenoid passiveClimber;
    private Relay ptoShifter;
    //----- Drive Motors -----
    private Motor frontLeftDriveMotor;
    private Motor rearLeftDriveMotor;
    private Motor frontRightDriveMotor;
    private Motor rearRightDriveMotor;
    //----- Shooter Motors -----
    private Motor frontShooterMotorA;
    private Motor frontShooterMotorB;
    private Motor rearShooterMotorA;
    private Motor rearShooterMotorB;
    private Motor shooterTiltMotor;
    //----- Misc Motors -----
    private Motor intakeMotor;
    private Motor elevatorMotorLeft;
    private Motor elevatorMotorRight;
    
    public RobotOutput()
    {   
        //----- Pneumatics -----
        compressor = new Compressor(Ports.SIDECAR_ONE, Ports.PRESSURE_SWITCH_PORT, Ports.SIDECAR_ONE, Ports.COMPRESSOR_RELAY_PORT);
        driveShifter = new Solenoid(Ports.DRIVE_SHIFTER_PORT);
        loader = new Solenoid(Ports.LOADER_SOLENOID_PORT);
        frisbeeLifter = new DoubleSolenoid(Ports.FRISBEE_LIFTER_SOLENOID_A_PORT, Ports.FRISBEE_LIFTER_SOLENOID_B_PORT);
        passiveClimber = new DoubleSolenoid(Ports.PASSIVE_CLIMBER_A_PORT, Ports.PASSIVE_CLIMBER_B_PORT);
        ptoShifter = new Relay(Ports.SIDECAR_ONE, Ports.PTO_RELAY_PORT, Relay.Direction.kBoth);
        //----- Drive Motors -----
        frontLeftDriveMotor = new Motor(new Victor(Ports.SIDECAR_TWO, Ports.FRONT_LEFT_MOTOR_PORT), true, true);
        rearLeftDriveMotor = new Motor(new Victor(Ports.SIDECAR_TWO, Ports.REAR_LEFT_MOTOR_PORT), true, true);
        frontRightDriveMotor = new Motor(new Victor(Ports.SIDECAR_ONE, Ports.FRONT_RIGHT_MOTOR_PORT), false, true);
        rearRightDriveMotor = new Motor(new Victor(Ports.SIDECAR_ONE, Ports.REAR_RIGHT_MOTOR_PORT), false, true);
        //----- Shooter Subsystem Motors-----
        frontShooterMotorA = new Motor(new Victor(Ports.SIDECAR_TWO, Ports.FRONT_SHOOTER_MOTOR_A_PORT), false, true);
        frontShooterMotorB = new Motor(new Victor(Ports.SIDECAR_TWO, Ports.FRONT_SHOOTER_MOTOR_B_PORT), false, true);
        rearShooterMotorA = new Motor(new Victor(Ports.SIDECAR_TWO, Ports.REAR_SHOOTER_MOTOR_A_PORT), false, true);
        rearShooterMotorB = new Motor(new Victor(Ports.SIDECAR_TWO, Ports.REAR_SHOOTER_MOTOR_B_PORT), false, true);
        shooterTiltMotor = new Motor(new Victor(Ports.SIDECAR_TWO, Ports.SHOOTER_TILT_MOTOR_PORT), true, true);
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
    
    public synchronized void setDriveMotors(double leftSpeed, double rightSpeed)
    {
        frontLeftDriveMotor.Set(leftSpeed);
        rearLeftDriveMotor.Set(leftSpeed);
        frontRightDriveMotor.Set(rightSpeed);
        rearRightDriveMotor.Set(rightSpeed);
    }
    
    public synchronized void setShooterMotors(double frontSpeed, double rearSpeed)
    {
        frontShooterMotorA.Set(frontSpeed);
        frontShooterMotorB.Set(frontSpeed);
        rearShooterMotorA.Set(rearSpeed);
        rearShooterMotorB.Set(rearSpeed);
    }
    
    public synchronized void setIntakeMotor(double speed)
    {
        intakeMotor.Set(speed);
    }
    
    public synchronized void setElevatorMotors(double speed)
    {
        elevatorMotorLeft.Set(speed);
        elevatorMotorRight.Set(speed);
    }
    
    public synchronized void setTiltMotor(double speed)
    {
        shooterTiltMotor.Set(speed);
    }
    
    public synchronized void setLoaderSolenoid(boolean extend)
    {
        loader.set(extend);
    }
    
    public synchronized void setShifters(boolean highGear)
    {
        driveShifter.set(highGear);
    }
    
    public synchronized void setFrisbeeLifter(boolean retracted)
    {
        if(retracted)
        {
            frisbeeLifter.set(DoubleSolenoid.Value.kReverse);
        }
        else
        {
            frisbeeLifter.set(DoubleSolenoid.Value.kForward);
        }
    }
    
    public synchronized void setPassiveClimber(boolean raised)
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
    
    public synchronized void setPTOShifters(boolean extend)
    {
        if(extend)
        {
            ptoShifter.set(Relay.Value.kForward);
        }
        else
        {
            ptoShifter.set(Relay.Value.kReverse);
        }
    }
}
