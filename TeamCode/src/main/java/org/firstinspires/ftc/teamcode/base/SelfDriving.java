package org.firstinspires.ftc.teamcode.base;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
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
public abstract class SelfDriving extends LinearOpMode {
    protected HardwareManager hardwareManager;

    protected final double WHEEL_CIRCUMFERENCE = Math.PI * 0.098; // M
    protected final int COUNTS_PER_MOTOR_REVOLUTION = 900;
    protected final double COUNTS_PER_METER =
            COUNTS_PER_MOTOR_REVOLUTION / WHEEL_CIRCUMFERENCE;
    protected ElapsedTime elapsedTime;
    //------------------------------------------------------------------------------------------------
    // Config
    //------------------------------------------------------------------------------------------------
    protected final double MOVEMENT_POWER = 0.25;
    protected final double TURN_POWER  = 0.3;
    //------------------------------------------------------------------------------------------------
    // Movement
    //------------------------------------------------------------------------------------------------
    protected void move(double metersDistance) {
        if (!opModeIsActive())
            return;

        hardwareManager.resetWheelCounts();
        hardwareManager.doToAllWheels((wheel) -> wheel.setPower(MOVEMENT_POWER));

        double totalCounts = COUNTS_PER_METER * metersDistance;
        while (opModeIsActive() && hardwareManager.getAverageWheelCounts() <= totalCounts) {
            idle();
        }

        hardwareManager.doToAllWheels((wheel) -> wheel.setPower(0));
    }

    protected void moveForSeconds(double milliseconds){
        elapsedTime.startTime(); // Start timer...

        hardwareManager.doToAllWheels((wheel) -> wheel.setPower(MOVEMENT_POWER)); // Run all wheels at set power...

        while (opModeIsActive() && milliseconds >= elapsedTime.milliseconds()){ // While the timer is less than time requested
            idle();
        }
        // Once while has stopped
        hardwareManager.doToAllWheels((wheel) -> wheel.setPower(0)); // Set all wheels to 0 power
        elapsedTime.reset(); // reset timer
    }

    //------------------------------------------------------------------------------------------------
    // Rotation
    //------------------------------------------------------------------------------------------------
    protected void rotate(double degreeAngle) {
        if (!opModeIsActive())
            return;

        hardwareManager.imu.resetYaw();
        double initialAngle = hardwareManager.getCurrentDegreeHeading();

        double motorOffset = degreeAngle > 0 ? 1 : -1;
        double leftPower = TURN_POWER * motorOffset;
        double rightPower = TURN_POWER * -motorOffset;

        hardwareManager.frontLeftWheel.setPower(leftPower);
        hardwareManager.frontRightWheel.setPower(rightPower);
        hardwareManager.backLeftWheel.setPower(leftPower);
        hardwareManager.backRightWheel.setPower(rightPower);

        while(opModeIsActive() && hasReachedDesiredAngle(initialAngle, degreeAngle)) {
            idle();
        }

        hardwareManager.doToAllWheels((wheel) -> wheel.setPower(0));
    }

    protected boolean hasReachedDesiredAngle(double initialAngle, double turnAngle) {
        double targetAngle = initialAngle - turnAngle;
        double currentAngle = hardwareManager.getCurrentDegreeHeading();

        return turnAngle > 0
                ? currentAngle > targetAngle
                : currentAngle < targetAngle;
    }
    //------------------------------------------------------------------------------------------------
    // Inheritance
    //------------------------------------------------------------------------------------------------

    @Override
    public void runOpMode() {
        hardwareManager = new HardwareManager(hardwareMap);
        waitForStart();
        runAutonomous();
    }

    protected abstract void runAutonomous();
}