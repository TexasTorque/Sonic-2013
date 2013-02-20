package org.TexasTorque.TexasTorque2013.subsystem.manipulator;

import org.TexasTorque.TexasTorque2013.constants.Constants;
import org.TexasTorque.TexasTorque2013.io.DriverInput;
import org.TexasTorque.TexasTorque2013.io.RobotOutput;
import org.TexasTorque.TexasTorque2013.io.SensorInput;
import org.TexasTorque.TorqueLib.util.Parameters;
import org.TexasTorque.TorqueLib.util.TorqueLogging;

public class Intake
{
    
    private static Intake instance;
    private RobotOutput robotOutput;
    private DriverInput driverInput;
    private SensorInput sensorInput;
    private TorqueLogging logging;
    private Parameters params;
    
    private double intakeMotorSpeed;
    private boolean intakeState;
    
    public static double intakeSpeed;
    public static double outtakeSpeed;
    
    public static Intake getInstance()
    {
        return (instance == null) ? instance = new Intake() : instance;
    }
    
    public Intake()
    {
        robotOutput = RobotOutput.getInstance();
        driverInput = DriverInput.getInstance();
        sensorInput = SensorInput.getInstance();
        logging = TorqueLogging.getInstance();
        params = Parameters.getInstance();
        
        intakeMotorSpeed = Constants.MOTOR_STOPPED;
        intakeState = Constants.INTAKE_UP_POSITION;
    }
    
    public void run()
    {
        robotOutput.setIntakeDropdown(intakeState);
        
        robotOutput.setIntakeMotor(intakeMotorSpeed);
    }
    
    public synchronized void logData()
    {
        logging.logValue("IntakeMotorSpeed", intakeMotorSpeed);
        logging.logValue("IntakeDropdownPosition", intakeState);
    }
    
    public synchronized String getKeyNames()
    {
        String names = "IntakeMotorSpeed,IntakeDropdownPosition";
        
        return names;
    }
    
    public synchronized void loadParameters()
    {
        intakeSpeed = params.getAsDouble("I_IntakeSpeed", 1.0);
        outtakeSpeed = params.getAsDouble("I_OuttakeSpeed", -1.0);
    }
    
    public synchronized void setIntakeSpeed(double speed)
    {
        intakeMotorSpeed = speed;
    }
    
    public synchronized void setIntakeDropdown(boolean dropdown)
    {
        intakeState = (dropdown) ? Constants.INTAKE_DOWN_POSITION : Constants.INTAKE_UP_POSITION;
    }
}
