package org.firstinspires.ftc.teamcode.base;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.all_purpose.HardwareManager;

/**
 * Base class for all human-operated scripts, a.k.a TeleOp.
 * Any inherited classes manipulates the given protected power
 * values of each motor and servos, and then calls `setHardwarePower`
 * which ensures that the values obeys the UPPER and LOWER limits,
 * before sending them to each hardware binding classes.
 *
 * (USER_INPUT) -> (Class extends HumanOperated) -> (Base Class HumanOperated) -> (Hardware Manager)
 *      |                   |                             |                              |
 *     \/                   |                             |                              `-> Magic Class that converts Java value to actual
 *   GamePad1 or            |                             |                                  voltage to be used by DcMotors and Servos.
 *   GamePad2               |                             |
 *                          |                             `> Ensures the desired power setting is within min and max
 *                          |                                 before sending it to the actual hardware binding class.
 *                         \/
 *                    Interprets the human control into whatever
 *                    control schema we decide. E.g Tank Control
 *                    Split Control, etc..
 */
public abstract class HumanOperated extends OpMode {
    protected HardwareManager hardwareManager;
    protected boolean slowToggle = false;
    public enum player{
        player1,
        player2
    }
    //------------------------------------------------------------------------------------------------
    // Wheel power values
    //------------------------------------------------------------------------------------------------
    // For Dc Motors, power is a decimal value between -1 to 1, 0 = stop, 1,-1 = full power in different directions
    protected double frontLeftWheelP = 0;
    protected double frontRightWheelP = 0;
    protected double backLeftWheelP = 0;
    protected double backRightWheelP = 0;

    //------------------------------------------------------------------------------------------------
    // Config
    //------------------------------------------------------------------------------------------------

    protected double MOTOR_UPPER_POWER_LIMIT = 1;
    protected double MOTOR_LOWER_POWER_LIMIT = -1;
    protected final double SERVO_UPPER_POWER_LIMIT = 0.8; // VEX Servos Actual Limitation
    protected final double SERVO_LOWER_POWER_LIMIT = -0.8; // VEX Servos Actual Limitation
    protected double MOTOR_SHRINK_MULTIPLIER = 0.3;
    //------------------------------------------------------------------------------------------------
    // Launcher
    //------------------------------------------------------------------------------------------------
    protected boolean launcherSpeedPauseOn = false;
    protected double pausedLauncherSpeed = 0;

    //------------------------------------------------------------------------------------------------
    // Defaults
    //------------------------------------------------------------------------------------------------
    protected void useDefaultMovementControls() {
        // Allow for forward / backward movement command
        // to be receive from left and right joystick.


        /** [HOW THIS WORKS]
         * DcMotors need a power input between (-1.00 to 1.00)
         * This can be done by calling the .setPower(); method on a DcMotor variable
         * -----
         * Each of the joysticks' have two axes (x and y)
         * if a the left joystick of a gamepad is moved up then gamepad#.left_stick_y is positive
         * if a the left joystick of a gamepad is moved down then gamepad#.left_stick_y is negative
         * etc.
         * -----
         * Forward and Backward drive is done by setting the power of all the wheels as the value
         * of the y-axis value of the gamepad's left joystick
         *
         */

        // Movement values:
        double drive = (gamepad1.left_stick_y != 0)
                ? -gamepad1.left_stick_y
                : -gamepad1.right_stick_y;

        double strafe = -gamepad1.left_stick_x;
        double rotate = gamepad1.right_stick_x;

        /* Movement values are summed into the power for the wheels */
        frontLeftWheelP  = drive - strafe - rotate;
        frontRightWheelP = drive - strafe + rotate;
        backLeftWheelP   = drive + strafe - rotate;
        backRightWheelP  = drive + strafe + rotate;
    }
    protected void useDefaultLauncherControls(player driver){
        Gamepad currentDriver = (driver == player.player1) ? gamepad1 : gamepad2;

        // Holy mother of god what in the world is this GENERATIONAL IF
        if(currentDriver.a){
            hardwareManager.wheelLauncher.setPower(1);
        } else if (currentDriver.b) {
            hardwareManager.wheelLauncher.setPower(-0.5);
        }else if (currentDriver.x){

            // Pause and keep the current speed of the motor active from the left joystick
            launcherSpeedPauseOn = true;
            pausedLauncherSpeed = currentDriver.left_stick_y;
            telemetry.addData("!! PAUSE STATUS !! -> ", "ON");
            telemetry.addData("!! LAUNCHER SPEED (%) !! -> ", pausedLauncherSpeed * -100);
        }else if (currentDriver.y){

            // Unpause and let the joystick dictate the speed again
            launcherSpeedPauseOn = false;
            telemetry.addData("!! PAUSE STATUS !! -> ", "OFF");
        } else {
            double activeSpeed = launcherSpeedPauseOn ? pausedLauncherSpeed : currentDriver.left_stick_y;
            hardwareManager.wheelLauncher.setPower(-activeSpeed);

        }



        //Stopper
        if(currentDriver.right_bumper){
            hardwareManager.stopper.setPosition(0.25);
        } else if (currentDriver.right_trigger > 0){
            hardwareManager.stopper.setPosition(0);
        }

        //Flinger
        if(currentDriver.left_bumper){
            hardwareManager.flinger.setPosition(0.05); // Up Position = 0.05
        } else if (currentDriver.left_trigger > 0){
            hardwareManager.flinger.setPosition(0.38); // Down Position = 0.38
        }

        telemetry.update();
    }

    protected void zeroAllServos(){
        hardwareManager.flinger.setPosition(0.38);
        hardwareManager.stopper.setPosition(0);
    }

    //------------------------------------------------------------------------------------------------
    // Inheritance
    //------------------------------------------------------------------------------------------------

    @Override
    public void init() {
        hardwareManager = new HardwareManager(hardwareMap);
        zeroAllServos();
    }

    protected double limitMotorPower(double input){
        // Limits the DcMotor output power within a certain interval
        return Range.clip(input, MOTOR_LOWER_POWER_LIMIT, MOTOR_UPPER_POWER_LIMIT);
    }

    protected double shrinkMotorPower(double input){
        // Limits the DcMotor output power within a certain interval. for changing joystick sensitivity
        return MOTOR_SHRINK_MULTIPLIER * input;
    }

    protected double limitServoPower(double input) {
        // Limits the Servo output power with a certain interval
        return Range.clip(input, SERVO_LOWER_POWER_LIMIT, SERVO_UPPER_POWER_LIMIT);
    }

    protected void SetMotorPower(){
        // Sets motor power
        hardwareManager.backLeftWheel.setPower(gamepad1.right_trigger > 0 ? shrinkMotorPower(backLeftWheelP) : backLeftWheelP);
        hardwareManager.backRightWheel.setPower(gamepad1.right_trigger > 0 ? shrinkMotorPower(backRightWheelP) : backRightWheelP);
        hardwareManager.frontLeftWheel.setPower(gamepad1.right_trigger > 0 ? shrinkMotorPower(frontLeftWheelP) : frontLeftWheelP);
        hardwareManager.frontRightWheel.setPower(gamepad1.right_trigger > 0 ? shrinkMotorPower(frontRightWheelP) : frontRightWheelP);
    }
}