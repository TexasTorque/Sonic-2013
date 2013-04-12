package org.TexasTorque.TexasTorque2013.io;

import edu.wpi.first.wpilibj.AnalogChannel;
import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.Watchdog;
import org.TexasTorque.TexasTorque2013.constants.Constants;
import org.TexasTorque.TexasTorque2013.constants.Ports;
import org.TexasTorque.TorqueLib.component.TorqueCounterNew;
import org.TexasTorque.TorqueLib.component.TorqueEncoder;
import org.TexasTorque.TorqueLib.component.TorquePotentiometer;
import org.TexasTorque.TorqueLib.util.TorqueUtil;

public class SensorInput
{
    private static SensorInput instance;
    private Watchdog watchdog;
    //----- Encoder -----
    private TorqueEncoder leftDriveEncoder;
    private TorqueEncoder rightDriveEncoder;
    private TorqueCounterNew frontShooterCounter;
    private TorqueCounterNew middleShooterCounter;
    private TorqueCounterNew rearShooterCounter;
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
        frontShooterCounter = new TorqueCounterNew(Ports.SIDECAR_TWO, Ports.FRONT_SHOOTER_COUNTER_PORT);
        middleShooterCounter = new TorqueCounterNew(Ports.SIDECAR_TWO, Ports.MIDDLE_SHOOOTER_COUNTER_PORT);
        rearShooterCounter = new TorqueCounterNew(Ports.SIDECAR_TWO, Ports.REAR_SHOOTER_COUNTER_PORT);
        elevatorEncoder = new TorqueEncoder(Ports.SIDECAR_TWO, Ports.ELEVATOR_ENCODER_A_PORT, Ports.SIDECAR_TWO, Ports.ELEVATOR_ENCODER_B_PORT, true);
        //----- Gyro -----
        gyroChannel = new AnalogChannel(Ports.GYRO_PORT);
        gyro = new Gyro(gyroChannel);
        gyro.reset();
        gyro.setSensitivity(Constants.GYRO_SENSITIVITY);
        //----- Misc -----
        pressureSensor = new AnalogChannel(Ports.PRESSURE_SWITCH_PORT);
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
        frontShooterCounter.setFilterSize(Constants.SHOOTER_FILTER_SIZE);
        middleShooterCounter.setFilterSize(Constants.SHOOTER_FILTER_SIZE);
        rearShooterCounter.setFilterSize(Constants.SHOOTER_FILTER_SIZE);
        
        // 1 foot = 958 clicks
        leftDriveEncoder.start();
        rightDriveEncoder.start();        
        frontShooterCounter.start();
        middleShooterCounter.start();
        rearShooterCounter.start();
        elevatorEncoder.start();
    }
    
    public void resetEncoders()
    {
        leftDriveEncoder.reset();
        rightDriveEncoder.reset();
        frontShooterCounter.reset();
        middleShooterCounter.reset();
        rearShooterCounter.reset();
        elevatorEncoder.reset();
    }
    
    public void calcEncoders()
    {
        leftDriveEncoder.calc();
        rightDriveEncoder.calc();
        frontShooterCounter.calc();
        middleShooterCounter.calc();
        rearShooterCounter.calc();
        elevatorEncoder.calc();
    }
    
    public void resetGyro()
    {
        gyro.reset();
        gyro.setSensitivity(Constants.GYRO_SENSITIVITY);
    }
    
    public double getLeftDriveEncoder()
    {
        return (leftDriveEncoder.get() / 958.0) * 12; 
    }
    
    public double getRightDriveEncoder()
    {
        return (rightDriveEncoder.get() / 958.0) * 12;
    }
    
    public double getLeftDriveEncoderRate()
    {
        return (leftDriveEncoder.getRate() / 958.0) * 12;
    }
    
    public double getRightDriveEncoderRate()
    {
        return (rightDriveEncoder.getRate() / 958.0) * 12;
    }
    
    public double getLeftDriveEncoderAcceleration()
    {
        return (leftDriveEncoder.getAcceleration() / 958.0) * 12;
    }
    
    public double getRightDriveEncoderAcceleration()
    {
        return (rightDriveEncoder.getAcceleration() / 958.0) * 12;
    }
    
    public double getFrontShooterRate()
    {
        return TorqueUtil.convertToRMP(frontShooterCounter.getRate(), 100);
    }
    
    public double getMiddleShooterRate()
    {
        return TorqueUtil.convertToRMP(middleShooterCounter.getRate(), 100);
    }
    
    public double getRearShooterRate()
    {
        return TorqueUtil.convertToRMP(rearShooterCounter.getRate(), 100);
    }
    
    public int getElevatorEncoder()
    {
        return elevatorEncoder.get();
    }
    
    public double getElevatorEncoderVelocity()
    {
        return elevatorEncoder.getRate();
    }
    
    public double getElevatorAcceleration()
    {
        return elevatorEncoder.getAcceleration();
    }
    
    public double getPSI()
    {
        return pressureSensor.getVoltage();
    }
    
    public double getGyroAngle()
    {
        return limitGyroAngle(-gyro.getAngle() * 2);
    }
    
    public double limitGyroAngle(double angle)
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
    
    public double getTiltAngle()
    {
        return getTiltPotentiometer() * 90;
    }
    
    private double getTiltPotentiometer()
    {
        return tiltPotentiometer.get();
    }
    
    public double getTiltVoltage()
    {
        return tiltPotentiometer.getRaw();
    }
}
