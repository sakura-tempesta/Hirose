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
import edu.wpi.first.wpilibj.XboxController;



public class Robot extends IterativeRobot {
	private XboxController driver;
	private PWMTalonSRX m_leftArm,m_rightArm;

	Robot(){
		driver=new XboxController(0);

		m_leftArm=new PWMTalonSRX(5);
		m_rightArm=new PWMTalonSRX(6);
	}

	public void teleopPeriodic() {
		double value =driver.getY(Hand.kLeft);
		if(value>-0.2&&value<0.2) value=0;

		m_leftArm.set(value);
		m_rightArm.set(-value);
	}

}
