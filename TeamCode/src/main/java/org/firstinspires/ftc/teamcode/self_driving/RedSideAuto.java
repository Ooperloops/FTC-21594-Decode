package org.firstinspires.ftc.teamcode.self_driving;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.base.SelfDriving;

@Autonomous(name = "Blue Side Auto", group = "Autonomous")
public class RedSideAuto extends SelfDriving {

    @Override
    protected void runAutonomous() {
       move(0.3);
       rotate(45);
       move(2.5);
       launch(3);
    }
}