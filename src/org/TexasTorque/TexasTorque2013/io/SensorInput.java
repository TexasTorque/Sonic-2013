package org.TexasTorque.TexasTorque2013.io;

import edu.wpi.first.wpilibj.AnalogChannel;
import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.Watchdog;
import org.TexasTorque.TexasTorque2013.constants.Constants;
import org.TexasTorque.TexasTorque2013.constants.Ports;
import org.TexasTorque.TexasTorque2013.subsystem.manipulator.Shooter;
import org.TexasTorque.TorqueLib.sensor.TorqueCounter;
import org.TexasTorque.TorqueLib.sensor.TorqueEncoder;
import org.TexasTorque.TorqueLib.sensor.TorquePotentiometer;

public class SensorInput
{
    private static SensorInput instance;
    private Watchdog watchdog;
    //----- Encoder -----
    private TorqueCounter leftDriveCounter;
    private TorqueCounter rightDriveCounter;
    private TorqueCounter frontShooterCounter;
    private TorqueCounter rearShooterCounter;
    private TorqueEncoder elevatorEncoder;
    
    private TorqueEncoder tiltEncoder;
    //----- Analog -----
    private AnalogChannel pressureSensor;
    private AnalogChannel gyroChannel;
    private Gyro gyro;
    private TorquePotentiometer tiltPotentiometer;

    public SensorInput()
    {
        watchdog = Watchdog.getInstance();
        //----- Encoders/Counters -----
        leftDriveCounter = new TorqueCounter(Ports.SIDECAR_TWO, Ports.LEFT_DRIVE_ENCODER_A_PORT);
        rightDriveCounter = new TorqueCounter(Ports.SIDECAR_ONE, Ports.RIGHT_DRIVE_ENCODER_A_PORT);
        frontShooterCounter = new TorqueCounter(Ports.SIDECAR_TWO, Ports.FRONT_SHOOTER_COUNTER_PORT);
        rearShooterCounter = new TorqueCounter(Ports.SIDECAR_TWO, Ports.REAR_SHOOTER_COUNTER_PORT);
        elevatorEncoder = new TorqueEncoder(Ports.SIDECAR_TWO, Ports.ELEVATOR_ENCODER_A_PORT, Ports.SIDECAR_TWO, Ports.ELEVATOR_ENCODER_B_PORT, true);
        
        tiltEncoder = new TorqueEncoder(Ports.SIDECAR_TWO, Ports.TILT_ENCODER_A_PORT, Ports.SIDECAR_TWO, Ports.TILT_ENCODER_B_PORT, true);
        //----- Gyro -----
        gyroChannel = new AnalogChannel(Ports.GYRO_PORT);
        gyroChannel.setAccumulatorDeadband(Constants.GYRO_ACCUMULATOR_DEADBAND);
        gyro = new Gyro(gyroChannel);
        gyro.reset();
        gyro.setSensitivity(Constants.GYRO_SENSITIVITY);
        //----- Misc -----
        pressureSensor = new AnalogChannel(Ports.PRESSURE_SENSOR_PORT);
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
        // 1 foot = 958 clicks
        leftDriveCounter.setOptions(20, 1062, false);
        rightDriveCounter.setOptions(20, 1062, false);
        frontShooterCounter.setOptions(10, 100, true);
        rearShooterCounter.setOptions(10, 100, true);
        elevatorEncoder.setOptions(10, 250, false);
        tiltEncoder.setOptions(20, 250, false);
        leftDriveCounter.start();
        rightDriveCounter.start();        
        frontShooterCounter.start();
        rearShooterCounter.start();
        elevatorEncoder.start();
        tiltEncoder.start();
    }
    
    public synchronized void resetEncoders()
    {
        leftDriveCounter.resetCounter();
        rightDriveCounter.resetCounter();
        frontShooterCounter.resetCounter();
        rearShooterCounter.resetCounter();
        elevatorEncoder.resetEncoder();
        tiltEncoder.resetEncoder();
    }
    
    public synchronized void resetGyro()
    {
        gyro.reset();
        gyro.setSensitivity(Constants.GYRO_SENSITIVITY);
    }
    
    public synchronized int getLeftDriveEncoder()
    {
        return leftDriveCounter.get();
    }
    
    public synchronized int getRightDriveEncoder()
    {
        return rightDriveCounter.get();
    }
    
    public synchronized double getLeftDriveEncoderRate()
    {
        return leftDriveCounter.getRate();
    }
    
    public synchronized double getRightDriveEncoderRate()
    {
        return rightDriveCounter.getRate();
    }
    
    public synchronized double getFrontShooterRate()
    {
        return Shooter.convertToRPM(frontShooterCounter.getRate());
    }
    
    public synchronized double getRearShooterRate()
    {
        return Shooter.convertToRPM(rearShooterCounter.getRate());
    }
    
    public synchronized int getElevatorEncoder()
    {
        return elevatorEncoder.get();
    }
    
    public synchronized double getElevatorEncoderVelocity()
    {
        return elevatorEncoder.getRate();
    }
    
    public synchronized double getElevatorAcceleration()
    {
        return elevatorEncoder.getAcceleration();
    }
    
    public synchronized double getGyroAngle()
    {
        return limitGyroAngle(-gyro.getAngle() * 2);
    }
    
    public synchronized double limitGyroAngle(double angle)
    {
        while(angle >= 360.0)
        {
            watchdog.feed();
            angle -= 360.0;
        }
        while(angle < 0.0)
        {
            watchdog.feed();
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
    
    public synchronized double getTiltAngle()
    {
        return getTiltPotentiometer() * 90;
    }
    
    private synchronized double getTiltPotentiometer()
    {
        return tiltPotentiometer.get();
    }
    
    public synchronized double getTiltVoltage()
    {
        return tiltPotentiometer.getRaw();
    }
    
    public synchronized double getTiltPosition()
    {
        return convertToDegrees(tiltEncoder.get());
    }
    
    public synchronized double getTiltVelocity()
    {
        return convertToDegrees(tiltEncoder.getRate());
    }
    
    private synchronized double convertToDegrees(double clicks)
    {
        return clicks / 62.5;
    }
    
}
