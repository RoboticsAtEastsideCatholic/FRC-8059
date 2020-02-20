package frc.robot;

//import com.revrobotics.ColorSensorV3;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PWM;
import edu.wpi.first.wpilibj.PWMVictorSPX;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import sun.awt.image.XbmImageDecoder;


/*
            Current Controls:
Driver Controller (Port 0)
    Right Stick: Arcade Drive

Manipulator Controller (Port 1)
    Right Stick X: Intake
    Right Stick Y: Raise and Lower Intake

    Left Stick Y: Raise and Lower CP Spinner
    Right Trigger: Spin CP to the Right
    Left Trigger: Spin CP to the Left

*/


/*
            TODO List
  Verify Teleop Control
  Verify Teleop Direction
  Verify Teleop Sensitivity

  Make Auto

  Cameras

  Auto Switch


*/

public class Robot extends TimedRobot {
  //Autonomous Timer
  private int test = 0;
  private Timer autoTimer;

  //Motor Controllers
  private final PWMVictorSPX rightMotor = new PWMVictorSPX(0);
  private final PWMVictorSPX leftMotor = new PWMVictorSPX(1);

  private final PWMVictorSPX cpSpinMotor = new PWMVictorSPX(2);
  private final PWMVictorSPX cpLiftMotor = new PWMVictorSPX(3);

  private final PWMVictorSPX intakeMotor = new PWMVictorSPX(4);

  //Driving Objects
  private DifferentialDrive robotDrive = null;

  //CP Limit Switches
  private DigitalInput highLimit = new DigitalInput(5);
  private DigitalInput lowLimit = new DigitalInput(6);

  //Controllers
  private final XboxController drivController = new XboxController(0);
  private final XboxController manipController = new XboxController(1);

  // LED Digital Output Communitation
  private final DigitalOutput leftDriveForward = new DigitalOutput(0);
  private final DigitalOutput leftDriveBackward = new DigitalOutput(1);
  private final DigitalOutput rightDriveForward = new DigitalOutput(2);
  private final DigitalOutput rightDriveBackward = new DigitalOutput(3);

  //private final ColorSensorV3 colorSensor = new ColorSensorV3(I2C.Port.kOnboard);

  //Microsoft LifeCams
  private final CameraServer camera = CameraServer.getInstance();

  //Shuffleboard Objects
  private NetworkTableEntry colorEntry;
  private NetworkTableEntry autoEntry;
  private SendableChooser<String> colorChoice = new SendableChooser<String>();

  @Override
  public void robotInit() {
    camera.startAutomaticCapture();

    //Initialize and Set Shuffleboard
    ShuffleboardTab judas = Shuffleboard.getTab("Judas View");
    autoEntry = judas.add("Autonomous Mode", false).withWidget(BuiltInWidgets.kToggleButton).getEntry();

    colorChoice.addOption("red", "red");
    colorChoice.addOption("blue", "blue");
    colorChoice.addOption("green", "green");
    colorChoice.addOption("yellow", "yellow");
    colorEntry = judas.add("Color", false).withWidget(BuiltInWidgets.kBooleanBox).getEntry();
    judas.add("Target Color", colorChoice).withWidget(BuiltInWidgets.kComboBoxChooser);
    //This is for the dropdown menu where the colorChoice is being a dropdown for each option and add option adds for each one.
    
  }
  
  @Override
  public void autonomousInit() {
    test = 0;
    autoTimer.reset();
    autoTimer.start();

  }

  @Override
  public void autonomousPeriodic() {
    while(autoTimer.get() <= 2.0){
      drive(0.2);
    }
    stop();
  }

  private void stop(){
    leftMotor.set(0);
    rightMotor.set(0);
    updateLEDs();
  }

  /**
   * 
   * @param power 0 to 1 is forward, -1 to 0 is reverse
   */
  private void drive(double power){
    leftMotor.set(power);
    rightMotor.set(power);
    updateLEDs();
  }

  /**
   * 
   * @param power a value 0 to 1
   */
  private void turnRight(double power){
    leftMotor.set(power);
    rightMotor.set(-power);
    updateLEDs();
  }

  /**
   * 
   * @param power a value 0 to 1
   */
  private void turnLeft(double power){
    leftMotor.set(-power);
    rightMotor.set(power);
    updateLEDs();
  }

  @Override
  public void disabledInit() {
    if(robotDrive != null){
      robotDrive.setSafetyEnabled(false);
    }
  }

  @Override
  public void teleopInit() {
    super.teleopInit();
    robotDrive = new DifferentialDrive(rightMotor, leftMotor);
  }


  @Override
  public void teleopPeriodic() {
    /*
      Drive Controls
    */
    //TODO: MIGHT NEED TO ADJUST FOR SENSITIVITY
    robotDrive.arcadeDrive(drivController.getY(GenericHID.Hand.kRight), drivController.getX(GenericHID.Hand.kRight));


    /*
      CP Controls 
     */

    /* CP Spin */
    double manipRightTrigger = manipController.getTriggerAxis(GenericHID.Hand.kRight);
    double manipLeftTrigger = manipController.getTriggerAxis(GenericHID.Hand.kLeft);

    System.out.println("Right Trigger: " + manipRightTrigger);
    System.out.println("Left Trigger : " + manipLeftTrigger);

    //TODO: MIGHT NEED TO CHANGE VALUES DEPENDING ON WHAT THE CONTROLLER GIVES
    if(manipRightTrigger > 0){
      //TODO: ADJUST DIVISION FOR SENSITIVITY
      cpSpinMotor.set(manipRightTrigger/2);
    } else {
      //TODO: ADJUST DIVISION FOR SENSITIVITY
      cpSpinMotor.set(manipLeftTrigger/2);
    }

    /* CP Lift */
    
    double manipLeftStickY = manipController.getY(GenericHID.Hand.kLeft);

    //TODO: ADJUST DIVISION FOR SENSITIVITY
    if(manipLeftStickY > 0 && !highLimit.get()){
      cpLiftMotor.set(manipLeftStickY/4);
    } else if(manipLeftStickY > 0 && !lowLimit.get()){
      cpLiftMotor.set(manipLeftStickY/4);
    }

    /*
      Intake Controls
    */

    /* Intake */
    double manipRightStickX = manipController.getX(Hand.kRight);

    //TODO: ADJUST FOR SENSITIVITY
    intakeMotor.set(manipRightStickX/2);

    /* Intake Lift */

    //TODO ?

    /*
      LEDS
    */
    updateLEDs();
  }

  private void updateLEDs(){
        //Get drive motor states to send to arduino
        final double rightMotorPower = rightMotor.getSpeed();
        final double leftMotorPower = leftMotor.getSpeed();
    
        //Send state of left motor to arduino
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
    
        //Send state or right motor to arduino
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