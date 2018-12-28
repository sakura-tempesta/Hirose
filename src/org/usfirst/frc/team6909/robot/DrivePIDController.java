package org.usfirst.frc.team6909.robot;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;


public class DrivePIDController extends DifferentialDrive{

	enum PIDMode{Straight, Rotate,Default};

	private double straightOutput, rotateOutput;

	private DriveEncoderGroup e_drive;
	private ADXRS450_Gyro g_drive;

	private PIDController driveStraightController, driveRotateController;


	public DrivePIDController(SpeedController leftMotor, SpeedController rightMotor, DriveEncoderGroup e_drive, ADXRS450_Gyro g_drive) {
		super(leftMotor, rightMotor);
		this.e_drive = e_drive;
		this.g_drive = g_drive;

		driveStraightController = new PIDController(Const.kp_straight, Const.ki_straight, Const.kd_straight, e_drive, new DrivePIDOutput(PIDMode.Straight));
		driveRotateController = new PIDController(Const.kp_rotate, Const.ki_rotate, Const.kd_rotate, g_drive, new DrivePIDOutput(PIDMode.Rotate));
	}

	/* 本来のsetSetPointでは現在位置からではなくEncoder系を作動させた時の初期位置からの距離、角度を指定する必要がある。
	 * PIDControllerのcalculate()をみるとinputにpidGet()をいれ、setSetpoint()でsetされた目標値との偏差をとっている。
	 * つまり、何も考えずに入れるとEncoder系を作動させた時の初期位置からのsetpointになり、思うように動作しない。
	 * 例えば、1ｍ前進した後に2ｍ後進しようとしてsetSetpoint(-2000)などとすると実際には3ｍ後進してしまう。
	 * だから、setSetpoint(getDistance()(=1000) + setpoint(=-2000))とする。
	 * しかし、少し扱いづらいので"Relative"(=相対)にして見えないところで処理する。
	 */
	public void setRelativeStraightSetpoint(double setpoint) {
		driveStraightController.setSetpoint(e_drive.getDistance() + setpoint);
		driveRotateController.setSetpoint(g_drive.getAngle());

	}

	public void PIDenable() {
		if(!driveStraightController.isEnabled()) {
			driveStraightController.enable();
		}
		if(!driveRotateController.isEnabled()) {
			driveRotateController.enable();
		}
	}

	public void PIDdisable() {
		if(driveStraightController.isEnabled()) {
			driveStraightController.disable();
		}
		if(driveRotateController.isEnabled()) {
			driveRotateController.disable();
		}
	}

	public boolean is_PIDEnabled() {
		return driveStraightController.isEnabled() && driveRotateController.isEnabled();
	}

	class DrivePIDOutput implements PIDOutput{

		PIDMode m_pid = PIDMode.Default;

		DrivePIDOutput(PIDMode m_pid){
			this.m_pid = m_pid;
		}


		public void pidWrite(double output) {

			switch(m_pid) {
			case Straight:
				straightOutput = output;
				break;

			case Rotate:
				rotateOutput = output;
				break;

			case Default:
			default:
			}

			arcadeDrive(straightOutput,rotateOutput);

		}
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

	}//他のところでもEncoderGroupは使うので外に置く。DriveTrainにはアクセスしないので問題はない。



