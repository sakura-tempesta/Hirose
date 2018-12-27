/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team6909.robot;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.PWMTalonSRX;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;



public class Robot extends IterativeRobot {

	private XboxController driver;
	private PWMTalonSRX m_leftArm, m_rightArm;
	private Spark m_rightFront, m_rightRear
				  ,m_leftFront,m_leftRear;
	private SpeedControllerGroup m_left, m_right;
	private  DrivePIDController DrivePID;

	private Encoder e_driveleft;
	private DriveEncoderGroup e_drive;
	private LiftEncoder e_liftEncoder;
	private DriveGyro Gyro;


	private double armvalue = 0;
	private boolean is_leftTriggerOn, is_rightTriggerOn;
	private double X_driver, Y_driver;



	private double NoReact = 0.1;

	private int disabledPeriodicCounter = 0;
	private double nowValue_driveEncoder, firstValue_driveEncoder;
	private boolean is_driveEncoderOver, advancingOnemeterInit;

	private double NoReactProcessing(double value) {

		value *= Math.abs(value) > NoReact ? 1 : 0 ;

		return value;
	}; //不感帯未満なら0


	Robot() {
		driver = new XboxController(0);

		m_leftArm = new PWMTalonSRX(5);
		m_rightArm = new PWMTalonSRX(6);

		m_leftFront = new Spark(0);
		m_leftRear = new Spark(1);
		m_rightFront = new Spark(2);
		m_rightRear = new Spark(3);
		m_left = new SpeedControllerGroup(m_leftFront, m_leftRear);
		m_right = new SpeedControllerGroup(m_rightFront, m_rightRear);
		DrivePID = new DrivePIDController(m_left, m_right);

		e_driveleft = new Encoder(8, 9);
		e_drive = new DriveEncoderGroup(e_driveleft);
		e_drive.setDistancePerPulse(7.7 * Math.PI / 10.71);
		e_liftEncoder = new LiftEncoder(4,5);
		e_liftEncoder.setDistancePerPulse(2);
		Gyro = new DriveGyro();

		e_drive.reset();
		e_liftEncoder.reset();

		DrivePID = new DrivePIDController(m_left, m_right,e_drive, Gyro);
	}


	public void teleopPeriodic() {



		is_leftTriggerOn = 0.2 < driver.getTriggerAxis(Hand.kLeft);
		is_rightTriggerOn =  0.2 < driver.getTriggerAxis(Hand.kRight);

		if( is_leftTriggerOn && is_rightTriggerOn ) {
			armvalue = 0;
		}else {
			armvalue = is_leftTriggerOn ? 1.0 : 0;
			armvalue = is_rightTriggerOn ? -1.0 : 0;
		}

		X_driver = NoReactProcessing(driver.getY(Hand.kLeft));
		X_driver = NoReactProcessing(driver.getX(Hand.kRight));

		m_leftArm.set(armvalue);
		m_rightArm.set(-armvalue);


		if(driver.getAButton()) {

			if(!advancingOnemeterInit) {
				firstValue_driveEncoder = e_drive.getDistance();
				advancingOnemeterInit = true;
			}//初期化

			nowValue_driveEncoder = e_drive.getDistance() - firstValue_driveEncoder;
			is_driveEncoderOver	= nowValue_driveEncoder > 1000;

			if(is_driveEncoderOver) {
				advancingOnemeterInit = false;
				DrivePID.arcadeDrive(0, 0);
			}else {
				DrivePID.arcadeDrive(1.0, 0);
			}

		}else {
			advancingOnemeterInit= false;

			DrivePID.arcadeDrive(Y_driver, X_driver);

		}


	}

	public void disabledPeriodic() {

		disabledPeriodicCounter++;

		SmartDashboard.putNumber("driverの左のY", driver.getY(Hand.kLeft));
		SmartDashboard.putBoolean("driverのAが押されてるか", driver.getAButton());
		SmartDashboard.putNumber("呼ばれた回数", disabledPeriodicCounter);

	}

}


class LiftEncoder extends Encoder{


	public LiftEncoder(int channelA, int channelB) {
		super(channelA, channelB);
	}

	public double getDistance(){
		double distace = super.getDistance();

		//ここで処理

		return distace;
	}
}




