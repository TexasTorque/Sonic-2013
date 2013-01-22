package org.TexasTorque.TexasTorque2013.io;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Victor;
import org.TexasTorque.TexasTorque2013.constants.Ports;
import org.TexasTorque.TorqueLib.util.Motor;

public class RobotOutput
{
    private static RobotOutput instance;
    private Compressor compressor;
    private DoubleSolenoid shifters;
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
    //----- Misc Motors -----
    private Motor intakeMotor;
    
    public RobotOutput()
    {
        compressor = new Compressor(Ports.SIDECAR_ONE, Ports.PRESSURE_SWITCH_PORT, Ports.SIDECAR_ONE, Ports.COMPRESSOR_RELAY_PORT);
        shifters = new DoubleSolenoid(Ports.SHIFTERS_FORWARD_PORT, Ports.SHIFTERS_REVERSE_PORT);
        //----- Drive Motors -----
        frontLeftDriveMotor = new Motor(new Victor(Ports.SIDECAR_ONE, Ports.FRONT_LEFT_MOTOR_PORT), true, false);
        rearLeftDriveMotor = new Motor(new Victor(Ports.SIDECAR_ONE, Ports.REAR_LEFT_MOTOR_PORT), true, false);
        frontRightDriveMotor = new Motor(new Victor(Ports.SIDECAR_ONE, Ports.FRONT_RIGHT_MOTOR_PORT), false, false);
        rearRightDriveMotor = new Motor(new Victor(Ports.SIDECAR_ONE, Ports.REAR_RIGHT_MOTOR_PORT), false, false);
        //----- Shooter Motors-----
        frontShooterMotorA = new Motor(new Victor(Ports.SIDECAR_TWO, Ports.FRONT_SHOOTER_MOTOR_A_PORT), false, false);
        frontShooterMotorB = new Motor(new Victor(Ports.SIDECAR_TWO, Ports.FRONT_SHOOTER_MOTOR_B_PORT), false, false);
        rearShooterMotorA = new Motor(new Victor(Ports.SIDECAR_TWO, Ports.REAR_SHOOTER_MOTOR_A_PORT), false, false);
        rearShooterMotorB = new Motor(new Victor(Ports.SIDECAR_TWO, Ports.REAR_SHOOTER_MOTOR_B_PORT), false, false);
        //----- Misc Motors -----
        intakeMotor = new Motor(new Victor(Ports.SIDECAR_ONE, Ports.INTAKE_MOTOR_PORT), false, false);
        
        compressor.start();
    }
 
    public synchronized static RobotOutput getInstance()
    {
        return (instance == null) ? instance = new RobotOutput() : instance;
    }
    
    public synchronized void setShifters(boolean highGear)
    {
        if(highGear)
        {
            shifters.set(DoubleSolenoid.Value.kForward);
        }
        else
        {
            shifters.set(DoubleSolenoid.Value.kReverse);
        }
    }
    
    public synchronized void setLeftDriveMotors(double leftSpeed)
    {
        frontLeftDriveMotor.Set(leftSpeed);
        rearLeftDriveMotor.Set(leftSpeed);
    }
    
    public synchronized void setRightDriveMotors(double rightSpeed)
    {
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
    
}
