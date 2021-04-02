/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants;
import frc.robot.subsystems.chassis.Drivetrain;
import frc.robot.subsystems.superstructure.Flywheel;
import frc.robot.subsystems.superstructure.Hopper;
import edu.wpi.first.wpilibj.controller.ProfiledPIDController;
import edu.wpi.first.wpilibj.trajectory.TrapezoidProfile;

import org.photonvision.*;

public class TurnToTarget extends CommandBase {
  @SuppressWarnings({"PMD.UnusedPrivateField", "PMD.SingularField"})
  private final Drivetrain s_drive;
  private final PhotonCamera camera;
  private final ProfiledPIDController pid = new ProfiledPIDController(
    Constants.Vision.kP,
    Constants.Vision.kI,
    Constants.Vision.kD, 
    new TrapezoidProfile.Constraints(Constants.Vision.MAX_VELOCITY, Constants.Vision.MAX_ACCELERATION);
  private boolean isFinished = false;

  public TurnToTarget(Drivetrain drive, PhotonCamera camera) {
    s_drive = drive;
    this.camera = camera;
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(drive);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    isFinished = false;
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    PhotonPipelineResult result = camera.getLatestResult();
    if (result.hasTargets()) {
      PhotonTrackedTarget target = result.getBestTarget();
      double yaw = target.getYaw();
      if (yaw < Constants.Vision.THRESHOLD) {
        double angularVelocity = pid.calculate(yaw, 0);
        drive.curveDrive(0, angularVelocity, true);
      } else {
        isFinished = true;
      }
    }
    //drive.curveDrive(0.7, 0, false);
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    drive.curveDrive(0, 0, false);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return isFinished;
  }
}
