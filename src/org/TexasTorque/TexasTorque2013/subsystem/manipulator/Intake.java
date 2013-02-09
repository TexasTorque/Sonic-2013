package org.TexasTorque.TexasTorque2013.subsystem.manipulator;

import javax.microedition.io.Connection;
import org.TexasTorque.TexasTorque2013.constants.Constants;
import org.TexasTorque.TexasTorque2013.io.DriverInput;
import org.TexasTorque.TexasTorque2013.io.RobotOutput;
import org.TexasTorque.TexasTorque2013.io.SensorInput;
import org.TexasTorque.TorqueLib.util.Parameters;

public class Intake
{
    
    private static Intake instance;
    private RobotOutput robotOutput;
    private DriverInput driverInput;
    private SensorInput sensorInput;
    private Parameters params;
    private double intakeMotorSpeed;
    
    public static Intake getInstance()
    {
        return (instance == null) ? instance = new Intake() : instance;
    }
    
    public Intake()
    {
        robotOutput = RobotOutput.getInstance();
        driverInput = DriverInput.getInstance();
        sensorInput = SensorInput.getInstance();
        params = Parameters.getInstance();
        intakeMotorSpeed = Constants.MOTOR_STOPPED;
    }
    
    public void run()
    {
        robotOutput.setIntakeMotor(intakeMotorSpeed);
    }
    
    public synchronized void setIntakeSpeed(double speed)
    {
        intakeMotorSpeed = speed;
    }
}
