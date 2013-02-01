package org.TexasTorque.TexasTorque2013.io;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
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
    private DoubleSolenoid ptoDoubleSolenoid;
    private Solenoid wheelyBar;
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
        ptoDoubleSolenoid = new DoubleSolenoid(Ports.PTO_SOLENOID_A_PORT,Ports.PTO_SOLENOID_B_PORT);
        wheelyBar = new Solenoid(Ports.WHEELY_BAR_SOLENOID_PORT);
        //----- Drive Motors -----
        frontLeftDriveMotor = new Motor(new Victor(Ports.SIDECAR_ONE, Ports.FRONT_LEFT_MOTOR_PORT), true, true);
        rearLeftDriveMotor = new Motor(new Victor(Ports.SIDECAR_ONE, Ports.REAR_LEFT_MOTOR_PORT), true, true);
        frontRightDriveMotor = new Motor(new Victor(Ports.SIDECAR_ONE, Ports.FRONT_RIGHT_MOTOR_PORT), false, true);
        rearRightDriveMotor = new Motor(new Victor(Ports.SIDECAR_ONE, Ports.REAR_RIGHT_MOTOR_PORT), false, true);
        //----- Shooter Subsystem Motors-----
        frontShooterMotorA = new Motor(new Victor(Ports.SIDECAR_TWO, Ports.FRONT_SHOOTER_MOTOR_A_PORT), false, true);
        frontShooterMotorB = new Motor(new Victor(Ports.SIDECAR_TWO, Ports.FRONT_SHOOTER_MOTOR_B_PORT), false, true);
        rearShooterMotorA = new Motor(new Victor(Ports.SIDECAR_TWO, Ports.REAR_SHOOTER_MOTOR_A_PORT), false, true);
        rearShooterMotorB = new Motor(new Victor(Ports.SIDECAR_TWO, Ports.REAR_SHOOTER_MOTOR_B_PORT), false, true);
        shooterTiltMotor = new Motor(new Victor(Ports.SIDECAR_TWO, Ports.SHOOTER_TILT_MOTOR_PORT), false, true);
        //----- Misc Motors -----
        intakeMotor = new Motor(new Victor(Ports.SIDECAR_ONE, Ports.INTAKE_MOTOR_PORT), false, true);
        elevatorMotorLeft = new Motor(new Victor(Ports.SIDECAR_ONE, Ports.ELEVATOR_MOTOR_PORT_LEFT), false, true);
        elevatorMotorRight = new Motor(new Victor(Ports.SIDECAR_ONE, Ports.ELEVATOR_MOTOR_PORT_RIGHT), false, true);
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
    
    public synchronized void setShooterTiltMotor(double speed)
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
    
    public synchronized void setFrisbeeLifter(boolean extend)
    {
        if(extend)
        {
            frisbeeLifter.set(DoubleSolenoid.Value.kForward);
        }
        else
        {
            frisbeeLifter.set(DoubleSolenoid.Value.kReverse);
        }
    }
    
    public synchronized void setPTO(boolean extend)
    {
        if(extend)
        {
            ptoDoubleSolenoid.set(DoubleSolenoid.Value.kForward);
        }
        else
        {
            ptoDoubleSolenoid.set(DoubleSolenoid.Value.kReverse);
        }
    }
    
    public synchronized void setWheelyBar(boolean extend)
    {
        wheelyBar.set(extend);
    }
    
    
}
