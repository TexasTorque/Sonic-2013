package org.TexasTorque.TexasTorque2013.constants;

public class Ports
{
    //----- Misc -----
    public final static int SIDECAR_ONE = 1;
    public final static int SIDECAR_TWO = 2;
    
    //----- Controllers -----
    public final static int DRIVE_CONTROLLER_PORT = 1;
    public final static int OPERATOR_CONTROLLER_PORT = 2;
    
    //----- Motors -----
        //----- Sidecar 1 -----
        public final static int FRONT_RIGHT_MOTOR_PORT = 2;
        public final static int REAR_RIGHT_MOTOR_PORT = 3;
        public final static int ELEVATOR_MOTOR_PORT_LEFT = 4;
        public final static int INTAKE_MOTOR_PORT = 5;
        
        //----- Sidecar 2 -----
        public final static int REAR_SHOOTER_MOTOR_A_PORT = 1;
        public final static int REAR_SHOOTER_MOTOR_B_PORT = 2;
        public final static int FRONT_SHOOTER_MOTOR_A_PORT = 3;
        public final static int FRONT_SHOOTER_MOTOR_B_PORT = 4;
        public final static int FRONT_LEFT_MOTOR_PORT = 5;
        public final static int REAR_LEFT_MOTOR_PORT = 6;
        public final static int ELEVATOR_MOTOR_PORT_RIGHT = 7;
        public final static int SHOOTER_TILT_MOTOR_PORT = 8;
    
    //----- Solenoids -----
    public final static int PASSIVE_CLIMBER_A_PORT = 1;
    public final static int PASSIVE_CLIMBER_B_PORT = 2;
    public final static int FRISBEE_LIFTER_SOLENOID_A_PORT = 3;
    public final static int FRISBEE_LIFTER_SOLENOID_B_PORT = 4;
    public final static int LOADER_SOLENOID_PORT = 5;
    public final static int DRIVE_SHIFTER_PORT = 6;
    public final static int INTAKE_DROPDOWN_PORT = 7;
    public final static int LEVEL_TWO_CLIMBER_PORT = 8;
    
    //----- Digital Inputs -----
        //----- Sidecar 1 -----
        public final static int PRESSURE_SWITCH_PORT = 13;
        public final static int RIGHT_DRIVE_ENCODER_A_PORT = 1;
        public final static int RIGHT_DRIVE_ENCODER_B_PORT = 2;
        public final static int MIDDLE_SHOOTER_COUNTER_PORT = 3;
        //----- Sidecar 2 -----
        public final static int LEFT_DRIVE_ENCODER_A_PORT = 1;
        public final static int LEFT_DRIVE_ENCODER_B_PORT = 2;
        public final static int ELEVATOR_ENCODER_A_PORT = 3;
        public final static int ELEVATOR_ENCODER_B_PORT = 4;
        public final static int FRONT_SHOOTER_COUNTER_PORT = 5;
        public final static int REAR_SHOOTER_COUNTER_PORT = 6;
        public final static int LIGHTS_A_PORT = 7;
        public final static int LIGHTS_B_PORT = 8;
        public final static int LIGHTS_C_PORT = 9;
        public final static int LIGHTS_D_PORT = 10;
        public final static int TILT_ENCODER_A_PORT = 13;
        public final static int TILT_ENCODER_B_PORT = 14;
    
    //----- Analog Inputs -----
    public final static int GYRO_PORT = 1;
    public final static int PRESSURE_SENSOR_PORT = 2;
    public final static int TILT_POTENTIOMETER_PORT = 3;
    
    //----- Relays -----
        //----- Sidecar 1 -----
        public final static int COMPRESSOR_RELAY_PORT = 1;
        public final static int PTO_RELAY_PORT = 2;
        //----- Sidecar 2 -----
    
}
