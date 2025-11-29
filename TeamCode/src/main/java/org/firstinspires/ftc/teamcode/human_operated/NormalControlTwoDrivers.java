package org.firstinspires.ftc.teamcode.human_operated;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.base.HumanOperated;


@TeleOp(name = "Normal Two Drivers", group = "TeleOp")
public class NormalControlTwoDrivers extends HumanOperated {

    /* > The following code overrides the "loop" function
     * > The "loop" function is constantly called as the TeleOp is running
     */
    @Override
    public void loop() {
        /* All functions you write in HumanOperated should be called here since this class
        extends HumanOperated */

        useDefaultMovementControls();
        useDefaultLauncherControls(player.player2);
        SetMotorPower();

    }
}