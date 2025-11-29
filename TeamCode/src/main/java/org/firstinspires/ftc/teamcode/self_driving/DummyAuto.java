package org.firstinspires.ftc.teamcode.self_driving;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.base.SelfDriving;

@TeleOp(name = "Normal Two Drivers", group = "TeleOp")
public class DummyAuto extends SelfDriving {


    @Override
    protected void runAutonomous() {
        moveForSeconds( 1000);
        rotate(360);
    }
}