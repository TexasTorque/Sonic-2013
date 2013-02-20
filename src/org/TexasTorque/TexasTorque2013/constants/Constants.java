package org.TexasTorque.TexasTorque2013.constants;

public class Constants
{
    //----- Controller -----
    public final static double SPEED_AXIS_DEADBAND = 0.07;
    public final static double TURN_AXIS_DEADBAND = 0.07;
    
    public final static boolean DEFAULT_FIRST_CONTROLLER_TYPE = true;
    public final static boolean DEFAULT_SECOND_CONTROLLER_TYPE = false;
    
    //----- Autonomous -----
    public final static int DO_NOTHING_AUTO = 0;
    public final static int DIAGNOSTIC_AUTO = 1;
    public final static int FRONT_SHOOT_AUTO = 2;
    public final static int FRONT_DRIVE_AUTO = 3;
    public final static int REAR_SHOOT_AUTO = 4;
    public final static int REAR_DRIVE_FORWARD_AUTO = 5;
    public final static int REAR_DRIVE_FORWARD_DOUBLE_AUTO = 6;
    public final static int REAR_DRIVE_REVERSE_AUTO = 7;
    public final static int REAR_DRIVE_REVERSE_SHOOT_AUTO = 8;
    
    //----- Drivebase -----
    public final static double MAX_DIAMETER = 1.5;
    public final static double ROBOT_WIDTH = 1.0;
    public final static double DEFAULT_LOW_SENSITIVITY = 1.25;
    public final static double DEFAULT_HIGH_SENSITIVITY = 0.7;
    
    //----- Gyro -----
    public final static int GYRO_ACCUMULATOR_DEADBAND = 75;
    public final static double GYRO_SENSITIVITY = 0.0136;
    
    //----- Shooter -----
    public final static double DEFAULT_STANDARD_TILT_POSITION = 5.0;
    public final static double POTENTIOMETER_LOW_VOLTAGE = 3.24;
    public final static double POTENTIOMETER_HIGH_VOLTAGE = 2.016;
    public final static double DEFAULT_FRONT_SHOOTER_RATE = 3000.0;
    public final static double DEFAULT_REAR_SHOOTER_RATE = 2000.0;
    public final static double SHOOTER_STOPPED_RATE = 0.0;
    public final static int TILT_LOCKED_STATE = 0;
    public final static int TILT_MOVING_STATE = 1;
    
    //----- Intake -----
    public final static boolean INTAKE_DOWN_POSITION = true;
    public final static boolean INTAKE_UP_POSITION = false;
    
    //----- Elevator -----
    public final static int DEFAULT_ELEVATOR_BOTTOM_POSITION = 0;
    public final static int DEFAULT_ELEVATOR_TOP_POSITION = 750;
    public final static int ELEVATOR_LOCKED_STATE = 0;
    public final static int ELEVATOR_MOVING_STATE = 1;
    
    //----- Magazine -----
    public final static boolean MAGAZINE_STORED = false;
    public final static boolean MAGAZINE_LOADING = true;
    
    public final static int MAGAZINE_READY_STATE = 0;
    public final static int MAGAZINE_LOADING_STATE = 1;
    public final static int MAGAZINE_SHOOTING_STATE = 2;
    public final static int MAGAZINE_RESETTING_STATE = 3;
    
    public final static double MAGAZINE_DELTA_TIME = 1.0;
    
    //----- Misc -----
    public final static int TORQUE_LOGGING_LOOP_TIME = 20;
    public final static double MOTOR_STOPPED = 0.0;
    
}
