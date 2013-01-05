package org.TexasTorque.TexasTorque2013.io;

import edu.wpi.first.wpilibj.Encoder;
import org.TexasTorque.TexasTorque2013.constants.Ports;

public class SensorInput
{
    private static SensorInput instance;
    private Encoder leftDriveEncoder;
    private Encoder rightDriveEncoder;
    
    public SensorInput()
    {
        leftDriveEncoder = new Encoder(Ports.SIDECAR_ONE, Ports.LEFT_DRIVE_ENCODER_A_PORT, Ports.SIDECAR_ONE, Ports.LEFT_DRIVE_ENCODER_B_PORT, true);
        rightDriveEncoder = new Encoder(Ports.SIDECAR_ONE, Ports.RIGHT_DRIVE_ENCODER_A_PORT, Ports.SIDECAR_ONE, Ports.RIGHT_DRIVE_ENCODER_B_PORT, false);
        leftDriveEncoder.start();
        rightDriveEncoder.start();
    }
    
    public synchronized static SensorInput getInstance()
    {
        return (instance == null) ? instance = new SensorInput() : instance;
    }
    
    public synchronized void resetEncoders()
    {
        leftDriveEncoder.reset();
        rightDriveEncoder.reset();
    }
    
    public synchronized int getLeftDriveEncoder()
    {
        return leftDriveEncoder.get();
    }
    
    public synchronized int getRightDriveEncoder()
    {
        return rightDriveEncoder.get();
    }
}
