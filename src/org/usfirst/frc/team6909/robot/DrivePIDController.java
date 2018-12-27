package org.usfirst.frc.team6909.robot;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

enum PIDMode{Straight, Rotate,Default};

public class DrivePIDController extends DifferentialDrive{

	public DrivePIDController(SpeedController leftMotor, SpeedController rightMotor) {
		super(leftMotor, rightMotor);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	public DrivePIDController(SpeedController leftMotor, SpeedController rightMotor, DriveEncoderGroup e_drive, DriveGyro g_drive) {
		this(leftMotor, rightMotor);

		DriveStraightController = new PIDController(Const.kp, Const.ki, Const.kd,e_drive, new DrivePIDOutput(PIDMode.Straight));
		DriveRotateController = new PIDController(Const.kp, Const.ki, Const.kd,g_drive, new DrivePIDOutput(PIDMode.Rotate));
	}



	protected static double straightOutput;
	protected static double rotateOutput;

	private PIDController DriveStraightController, DriveRotateController;

	public void setSetpoint(double setpoint) {
		DriveStraightController.setSetpoint(setpoint);
		DriveRotateController.setSetpoint(0);

		arcadeDrive(straightOutput, rotateOutput);
	}

}


class DriveEncoderGroup implements PIDSource{

	Encoder e_driveA, e_driveB;

	DriveEncoderGroup(Encoder e_driveA){
		this(e_driveA, e_driveA);
	}

	DriveEncoderGroup(Encoder e_driveA,Encoder e_driveB){
		this.e_driveA = e_driveA;
		this.e_driveB = e_driveB;
	}

	public void setDistancePerPulse(double distancePerPulse) {
		e_driveA.setDistancePerPulse(distancePerPulse);
		e_driveB.setDistancePerPulse(distancePerPulse);
	}

	public double getDistance() {
		return (e_driveA.getDistance() + e_driveB.getDistance()) /2;
	}

	public void reset() {
		e_driveA.reset();
		e_driveB.reset();
	}




	@Override
	public void setPIDSourceType(PIDSourceType pidSource) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public PIDSourceType getPIDSourceType() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public double pidGet() {
		// TODO 自動生成されたメソッド・スタブ
		return getDistance();	}
}

class DriveGyro extends ADXRS450_Gyro {
	@Override
	public double pidGet() {

		return this.getAngle();
	}

}

class DrivePIDOutput implements PIDOutput{


	DrivePIDOutput(PIDMode m_pid){
		this.m_pid = m_pid;
	}


	PIDMode m_pid = PIDMode.Default;


	public void pidWrite(double output) {

		switch(m_pid) {
		case Straight:
			DrivePIDController.straightOutput = output;
			break;

		case Rotate:
			DrivePIDController.rotateOutput = output;
			break;

		case Default:
		default:
		}

	}
}
