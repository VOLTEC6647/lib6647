# lib6647

Team 6647's library for JSON-oriented object initialization, among other things.
<p align="left"><a href="https://github.com/pacoito123/lib6647" target="_blank"><img src="https://i.imgur.com/F4focyC.png"></a></p>

## Getting Started

### Usage

Team 6647 encourages the usage of this library by any team that may find it useful, period. Giving us credit is optional, but greatly appreciated. Please feel free to request anything your team might need that could be added to this library.

### Compiling lib6647 to your Robot

**lib6647** must be added as a dependency in your Robot's _build.gradle_ file, as well as the **jackson** library that it requires.

First, add the following url to your Maven repositories in _build.gradle_: https://jitpack.io. This is to effortlessly compile code from any github project release or commit into your code.

Your _build.gradle_ file should look like this (if no other Maven repositories are present):

```
    repositories {
        mavenCentral()
        maven {
            url 'https://jitpack.io'
        }
    }
```

Then, add **lib6647**, **jackson-core**, **jackson-databind**, and **jackson-annotations** as a dependency in your _build.gradle_'s dependencies like so:

```
    // https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind
    compile group: 'com.fasterxml.jackson.core', name: 'jackson-databind', version: '2.10.0.pr1'
    // https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-core
    compile group: 'com.fasterxml.jackson.core', name: 'jackson-core', version: '2.10.0.pr1'
    // https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-annotations
    compile group: 'com.fasterxml.jackson.core', name: 'jackson-annotations', version: '2.10.0.pr1'

    // lib6647
    compile group: 'com.github.pacoito123', name: 'lib6647', version: '-SNAPSHOT'
```

Your dependencies in should look like this (again, if none other than WPILib's dependencies are present):

```
    dependencies {
        // WPILib dependencies.
        compile wpi.deps.wpilib()
        compile wpi.deps.vendor.java()
        nativeZip wpi.deps.vendor.jni(wpi.platforms.roborio)
        nativeDesktopZip wpi.deps.vendor.jni(wpi.platforms.desktop)
        testCompile 'junit:junit:4.12'

        // https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind
    	compile group: 'com.fasterxml.jackson.core', name: 'jackson-databind', version: '2.10.0.pr1'
    	// https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-core
    	compile group: 'com.fasterxml.jackson.core', name: 'jackson-core', version: '2.10.0.pr1'
    	// https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-annotations
    	compile group: 'com.fasterxml.jackson.core', name: 'jackson-annotations', version: '2.10.0.pr1'

        // lib6647
        compile group: 'com.github.pacoito123', name: 'lib6647', version: '-SNAPSHOT'
    }
```

Of course, you can change the version of the library to any specific commit in any branch, just make sure that your code is compatible with it.

Now you're ready to begin instantiating and initializing objects through JSON!

## To do (for now)

- [ ] Implement every HyperComponent Wrapper (if needed).
	- [ ] HyperAHRS
	- [ ] HyperAnalogPotentiometer
	- [ ] HyperCompressor
	- [ ] HyperDigitalInput
	- [ ] HyperDoubleSolenoid
	- [ ] HyperEncoder
	- [ ] HyperPDP
	- [x] HyperPIDController
	- [x] HyperSolenoid
	- [x] HyperTalon
	- [ ] HyperUltrasonic
	- [x] HyperVictor
- [ ] Implement SuperComponents.
	- [x] SuperAHRS
	- [ ] SuperAnalogPotentiometer
	- [x] SuperComponent (Custom Component with custom initialization)
	- [x] SuperCompressor
	- [x] SuperDigitalInput
	- [x] SuperDoubleSolenoid
	- [x] SuperEncoder
	- [x] SuperPDP
	- [x] SuperSolenoid
	- [x] SuperTalon
	- [x] SuperUltrasonic
	- [x] SuperVictor
- [x] Improve Controller initialization.
	- [x] Create JController class, for initializing Buttons along with a Controller.
	- [x] Add Buttons for each POV and axis found for the Controller.
	- [x] Integrate Controller initialization with a Robot template.
- [x] Implement Team 254's Looper subroutines.
	- [x] Create LooperRobot template.
	- [ ] Create LoopInitException, to be thrown when a Loop cannot be initialized properly.
	- [ ] Allow for different period times across Looper instances.
- [x] Improve PID subsystems and calibration.
	- [x] Facilitate updating PID values from Shuffleboard/SmartDashboard without having to reload everything.
	- [x] Allow for different PID loops within the same PIDSuperSubsystem.
	- [x] Re-implement max output functionality into PIDControllers.
- [ ] Add more flexibility and configuration options.
- [x] Comment everything.
- [x] Do proper Exception handling.
- [ ] Write proper documentation for this library.
- [x] Blame mechanical.

## Authors

* **Francisco Rubio** - [pacoito123](https://github.com/pacoito123)

## License

This project is under the BSD License for WPILib code, see: [BSD_License_for_WPILib_code.txt](BSD_License_for_WPILib_code.txt).
