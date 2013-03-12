package org.TexasTorque.TexasTorque2013.io;

import edu.wpi.first.wpilibj.AnalogChannel;
import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.Watchdog;
import org.TexasTorque.TexasTorque2013.constants.Constants;
import org.TexasTorque.TexasTorque2013.constants.Ports;
import org.TexasTorque.TexasTorque2013.subsystem.manipulator.Shooter;
import org.TexasTorque.TorqueLib.sensor.TorqueCounter;
import org.TexasTorque.TorqueLib.sensor.TorqueCounterThread;
import org.TexasTorque.TorqueLib.sensor.TorqueEncoder;
import org.TexasTorque.TorqueLib.sensor.TorqueEncoderThread;
import org.TexasTorque.TorqueLib.sensor.TorquePotentiometer;

public class SensorInput
{
    private static SensorInput instance;
    private Watchdog watchdog;
    //----- Encoder -----
    private TorqueEncoder leftDriveEncoder;
    private TorqueEncoder rightDriveEncoder;
    private TorqueCounter frontShooterCounter;
    private TorqueCounter middleShooterCounter;
    private TorqueCounter rearShooterCounter;
    private TorqueEncoder elevatorEncoder;
    //----- Analog -----
    private AnalogChannel pressureSensor;
    private AnalogChannel gyroChannel;
    public Gyro gyro;
    private TorquePotentiometer tiltPotentiometer;

    public SensorInput()
    {
        watchdog = Watchdog.getInstance();
        //----- Encoders/Counters -----
        leftDriveEncoder = new TorqueEncoder(Ports.SIDECAR_TWO, Ports.LEFT_DRIVE_ENCODER_A_PORT, Ports.SIDECAR_TWO, Ports.LEFT_DRIVE_ENCODER_B_PORT, false);
        rightDriveEncoder = new TorqueEncoder(Ports.SIDECAR_ONE, Ports.RIGHT_DRIVE_ENCODER_A_PORT, Ports.SIDECAR_ONE, Ports.RIGHT_DRIVE_ENCODER_B_PORT, false);
        frontShooterCounter = new TorqueCounter(Ports.SIDECAR_TWO, Ports.FRONT_SHOOTER_COUNTER_PORT);
        middleShooterCounter = new TorqueCounter(Ports.SIDECAR_ONE, Ports.MIDDLE_SHOOTER_COUNTER_PORT);
        rearShooterCounter = new TorqueCounter(Ports.SIDECAR_TWO, Ports.REAR_SHOOTER_COUNTER_PORT);
        elevatorEncoder = new TorqueEncoder(Ports.SIDECAR_TWO, Ports.ELEVATOR_ENCODER_A_PORT, Ports.SIDECAR_TWO, Ports.ELEVATOR_ENCODER_B_PORT, true);
        //----- Gyro -----
        gyroChannel = new AnalogChannel(Ports.GYRO_PORT);
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
        leftDriveEncoder.start();
        rightDriveEncoder.start();        
        frontShooterCounter.start();
        middleShooterCounter.start();
        rearShooterCounter.start();
        elevatorEncoder.start();
    }
    
    public synchronized void resetEncoders()
    {
        leftDriveEncoder.reset();
        rightDriveEncoder.reset();
        frontShooterCounter.reset();
        middleShooterCounter.reset();
        rearShooterCounter.reset();
        elevatorEncoder.reset();
    }
    
    public synchronized void calcEncoders()
    {
        leftDriveEncoder.calc();
        rightDriveEncoder.calc();
        frontShooterCounter.calc();
        middleShooterCounter.calc();
        rearShooterCounter.calc();
        elevatorEncoder.calc();
    }
    
    public synchronized void resetGyro()
    {
        gyro.reset();
        gyro.setSensitivity(Constants.GYRO_SENSITIVITY);
    }
    
    public synchronized double getLeftDriveEncoder()
    {
        return (leftDriveEncoder.get() / 958.0) * 12; 
    }
    
    public synchronized double getRightDriveEncoder()
    {
        return (rightDriveEncoder.get() / 958.0) * 12;
    }
    
    public synchronized double getLeftDriveEncoderRate()
    {
        return (leftDriveEncoder.getRate() / 958.0) * 12;
    }
    
    public synchronized double getRightDriveEncoderRate()
    {
        return (rightDriveEncoder.getRate() / 958.0) * 12;
    }
    
    public synchronized double getLeftDriveEncoderAcceleration()
    {
        return (leftDriveEncoder.getAcceleration() / 958.0) * 12;
    }
    
    public synchronized double getRightDriveEncoderAcceleration()
    {
        return (rightDriveEncoder.getAcceleration() / 958.0) * 12;
    }
    
    public synchronized double getFrontShooterRate()
    {
        return Shooter.convertToRPM(frontShooterCounter.getRate());
    }
    
    public synchronized double getMiddleShooterRate()
    {
        return Shooter.convertToRPM(middleShooterCounter.getRate());
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
}
