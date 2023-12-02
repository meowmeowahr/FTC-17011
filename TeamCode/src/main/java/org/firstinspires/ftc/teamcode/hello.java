package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

@TeleOp (name = "Main2024CompCode" ,group = "Linear Opmode")

//@Disabled
public class hello extends LinearOpMode {
    //Dashboard demo variables
    public static double ORBITAL_FREQUENCY = 0.05;
    public static double SPIN_FREQUENCY = 0.25;
    public static double ORBITAL_RADIUS = 50;
    public static double SIDE_LENGTH = 10;
    public static double SPEED_MULTIPLIER = 0.95;

    public static double INTAKE_OPEN_POS = 0.4;
    public static double INTAKE_CLOSE_POS = 0.6;

    public static int
            ARM_DRIVE = 0;

    private IMU imu = null;      // Control/Expansion Hub IMU

    public static int ARM_SCORE = -650;
    public static int ARM_FRONTSCORE = -145;
    public static double ARM_POWER = 0.2;

    //Motor demo variables
    private DcMotorEx frontLeftDrive = null;
    private DcMotorEx frontRightDrive = null;
    private DcMotorEx backLeftDrive = null;
    private DcMotorEx backRightDrive = null;

    private DcMotorEx climberMotorLeft = null;
    private DcMotorEx climberMotorRight = null;

    private Servo shooter = null;

    private Servo intake = null;

    private DcMotorEx arm = null;
    private CRServo climberHookLeft;
    private CRServo climberHookRight;



    public void initializeMotors()
    {
        frontLeftDrive = hardwareMap.get(DcMotorEx.class, "FL");
        frontRightDrive = hardwareMap.get(DcMotorEx.class, "FR");
        backLeftDrive = hardwareMap.get(DcMotorEx.class, "BL");
        backRightDrive = hardwareMap.get(DcMotorEx.class, "BR");
        climberMotorLeft = hardwareMap.get(DcMotorEx.class, "climberLeft");
        climberMotorRight = hardwareMap.get(DcMotorEx.class, "climberRight");
        arm = hardwareMap.get(DcMotorEx.class, "Arm");
        intake = hardwareMap.get(Servo.class, "intake");
        shooter = hardwareMap.get(Servo.class, "shooter");
        climberHookLeft = hardwareMap.get(CRServo.class, "climberHookLeft");
        climberHookRight = hardwareMap.get(CRServo.class, "climberHookRight");

        frontRightDrive.setDirection(DcMotorSimple.Direction.REVERSE);
        backRightDrive.setDirection(DcMotorSimple.Direction.REVERSE);

        backLeftDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontLeftDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        climberMotorLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        climberMotorRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        frontLeftDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRightDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRightDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeftDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        climberMotorLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        climberMotorLeft.setTargetPosition(0);
        climberMotorLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        climberMotorLeft.setPower(1);

        climberMotorRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        climberMotorRight.setTargetPosition(0);
        climberMotorRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        climberMotorRight.setPower(1);

        arm.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        arm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        arm.setPower(ARM_POWER);
        arm.setTargetPositionTolerance(1);
        arm.setTargetPosition(ARM_DRIVE);
        arm.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        RevHubOrientationOnRobot.LogoFacingDirection logoDirection = RevHubOrientationOnRobot.LogoFacingDirection.UP;
        RevHubOrientationOnRobot.UsbFacingDirection  usbDirection  = RevHubOrientationOnRobot.UsbFacingDirection.FORWARD;
        RevHubOrientationOnRobot orientationOnRobot = new RevHubOrientationOnRobot(logoDirection, usbDirection);

        // Now initialize the IMU with this mounting orientation
        // This sample expects the IMU to be in a REV Hub and named "imu".
        imu = hardwareMap.get(IMU.class, "imu");
        imu.initialize(new IMU.Parameters(orientationOnRobot));

//        arm.setPIDFCoefficients(DcMotor.RunMode.RUN_TO_POSITION, new PIDFCoefficients(1.5, 0.0, 0.0, 0.05));


    }

    private static void rotatePoints(double[] xPoints, double[] yPoints, double angle) {
        for (int i = 0; i < xPoints.length; i++) {
            double x = xPoints[i];
            double y = yPoints[i];
            xPoints[i] = x * Math.cos(angle) - y * Math.sin(angle);
            yPoints[i] = x * Math.sin(angle) + y * Math.cos(angle);
        }
    }


    public void dashboardDemo(){
        double time = getRuntime();

        double bx = ORBITAL_RADIUS * Math.cos(2 * Math.PI * ORBITAL_FREQUENCY * time);
        double by = ORBITAL_RADIUS * Math.sin(2 * Math.PI * ORBITAL_FREQUENCY * time);
        double l = SIDE_LENGTH / 2;

        double[] bxPoints = { l, -l, -l, l };
        double[] byPoints = { l, l, -l, -l };
        rotatePoints(bxPoints, byPoints, 2 * Math.PI * SPIN_FREQUENCY * time);
        for (int i = 0; i < 4; i++) {
            bxPoints[i] += bx;
            byPoints[i] += by;
        }


        sleep(20);
    }

    private void joystickMecanumDrive() {

        double y = -gamepad1.left_stick_y; // Remember, Y stick value is reversed
        double x = gamepad1.left_stick_x * 1.1; // Counteract imperfect strafing
        double rx = gamepad1.right_stick_x;


        // Denominator is the largest motor power (absolute value) or 1
        // This ensures all the powers maintain the same ratio,
        // but only if at least one is out of the range [-1, 1]
        double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
        double frontLeftPower = -(y + x + rx) / denominator; //A-RC has negative before parentheses and B-RC has no negative
        double backLeftPower = -(y - x + rx) / denominator;
        double frontRightPower = -(y - x - rx) / denominator;
        double backRightPower = -(y + x - rx) / denominator;

        frontLeftDrive.setPower(frontLeftPower * SPEED_MULTIPLIER);
        backLeftDrive.setPower(backLeftPower * SPEED_MULTIPLIER);
        frontRightDrive.setPower(frontRightPower * SPEED_MULTIPLIER);
        backRightDrive.setPower(backRightPower * SPEED_MULTIPLIER);


    }

    private void climber() {
        if (gamepad2.dpad_up) {
            climberMotorLeft.setTargetPosition(climberMotorLeft.getTargetPosition() + 20);
            climberMotorRight.setTargetPosition(climberMotorLeft.getTargetPosition() + 20);
        }
        else if (gamepad2.dpad_down){
            climberMotorLeft.setTargetPosition(climberMotorLeft.getTargetPosition() - 20);
            climberMotorRight.setTargetPosition(climberMotorLeft.getTargetPosition() - 20);
        }
    }


    public void armFunctions() {
        if (gamepad1.right_bumper){
            intake.setPosition(INTAKE_CLOSE_POS);
        }
        else if (gamepad1.left_bumper){
            intake.setPosition(INTAKE_OPEN_POS);
        }

        int encoderCount = arm.getCurrentPosition();

        if (gamepad2.a) {
            arm.setTargetPosition(ARM_DRIVE);
        } else if (gamepad2.right_trigger > 0.3){
            arm.setTargetPosition(encoderCount + Math.round(40 * gamepad2.right_trigger));
        }
        else if (gamepad2.left_trigger > 0.3) {
            arm.setTargetPosition(encoderCount - Math.round(40 * gamepad2.left_trigger));
        }/*else if (gamepad2.b) {
            arm.setTargetPosition(ARM_FRONTSCORE);
        }
        else if (gamepad2.x) {
            arm.setTargetPosition(ARM_SCORE);
        }*/

    }

    private void fieldCentricDrive() {
        double y = gamepad1.left_stick_y; // Remember, Y stick value is reversed
        double x = -gamepad1.left_stick_x * 1.1; // Counteract imperfect strafing
        double rx = -gamepad1.right_stick_x;
        double lf = -gamepad2.left_stick_y;

        // Calculate the current angle of the robot (yaw) relative to the field.
        double robotAngle = -Math.toRadians(imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES)); // Modify this to get the actual robot angle.

        // Calculate the field-centric components of movement.
        double fieldX = x * Math.cos(robotAngle) - y * Math.sin(robotAngle);
        double fieldY = x * Math.sin(robotAngle) + y * Math.cos(robotAngle);

        // Denominator is the largest motor power (absolute value) or 1
        // This ensures all the powers maintain the same ratio,
        // but only if at least one is out of the range [-1, 1]
        double denominator = Math.max(Math.abs(fieldY) + Math.abs(fieldX) + Math.abs(rx), 1);
        double frontLeftPower = (fieldY + fieldX + rx) / denominator;
        double backLeftPower = (fieldY - fieldX + rx) / denominator;
        double frontRightPower = (fieldY - fieldX - rx) / denominator;
        double backRightPower = (fieldY + fieldX - rx) / denominator;

        // Set the motor powers.
        frontLeftDrive.setPower(frontLeftPower);
        backLeftDrive.setPower(backLeftPower);
        frontRightDrive.setPower(frontRightPower);
        backRightDrive.setPower(backRightPower);
    }




    public void runOpMode()  {
        initializeMotors();
        intake.scaleRange(0,1);
        intake.setDirection(Servo.Direction.FORWARD);
        intake.setPosition(0.5);

        shooter.scaleRange(0,1);
        waitForStart();
        imu.resetYaw();
        while (opModeIsActive()) {
            telemetry.addData("arm pos", arm.getCurrentPosition());
            telemetry.addData("arm pwr", arm.getCurrent(CurrentUnit.MILLIAMPS));
            climber();
            if (gamepad2.dpad_up){
                shooter.setPosition(0.9);
            }

//            telemetry.addData("climber pos", climberMotor.getCurrentPosition());
            updateTelemetry(telemetry);

            //joystickTankDrive();
            joystickMecanumDrive();
            armFunctions();
            //fieldCentricDrive();
        }
    }
}