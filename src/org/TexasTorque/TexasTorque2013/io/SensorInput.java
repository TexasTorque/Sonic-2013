package org.TexasTorque.TexasTorque2013.io;

import edu.wpi.first.wpilibj.AnalogChannel;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Gyro;
import org.TexasTorque.TexasTorque2013.constants.Constants;
import org.TexasTorque.TexasTorque2013.constants.Ports;
import org.TexasTorque.TorqueLib.sensor.TorqueEncoder;
import org.TexasTorque.TorqueLib.sensor.TorquePotentiometer;

public class SensorInput
{
    private static SensorInput instance;
    //----- Encoder -----
    private TorqueEncoder leftDriveEncoder;
    private TorqueEncoder rightDriveEncoder;
    private TorqueEncoder frontShooterEncoder;
    private TorqueEncoder rearShooterEncoder;
    private TorqueEncoder elevatorEncoder;
    //----- Analog -----
    private AnalogChannel pressureSensor;
    private AnalogChannel gyroChannel;
    private Gyro gyro;
    private TorquePotentiometer tiltPotentiometer;
    //----- Other Digital -----
    private DigitalInput wheelyBarSwitch;
    
    public SensorInput()
    {
        //----- Encoders -----
        leftDriveEncoder = new TorqueEncoder(Ports.SIDECAR_TWO, Ports.LEFT_DRIVE_ENCODER_A_PORT, Ports.SIDECAR_TWO, Ports.LEFT_DRIVE_ENCODER_B_PORT, true);
        rightDriveEncoder = new TorqueEncoder(Ports.SIDECAR_ONE, Ports.RIGHT_DRIVE_ENCODER_A_PORT, Ports.SIDECAR_ONE, Ports.RIGHT_DRIVE_ENCODER_B_PORT, true);
        frontShooterEncoder = new TorqueEncoder(Ports.SIDECAR_ONE, Ports.FRONT_SHOOTER_ENCODER_A_PORT, Ports.SIDECAR_ONE, Ports.FRONT_SHOOTER_ENCODER_B_PORT, false);
        rearShooterEncoder = new TorqueEncoder(Ports.SIDECAR_ONE, Ports.REAR_SHOOTER_ENCODER_A_PORT, Ports.SIDECAR_ONE, Ports.REAR_SHOOTER_ENCODER_B_PORT, false);
        elevatorEncoder = new TorqueEncoder(Ports.SIDECAR_ONE, Ports.ELEVATOR_ENCODER_A_PORT, Ports.SIDECAR_ONE, Ports.ELEVATOR_ENCODER_B_PORT, true);
        //----- Gyro -----
        gyroChannel = new AnalogChannel(Ports.GYRO_PORT);
        gyroChannel.setAccumulatorDeadband(Constants.GYRO_ACCUMULATOR_DEADBAND);
        gyro = new Gyro(gyroChannel);
        gyro.reset();
        gyro.setSensitivity(Constants.GYRO_SENSITIVITY);
        //----- Misc -----
        pressureSensor = new AnalogChannel(Ports.PRESSURE_SENSOR_PORT);
        wheelyBarSwitch = new DigitalInput(Ports.SIDECAR_ONE, Ports.WHEELY_BAR_SWITCH_PORT);
        tiltPotentiometer = new TorquePotentiometer(Ports.TILT_POTENTIOMETER_PORT);
        tiltPotentiometer.setRange(Constants.POTENTIOMETER_LOW_VOLTAGE, Constants.POTENTIOMETER_HIGH_VOLTAGE);
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
        frontShooterEncoder.setOptions(20, 100, true);
        rearShooterEncoder.setOptions(20, 100, true);
        elevatorEncoder.setOptions(20, 250, false);
        leftDriveEncoder.start();
        rightDriveEncoder.start();        
        frontShooterEncoder.start();
        rearShooterEncoder.start();
        elevatorEncoder.start();
    }
    
    public synchronized void resetEncoders()
    {
        leftDriveEncoder.resetEncoder();
        rightDriveEncoder.resetEncoder();
        frontShooterEncoder.resetEncoder();
        rearShooterEncoder.resetEncoder();
        elevatorEncoder.resetEncoder();
    }
    
    public synchronized int getLeftDriveEncoder()
    {
        return leftDriveEncoder.get();
    }
    
    public synchronized int getRightDriveEncoder()
    {
        return rightDriveEncoder.get();
    }
    
    public synchronized double getLeftDriveEncoderRate()
    {
        return leftDriveEncoder.getRate();
    }
    
    public synchronized double getRightDriveEncoderRate()
    {
        return rightDriveEncoder.getRate();
    }
    
    public synchronized int getFrontShooterRate()
    {
        return (int)frontShooterEncoder.getRate();
    }
    
    public synchronized int getRearShooterRate()
    {
        return (int)rearShooterEncoder.getRate();
    }
    
    public synchronized int getElevatorEncoder()
    {
        return elevatorEncoder.get();
    }
    
    public synchronized double getGyroAngle()
    {
        return limitGyroAngle(-gyro.getAngle() * 2);
    }
    
    private synchronized double limitGyroAngle(double angle)
    {
        while(angle >= 360.0)
        {
            angle -= 360.0;
        }
        while(angle < 0.0)
        {
            angle += 360.0;
        }
        if(angle > 180)
        {
            angle -= 360;
        }
        return angle;
    }
    
    public synchronized double getPressure()
    {
        return pressureSensor.getVoltage();
    }
    
    public synchronized double getTiltPotentiometer()
    {
        return tiltPotentiometer.get();
    }
    
    public synchronized double getTiltAngle()
    {
        return getTiltPotentiometer() * 90;
    }
    
    public synchronized boolean getWheelyBarSwitch()
    {
        return wheelyBarSwitch.get();
    }
    
}
