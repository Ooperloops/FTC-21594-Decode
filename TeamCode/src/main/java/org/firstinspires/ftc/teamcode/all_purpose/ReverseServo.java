package org.firstinspires.ftc.teamcode.all_purpose;

import com.qualcomm.robotcore.hardware.Servo;

public class ReverseServo {

    public Servo servo;

    public ReverseServo(Servo device){
        servo = device;
    }

    public void setPosition(double position){
        servo.setPosition(1.0 - position);
    }
}
