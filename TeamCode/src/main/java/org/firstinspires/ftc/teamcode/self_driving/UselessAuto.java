package org.firstinspires.ftc.teamcode.self_driving;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.base.SelfDriving;

@Autonomous(name = "67 DUMMY AUTO", group = "Autonomous")
public class UselessAuto extends SelfDriving {

    @Override
    protected void runAutonomous() {
       moveForSeconds(1.5);
       rotate(50);
    }
}