package org.TexasTorque.TexasTorque2013.subsystem.manipulator;

import com.sun.squawk.io.j2me.ParameterParser;
import org.TexasTorque.TexasTorque2013.constants.Constants;
import org.TexasTorque.TexasTorque2013.io.DriverInput;
import org.TexasTorque.TexasTorque2013.io.RobotOutput;
import org.TexasTorque.TexasTorque2013.io.SensorInput;
import org.TexasTorque.TorqueLib.util.Parameters;

public class Manipulator
{
    
    private static Manipulator instance;
    private RobotOutput robotOutput;
    private SensorInput sensorInput;
    private DriverInput driverInput;
    private Parameters params;
    private Shooter shooter;
    private Elevator elevator;
    private Intake intake;
    private Magazine magazine;
    
    public static Manipulator getInstance()
    {
        return (instance == null) ? instance = new Manipulator() : instance;
    }
    
    public Manipulator()
    {
        robotOutput = RobotOutput.getInstance();
        sensorInput = SensorInput.getInstance();
        driverInput = DriverInput.getInstance();
        shooter = Shooter.getInstance();
        elevator = Elevator.getInstance();
        intake = Intake.getInstance();
        magazine = Magazine.getInstance();
    }
    
    public void run()
    {
        if(driverInput.reverseIntake())
        {
            calcReverseIntake();
        }
        shooter.run(false);
        elevator.run(false);
        intake.run(false);
        magazine.run(false);
    }
    
    public void calcReverseIntake()
    {
        shooter.setTiltAngle(Constants.TILT_PARALLEL_POSITION);
        shooter.setShooterRates(Constants.SHOOTER_STOPPED_RATE, Constants.SHOOTER_STOPPED_RATE);
        if(shooter.isParallel())
        {
            elevator.setDesiredPosition(Constants.ELEVATOR_BOTTOM_POSITION);
            if(elevator.elevatorAtBottom())
            {
                intake.setIntakeSpeed(params.getAsDouble("I_OuttakeSpeed", -1.0));
            }
        }
    }
    
}
