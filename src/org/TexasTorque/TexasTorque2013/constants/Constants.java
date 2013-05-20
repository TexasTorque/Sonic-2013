package org.TexasTorque.TexasTorque2013.constants;

public class Constants
{
    //----- Controller -----
    public final static double SPEED_AXIS_DEADBAND = 0.07;
    public final static double TURN_AXIS_DEADBAND = 0.07;
    
    public final static boolean DEFAULT_FIRST_CONTROLLER_TYPE = true;
    public final static boolean DEFAULT_SECOND_CONTROLLER_TYPE = false;
    
    //----- Light States -----
    public final static int WHITE_SOLID = 0;
    public final static int BLUE_SOLID = 1;
    public final static int RED_SOLID = 2;
    public final static int YELLOW_RED_ALLIANCE = 4;
    public final static int YELLOW_BLUE_ALLIANCE = 3;
    public final static int PARTY_MODE = 5;
    public final static int TRACKING_RED_ALLIANCE = 6;
    public final static int TRACKING_BLUE_ALLIANCE = 7;
    public final static int LOCKED_RED_ALLIANCE = 8;
    public final static int LOCKED_BLUE_ALLIANCE = 9;
    
    //----- Autonomous -----
    public final static int DO_NOTHING_AUTO = 0;
    public final static int MIDDLE_THREE_AUTO = 1;
    public final static int SIDE_THREE_AUTO = 2;
    public final static int MIDDLE_SEVEN_AUTO = 3;
    public final static int RIGHT_SEVEN_AUTO = 4;
    public final static int LEFT_SEVEN_AUTO = 5;
    public final static int RIGHT_THREE_DRIVE_AUTO = 6;
    
    //----- Drivebase -----
    public final static double DEFAULT_HIGH_SENSITIVITY = 0.7;
    public final static boolean HIGH_GEAR = true;
    public final static boolean LOW_GEAR = false;
    
    //----- Gyro -----
    public static double GYRO_SENSITIVITY = 0.014;
    
    //----- Tilt -----
    public final static double DEFAULT_STANDARD_TILT_POSITION = 5.0;
    public final static double POTENTIOMETER_LOW_VOLTAGE = 0.34;
    public final static double POTENTIOMETER_HIGH_VOLTAGE = 6.578;
    public final static boolean GATE_EXTENDED = false;
    public final static boolean GATE_RETRACTED = true;
    
    //----- Shooter -----
    public final static double DEFAULT_FRONT_SHOOTER_RATE = 4800.0;
    public final static double DEFAULT_REAR_SHOOTER_RATE = 4800.0;
    public final static double FULL_FIELD_FRONT_RATE = 4800.0;
    public final static double FULL_FIELD_REAR_RATE = 4800.0;
    public final static double SHOOTER_STOPPED_RATE = 0.0;
    
    //----- Elevator -----
    public final static int DEFAULT_ELEVATOR_BOTTOM_POSITION = 0;
    public final static int DEFAULT_ELEVATOR_TOP_POSITION = 750;
    public final static int DEFAULT_ELEVATOR_FEED_POSITION = 400;
    public final static int DEFAULT_ELEVATOR_MIDDLE_POSTION = 150;
    
    //----- Magazine -----
    public final static boolean MAGAZINE_STORED = false;
    public final static boolean MAGAZINE_LOADING = true;
    
    public final static int MAGAZINE_READY_STATE = 0;
    public final static int MAGAZINE_LOADING_STATE = 1;
    public final static int MAGAZINE_SHOOTING_STATE = 2;
    public final static int MAGAZINE_RESETTING_STATE = 3;
    
    public final static double MAGAZINE_DELTA_TIME = 1.0;
    
    //----- Climber -----
    public final static boolean PASSIVE_HANG_UP = true;
    public final static boolean PASSIVE_HANG_DOWN = false;
    
    //----- Misc -----
    public final static int CYCLES_PER_LOG = 10;
    public final static double MOTOR_STOPPED = 0.0;
    public final static int SHOOTER_FILTER_SIZE = 10;
    public final static double PRESSURE_THRESHOLD = 2.0;
    public final static int RED_ALLIANCE = 0;
    public final static int BLUE_ALLIANCE = 1;
    
}
