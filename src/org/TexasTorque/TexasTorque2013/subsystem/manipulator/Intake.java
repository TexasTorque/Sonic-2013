package org.TexasTorque.TexasTorque2013.subsystem.manipulator;

import org.TexasTorque.TexasTorque2013.TorqueSubsystem;
import org.TexasTorque.TexasTorque2013.constants.Constants;

public class Intake extends TorqueSubsystem
{
    private static Intake instance;
    
    private double intakeMotorSpeed;
    
    public static double intakeSpeed;
    public static double outtakeSpeed;
    
    public static Intake getInstance()
    {
        return (instance == null) ? instance = new Intake() : instance;
    }
    
    private Intake()
    {
        super();
        
        intakeMotorSpeed = Constants.MOTOR_STOPPED;
    }
    
    public void run()
    {   
        robotOutput.setIntakeMotor(intakeMotorSpeed);
    }
    
    public void setIntakeSpeed(double speed)
    {
        intakeMotorSpeed = speed;
    }
    
    public String getKeyNames()
    {
        String names = "IntakeMotorSpeed,";
        
        return names;
    }
    
     public String logData()
    {
        String data = intakeMotorSpeed + ",";
        
        return data;
    }
    
    public void loadParameters()
    {
        intakeSpeed = params.getAsDouble("I_IntakeSpeed", 1.0);
        outtakeSpeed = params.getAsDouble("I_OuttakeSpeed", -1.0);
    }
}
