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
    private Motor middleLeftDriveMotor;
    private Motor rearLeftDriveMotor;
    private Motor frontRightDriveMotor;
    private Motor middleRightDriveMotor;
    private Motor rearRightDriveMotor;
    
    public RobotOutput()
    {
        compressor = new Compressor(Ports.SIDECAR_ONE, Ports.PRESSURE_SWITCH_PORT, Ports.SIDECAR_ONE, Ports.COMPRESSOR_RELAY_PORT);
        shifters = new DoubleSolenoid(Ports.SHIFTERS_FORWARD_PORT, Ports.SHIFTERS_REVERSE_PORT);
        frontLeftDriveMotor = new Motor(new Victor(Ports.SIDECAR_ONE, Ports.FRONT_LEFT_MOTOR_PORT), true, false);
        middleLeftDriveMotor = new Motor(new Victor(Ports.SIDECAR_ONE, Ports.MIDDLE_LEFT_MOTOR_PORT), true, false);
        rearLeftDriveMotor = new Motor(new Victor(Ports.SIDECAR_ONE, Ports.REAR_LEFT_MOTOR_PORT), true, false);
        frontRightDriveMotor = new Motor(new Victor(Ports.SIDECAR_ONE, Ports.FRONT_RIGHT_MOTOR_PORT), false, false);
        middleRightDriveMotor = new Motor(new Victor(Ports.SIDECAR_ONE, Ports.MIDDLE_RIGHT_MOTOR_PORT), false, false);
        rearRightDriveMotor = new Motor(new Victor(Ports.SIDECAR_ONE, Ports.REAR_RIGHT_MOTOR_PORT), false, false);
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
        middleLeftDriveMotor.Set(leftSpeed);
        rearLeftDriveMotor.Set(leftSpeed);
    }
    
    public synchronized void setRightDriveMotors(double rightSpeed)
    {
        frontRightDriveMotor.Set(rightSpeed);
        middleRightDriveMotor.Set(rightSpeed);
        rearRightDriveMotor.Set(rightSpeed);
    }
    
}
