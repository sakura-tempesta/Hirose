/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team6909.robot;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.PWMTalonSRX;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;



public class Robot extends IterativeRobot {

	private XboxController driver;
	private PWMTalonSRX m_leftArm, m_rightArm;
	private Spark m_rightFront, m_rightRear
				  ,m_leftFront,m_leftRear;
	private SpeedControllerGroup m_left, m_right;
	private DifferentialDrive drive;

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
	}

	public void teleopPeriodic() {

		double value = 0;

		boolean is_leftTriggerOn = 0.2 < driver.getTriggerAxis(Hand.kLeft),
				 is_rightTriggerOn =  0.2 < driver.getTriggerAxis(Hand.kRight);

		if( is_leftTriggerOn && is_rightTriggerOn ) {
			value = 0;
		}else {
			value = is_leftTriggerOn ? 1.0 : 0;
			value = is_rightTriggerOn ? -1.0 : 0;
		}

		m_leftArm.set(value);
		m_rightArm.set(-value);

		//不感帯
		if( Math.abs(driver.getY(Hand.kLeft)) > 0.2 || Math.abs(driver.getX(Hand.kRight)) >0.2 ) {
			drive.arcadeDrive(driver.getY(Hand.kLeft), driver.getX(Hand.kRight) );
		} else {
			drive.arcadeDrive(0.0, 0.0);
		}
	}

}
