
/**
 * Copyright (c) 2001-2017 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/epl-v10.html
 */
package sample;


import robocode.HitRobotEvent;

import robocode.Robot;
import robocode.ScannedRobotEvent;
import robocode.WinEvent;
import static robocode.util.Utils.normalRelativeAngleDegrees;
import robocode.util.*;

import java.awt.*;
import java.awt.geom.*;



/**
 * Matilda - Robot built with survivability in mind
 * 
 * Lock on to nearest robot, dodge and shoot.
 *
 * @author Neal (original)
 * @contributor Thomas (Movement code)
 */
public class Matilda extends Robot {
	int count = 0; // Keeps track of turn count
	double gunTurnAmt; // gun Turn value
	String trRobotName; // the Name of the Robot currently tracking
	/**
	 * run:  Tracker's main run function
	 */
	public void run() {
		// Set colors
		setBodyColor(new Color(200, 128, 50));
		setGunColor(new Color(100, 50, 20));
		setRadarColor(new Color(50, 50, 70));
		setScanColor(Color.red);
		setBulletColor(Color.green);

		// Gun vars - Design defaults (Neal)
		trRobotName = null; // Forget Tracking for now.. 
		setAdjustGunForRobotTurn(true); // No change in Gun direction on change in direction
		gunTurnAmt = 180; // Set gun Turn to max of 45 degrees.
		
		// Loop forever
		while (true) {
			// turn the Gun + turn counter
			turnGunRight(gunTurnAmt);
			count++;
			if (count % 10 == 0) {
				trRobotName = null;
				movement();	//Sets off movement every 10 turns.
			}
			
  		}
	}	
	/**
	 * onScannedRobot:  triggers on any time the radar catches an enemy robot
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
		// Priority Queue - Targetting, Wall Evasion, Target Reacquisition
		double bulletPower = Math.min(3.0,getEnergy());
		double myX = getX();
		double myY = getY();
		double absoluteBearing = dTR(getHeading()) + e.getBearingRadians();
		double enemyX = getX() + e.getDistance() * Math.sin(absoluteBearing);
		double enemyY = getY() + e.getDistance() * Math.cos(absoluteBearing);
		double enemyHeading = e.getHeadingRadians();
		double enemyVelocity = e.getVelocity();
	//	double gunTurnAmt = 360; // Set gun Turn one whole rotation
		//	double previousEnergy = 100;
		
			//The Enemy has fired a bullet
		
		
		if (e.isSentryRobot() == true){
	//	turnRadarRight(999);
		//turnGunRight(999);
		}
		else{
	
		 
		 
		double deltaTime = 0;
		double battleFieldHeight = getBattleFieldHeight(), 
		       battleFieldWidth = getBattleFieldWidth();
		double predictedX = enemyX, predictedY = enemyY;
			while((++deltaTime) * (18.0 - 3.0 * bulletPower) < 
				  Point2D.Double.distance(myX, myY, predictedX, predictedY)){		
				predictedX += Math.sin(enemyHeading) * enemyVelocity;	
				predictedY += Math.cos(enemyHeading) * enemyVelocity;
				if(	predictedX < 18.0
					|| predictedY < 18.0
					|| predictedX > battleFieldWidth - 18.0
					|| predictedY > battleFieldHeight - 18.0){
					predictedX = Math.min(Math.max(18.0, predictedX), 
								battleFieldWidth - 18.0);	
					predictedY = Math.min(Math.max(18.0, predictedY), 
								battleFieldHeight - 18.0);
					break;
				}

					//angle theta for atan(delta)
					double theta = Utils.normalAbsoluteAngle(Math.atan2(
						predictedX - getX(), predictedY - getY()));

//				setTurnRadarRightRadians(Utils.normalRelativeAngle(absoluteBearing - e.getRadarHeadingRadians()));
//				setTurnGunRightRadians(Utils.normalRelativeAngle(theta - getGunHeadingRadians()));
				
				turnRadarRight(rTD(Utils.normalRelativeAngle(absoluteBearing - dTR(getRadarHeading()) ) ));
				turnGunRight(rTD(Utils.normalRelativeAngle(theta - dTR(getGunHeading()) )));	
					fire(bulletPower);
			}
		}
	}
	/**
	 * onHitRobot:  Set him as our new target
	 */
	public void onHitRobot(HitRobotEvent e) {
		// Only print if he's not already our target.
		if (trRobotName != null && !trRobotName.equals(e.getName())) {
			out.println("Tracking " + e.getName() + " due to collision");
		}
		// Set the target
		trRobotName = e.getName();
		gunTurnAmt = normalRelativeAngleDegrees(e.getBearing() + (getHeading() - getRadarHeading()));
		
	}
	
	public void movement()
	{
	//all encapsulated into the one method, small movements so that it doesn't take so long completing it, top to bottom.
		//	double heading = getHeading();//since this was originally junior, which had a heading variable constantly set, we can set it with 
			int movementArc = 50; //movementArc is the movement of each straight line, keeping it short will keep throw trackers off.
			turnRight(30);
			ahead(20 + movementArc);
			turnRight(40);
			ahead(30 + movementArc);
			turnLeft(20);
			back(40 + movementArc);		
	}


//Degrees to radians
public double dTR(double deg)
{
	double rads = Math.toRadians(deg);
	return rads;
}

//Radians to degrees
public double rTD(double rads)
{
	double deg = Math.toDegrees(rads);
	return deg;
}



	/**
	 * Do the worm!!!!
	 */
	public void onWin(WinEvent e) {
		for (int i = 0; i < 50; i++) {
			turnRight(30);
			ahead(5);
			turnLeft(30);
			ahead(5);
		}
	}
}
