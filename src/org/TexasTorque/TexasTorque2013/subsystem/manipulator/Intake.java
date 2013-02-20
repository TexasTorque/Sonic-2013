package org.TexasTorque.TexasTorque2013.subsystem.manipulator;

import org.TexasTorque.TexasTorque2013.TorqueSubsystem;
import org.TexasTorque.TexasTorque2013.constants.Constants;

public class Intake extends TorqueSubsystem
{
    private double intakeMotorSpeed;
    private boolean intakeState;
    
    public static double intakeSpeed;
    public static double outtakeSpeed;
    
    public static TorqueSubsystem getInstance()
    {
        return (instance == null) ? instance = new Intake() : instance;
    }
    
    private Intake()
    {
        super();
        
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
