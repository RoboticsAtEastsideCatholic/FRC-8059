/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

//import com.revrobotics.ColorSensorV3;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.DigitalOutput;
//import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PWMVictorSPX;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
//import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;

/**
 * This is a demo program showing the use of the DifferentialDrive class. Runs
 * the motors with arcade steering.
 */
public class Robot extends TimedRobot {
  private int test = 0;
  private final PWMVictorSPX rightMotor = new PWMVictorSPX(0);
  private final PWMVictorSPX leftMotor = new PWMVictorSPX(1);

  private DifferentialDrive robotDrive = null;// = new DifferentialDrive(rightMotor, leftMotor);
  private final Joystick stick = new Joystick(0);

  // LED Digital Output Communitation
  private final DigitalOutput leftDriveForward = new DigitalOutput(0);
  private final DigitalOutput leftDriveBackward = new DigitalOutput(1);
  private final DigitalOutput rightDriveForward = new DigitalOutput(2);
  private final DigitalOutput rightDriveBackward = new DigitalOutput(3);

  //private final ColorSensorV3 colorSensor = new ColorSensorV3(I2C.Port.kOnboard);

  private final CameraServer camera = CameraServer.getInstance();

  private NetworkTableEntry colorEntry;
  private NetworkTableEntry autoEntry;
  private SendableChooser<String> colorChoice = new SendableChooser<String>();

  @Override
  public void robotInit() {
    camera.startAutomaticCapture();
    ShuffleboardTab judas = Shuffleboard.getTab("Judas View");
   
    colorChoice.addOption("red", "red");
    colorChoice.addOption("blue", "blue");
    colorChoice.addOption("green", "green");
    colorChoice.addOption("yellow", "yellow");
    autoEntry = judas.add("Autonomous Mode", false).withWidget(BuiltInWidgets.kToggleButton).getEntry();
    colorEntry = judas.add("Color", false).withWidget(BuiltInWidgets.kBooleanBox).getEntry();
    judas.add("Target Color", colorChoice).withWidget(BuiltInWidgets.kComboBoxChooser);
    //This is for the dropdown menu where the colorChoice is being a dropdown for each option and add option adds for each one.
    
  }
  
  @Override
  public void autonomousInit() {
    test = 0;

  }
  @Override
  public void autonomousPeriodic() {
    if(test == 0 ){
    //  rightMotor.set(.25);
    System.out.println(colorEntry.getName()+"; "+colorEntry.getBoolean(false));  
    colorEntry.setBoolean(true);
    System.out.println(colorEntry.getName()+"; "+colorEntry.getBoolean(false)+autoEntry.getBoolean(false));
    }
    else if(test == 1000){
    rightMotor.set(0);
    colorEntry.setBoolean(false);

    }
    
    else if(test == 1500){
    //  rightMotor.set(.75);
    }
    else if(test == 2000){
   //   rightMotor.set(0);
    }

    if (test % 100 == 0) {
   // System.out.println(test);
    }
    test++;
  }

  @Override
  public void disabledInit() {
    if(robotDrive != null){
      robotDrive.setSafetyEnabled(false);
      // robotDrive.setExpiration(1);
    }
  }

  @Override
  public void teleopInit() {
    super.teleopInit();
   // NetworkTableEntry red = Shuffleboard.getTab("LiveWindow").add("red", true).withWidget("Text View").getEntry();

    robotDrive = new DifferentialDrive(rightMotor, leftMotor);
  }


  @Override
  public void teleopPeriodic() {
    System.out.println("Teleop");
    // Drive with arcade drive.
    // That means that the Y axis drives forward
    // and backward, and the X turns left and right.
    robotDrive.arcadeDrive(stick.getY(), stick.getX());

    final double rightMotorPower = rightMotor.getSpeed();
    final double leftMotorPower = leftMotor.getSpeed();

    System.out.println("Left Motor Power: " + rightMotorPower);
    System.out.println("Right Motor Power: " + leftMotorPower);

    // System.out.println("Red: " + colorSensor.getRed());
    // System.out.println("Green: " + colorSensor.getGreen());
    // System.out.println("Blue: " + colorSensor.getBlue());

    if(leftMotorPower > 0){ //Left Motor Forward
      System.out.println("Left Forward");
      leftDriveForward.set(true);
      leftDriveBackward.set(false);
    } else if (leftMotorPower < 0){ //Left Motor Backward
      System.out.println("Left Backward");
      leftDriveForward.set(false);
      leftDriveBackward.set(true);
    } else { //Left Motor Stop
      leftDriveForward.set(false);
      leftDriveBackward.set(false);
    }

    if(rightMotorPower < 0){ //Right Motor Forward
      System.out.println("Right Forward");
      rightDriveForward.set(true);
      rightDriveBackward.set(false);
    } else if (rightMotorPower > 0){ //Right Motor Backward
      System.out.println("Right Backward");
      rightDriveForward.set(false);
      rightDriveBackward.set(true);
    } else { //Right Motor Stop
      rightDriveForward.set(false);
      rightDriveBackward.set(false);
    }
  }
}
