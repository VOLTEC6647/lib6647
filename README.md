# lib6647

FRC Team #6647's library for JSON-oriented object initialization, among other things.
<p align="left"><a href="https://github.com/pacoito123/lib6647" target="_blank"><img src="https://i.imgur.com/F4focyC.png"></a></p>

## Getting Started

### Usage

FRC Team #6647 encourages the usage of this library by any team that may find it useful, period. Giving us credit is optional, but greatly appreciated. Please feel free to request anything your team might need that could be added to this library.

### Documentation

Currently written documentation can be found at https://lib6647.readthedocs.io/, though it's still a HUGE work in progress.

## To do (for now)

* [ ] Add simulation support for HyperComponents.
* [ ] Implement every HyperComponent Wrapper (if needed).
  + [x] HyperAHRS
  + [ ] HyperAnalogPotentiometer
  + [ ] HyperCompressor
  + [ ] HyperDigitalInput
  + [x] HyperDoubleSolenoid
  + [ ] HyperEncoder
  + [x] HyperFalcon
  + [ ] HyperPDP
  + [x] HyperSolenoid
  + [ ] HyperSpark
  + [x] HyperSparkMax
  + [x] HyperTalon
  + [ ] HyperUltrasonic
  + [x] HyperVictor
* [ ] Centralize SuperComponent storage, overhaul 'interface' workaround.
* [ ] Implement SuperComponents.
  + [x] SuperAHRS
  + [ ] SuperAnalogPotentiometer
  + [x] SuperComponent (Custom Component with custom initialization)
  + [x] SuperCompressor
  + [x] SuperDigitalInput
  + [x] SuperDoubleSolenoid
  + [x] SuperEncoder
  + [x] SuperFalcon
  + [ ] SuperLimelight
  + [x] SuperPDP
  + [x] SuperPID
  + [x] SuperProfiledPID
  + [x] SuperServo
  + [x] SuperSolenoid
  + [ ] SuperSpark
  + [x] SuperSparkMax
  + [x] SuperTalon
  + [x] SuperUltrasonic
  + [x] SuperVictor
* [ ] Add field to output SuperComponents to Shuffleboard.
  + [ ] Overwrite initSendable in HyperComponents, to support added features.
* [x] Improve Controller initialization.
  + [x] Create JController class, for initializing Buttons along with a Controller.
  + [x] Add Buttons for each POV and axis found for the Controller.
  + [x] Integrate Controller initialization with a Robot template.
* [x] Implement Team 254's Looper subroutines.
  + [x] Create LooperRobot template.
  + [ ] Create LoopRegisterException, to be thrown when a Loop cannot be registered properly.
  + [ ] Allow for different period times across Looper instances.
* [x] Improve PID subsystems and calibration.
  + [x] Facilitate updating PID values from Shuffleboard/SmartDashboard without having to reload everything.
  + [x] Allow for different PID loops within the same PIDSuperSubsystem.
  + [x] Re-implement max output functionality into PIDControllers.
* [ ] Implement characterization routines into library.
  + [x] Copy SimEnabler class from 'frc-characterization', to enable characterization of simulations.
  + [ ] Create CharLoop classes, with templates to run characterization routines across different robots.
* [ ] Fully implement trajectory generation into library.
* [ ] Add more flexibility and configuration options.
  + [x] To HyperTalon.
  + [x] To HyperVictor.
  + [x] To HyperFalcon.
  + [ ] To HyperSparkMax.
* [ ] Use FastMath calculations wherever possible/useful (recommend usage for projects using this library).
* [ ] Use FastUtil implementations of Java Collections (recommend usage for projects using this library).
* [x] Comment everything.
* [x] Do proper Exception handling.
* [ ] Write proper documentation for this library.
* [x] Blame mechanical.

## Authors

* **Francisco Rubio** - [pacoito123](https://github.com/pacoito123)
* **Ian De La Garza** - [ianyan11](https://github.com/ianyan11)

## License

This project is under the WPILib License, see: [WPILib-License.md](WPILib-License.md).

