package org.TexasTorque.TexasTorque2013.io;

import edu.wpi.first.wpilibj.AnalogChannel;
import edu.wpi.first.wpilibj.Gyro;
import org.TexasTorque.TexasTorque2013.constants.Constants;
import org.TexasTorque.TexasTorque2013.constants.Ports;
import org.TexasTorque.TorqueLib.sensor.TorqueEncoder;
import org.TexasTorque.TorqueLib.sensor.TorquePotentiometer;

public class SensorInput
{
    private static SensorInput instance;
    private TorqueEncoder leftDriveEncoder;
    private TorqueEncoder rightDriveEncoder;
    private TorqueEncoder frontShooterEncoder;
    private TorqueEncoder rearShooterEncoder;
    private AnalogChannel pressureSensor;
    private AnalogChannel gyroChannel;
    private Gyro gyro;
    private TorquePotentiometer tiltPotentiometer;
    
    public SensorInput()
    {
        leftDriveEncoder = new TorqueEncoder(Ports.SIDECAR_ONE, Ports.LEFT_DRIVE_ENCODER_A_PORT, Ports.SIDECAR_ONE, Ports.LEFT_DRIVE_ENCODER_B_PORT, true);
        rightDriveEncoder = new TorqueEncoder(Ports.SIDECAR_ONE, Ports.RIGHT_DRIVE_ENCODER_A_PORT, Ports.SIDECAR_ONE, Ports.RIGHT_DRIVE_ENCODER_B_PORT, false);
        frontShooterEncoder = new TorqueEncoder(Ports.SIDECAR_ONE, Ports.FRONT_SHOOTER_ENCODER_A_PORT, Ports.SIDECAR_ONE, Ports.FRONT_SHOOTER_ENCODER_B_PORT, false);
        rearShooterEncoder = new TorqueEncoder(Ports.SIDECAR_ONE, Ports.REAR_SHOOTER_ENCODER_A_PORT, Ports.SIDECAR_ONE, Ports.REAR_SHOOTER_ENCODER_B_PORT, false);
        pressureSensor = new AnalogChannel(Ports.PRESSURE_SENSOR_PORT);
        gyroChannel = new AnalogChannel(Ports.GYRO_PORT);
        gyroChannel.setAccumulatorDeadband(Constants.GYRO_ACCUMULATOR_DEADBAND);
        gyro = new Gyro(gyroChannel);
        gyro.reset();
        gyro.setSensitivity(Constants.GYRO_SENSITIVITY);
        tiltPotentiometer = new TorquePotentiometer(Ports.TILT_POTENTIOMETER_PORT);
        tiltPotentiometer.setRange(0.0, 5.0);
        startEncoders();
    }
    
    public synchronized static SensorInput getInstance()
    {
        return (instance == null) ? instance = new SensorInput() : instance;
    }
    
    private void startEncoders()
    {
        leftDriveEncoder.setOptions(20, 250, false);
        rightDriveEncoder.setOptions(20, 250, false);
        frontShooterEncoder.setOptions(20, 250, true);
        rearShooterEncoder.setOptions(20, 250, true);
        leftDriveEncoder.start();
        rightDriveEncoder.start();        
        frontShooterEncoder.start();
        rearShooterEncoder.start();
    }
    
    public synchronized void resetEncoders()
    {
        leftDriveEncoder.resetEncoder();
        rightDriveEncoder.resetEncoder();
        frontShooterEncoder.resetEncoder();
        rearShooterEncoder.resetEncoder();
    }
    
    public synchronized int getLeftDriveEncoder()
    {
        return leftDriveEncoder.get();
    }
    
    public synchronized int getRightDriveEncoder()
    {
        return rightDriveEncoder.get();
    }
    
    public synchronized double getGyroAngle()
    {
        return gyro.getAngle() * 2;
    }
    
    public synchronized double getPressure()
    {
        return pressureSensor.getVoltage();
    }
    
    public synchronized double getTiltPotentiometer()
    {
        return tiltPotentiometer.get();
    }
    
}
