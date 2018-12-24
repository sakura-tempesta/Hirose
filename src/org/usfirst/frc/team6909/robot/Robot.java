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
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;



public class Robot extends IterativeRobot {

	private XboxController driver;
	private PWMTalonSRX m_leftArm, m_rightArm;
	private Spark m_rightFront, m_rightRear
				  ,m_leftFront,m_leftRear;
	private SpeedControllerGroup m_left, m_right;
	private DifferentialDrive drive;

	private Encoder e_driveEncoder;
	private EncoderOriginal e_liftEncoder;

	double armvalue = 0;
	boolean is_leftTriggerOn, is_rightTriggerOn;
	double X_driver, Y_driver;

	double NoReact = 0.1;
	double disabledPeriodicCounter = 0;

	private double dataProcessing(double value) {

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
		drive = new DifferentialDrive(m_left, m_right);

		e_driveEncoder = new Encoder(8, 9);
		e_driveEncoder.setDistancePerPulse(7.7 * Math.PI / 10.71);
		e_liftEncoder = new EncoderOriginal(4,5);
		e_liftEncoder.setDistancePerPulse(2);

		e_driveEncoder.reset();
		e_driveEncoder.reset();
	}

	public void autonomousPeriodic() {
		boolean is_driveEncoderOver = e_driveEncoder.getDistance() > 1000;
		if(is_driveEncoderOver) {
			e_driveEncoder.reset();
		}

		if(driver.getAButton() && !is_driveEncoderOver) {
			drive.arcadeDrive(1.0, 0);
		}else {
			drive.arcadeDrive(0, 0);
		}
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

		X_driver = dataProcessing(driver.getY(Hand.kLeft));
		X_driver = dataProcessing(driver.getX(Hand.kRight));


		m_leftArm.set(armvalue);
		m_rightArm.set(-armvalue);

		drive.arcadeDrive(Y_driver, X_driver);




	}

	public void disabledPeriodic() {

		disabledPeriodicCounter++;

		SmartDashboard.putNumber("driverの左のY", driver.getY(Hand.kLeft));
		SmartDashboard.putBoolean("driverのAが押されてるか", driver.getAButton());
		SmartDashboard.putNumber("呼ばれた回数", disabledPeriodicCounter);

	}

}


class EncoderOriginal extends Encoder{


	public EncoderOriginal(int channelA, int channelB) {
		super(channelA, channelB);
	}

	public double getDistance(){
		double distace = super.getDistance();

		//ここで処理

		return distace;
	}
}




