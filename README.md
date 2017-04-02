# RPLidar4J

RPLidar4J, is a Java library designed to manage the sensor [RPLidar A1](http://www.slamtec.com/en/Lidar), a 2D LIDAR sensor in a easy way.

![](./docs/images/all_RPLidarA1.jpg)

# The sensor

RPLIDAR is a low cost 360 degree 2D laser scanner (LIDAR) solution 
developed by RoboPeak. The system can perform 360 degree scan within 
6 meter range. The produced 2D point cloud data can be used in mapping, 
localization and object/environment modeling

RPLIDAR is basically a laser triangulation measurement system. 
It can work excellent in all kinds of indoor environment and outdoor 
environment without sunlight.

The RPLIDAR adopts coordinate system of the left hand. The dead ahead 
of the sensors is the x axis of the coordinate system; the origin is 
the rotating center of the range scanner core. The rotation angle 
increases as rotating clockwise. The detailed definition is shown in 
the following figure:

![](./docs/images/rplidar_A1.png)

**Technical docs:**

- [datasheet-rplidar](./docs/sdk/datasheet-rplidar.pdf)
- [communication-protocol](./docs/rpk-02-communication-protocol.pdf)

## Getting Started

### Connect the sensor on your robot

If you adquire the RPLidarA1 Kit, the sensor includes in the Kit a small
USB Controller. Connect plug the sensor with the USB Controller. 
Later, connect the USB Controller to your favourite Brick (EV3, BrickPi+ & PiStorms)
You should notice that the brick turn on the USB Controller and 
the sensor start turning. In order to know if EV3Dev recognize 
the sensor execute the command `lsusb`:

```
robot@ev3dev:/dev$ lsusb
Bus 001 Device 004: ID 10c4:ea60 Cygnal Integrated Products, Inc. CP210x UART Bridge / myAVR mySmartUSB light
Bus 001 Device 003: ID 0424:ec00 Standard Microsystems Corp. SMSC9512/9514 Fast Ethernet Adapter
Bus 001 Device 002: ID 0424:9514 Standard Microsystems Corp. 
Bus 001 Device 001: ID 1d6b:0002 Linux Foundation 2.0 root hub
```

If you detected this element: `Cygnal Integrated Products, Inc. CP210x UART Bridge / myAVR mySmartUSB light`
is a good signal, the USB Controller was recognized in the system.

Once you know that Lynux detect the USB Controller, it is necessary to
know in what device are associated. To list the devices of your brick,
type `ls /dev`:

```
robot@ev3dev:/dev$ ls /dev/
autofs           fb1      loop-control  memory_bandwidth    pts    ram4    snd     tty14  tty24  tty34  tty44  tty54  tty7       vc-mem  vcsa1
block            fd       loop0         mmcblk0             ram0   ram5    stderr  tty15  tty25  tty35  tty45  tty55  tty8       vchiq   vcsa2
btrfs-control    full     loop1         mmcblk0p1           ram1   ram6    stdin   tty16  tty26  tty36  tty46  tty56  tty9       vcio    vcsa3
bus              fuse     loop2         mmcblk0p2           ram10  ram7    stdout  tty17  tty27  tty37  tty47  tty57  ttyAMA0    vcs     vcsa4
cachefiles       gpiomem  loop3         mqueue              ram11  ram8    tty     tty18  tty28  tty38  tty48  tty58  ttyS0      vcs1    vcsa5
char             hwrng    loop4         net                 ram12  ram9    tty0    tty19  tty29  tty39  tty49  tty59  ttyUSB0    vcs2    vcsa6
console          i2c-1    loop5         network_latency     ram13  random  tty1    tty2   tty3   tty4   tty5   tty6   ttyprintk  vcs3    vcsm
cpu_dma_latency  initctl  loop6         network_throughput  ram14  raw     tty10   tty20  tty30  tty40  tty50  tty60  uhid       vcs4    vhci
cuse             input    loop7         null                ram15  rfkill  tty11   tty21  tty31  tty41  tty51  tty61  uinput     vcs5    watchdog
disk             kmsg     mapper        ppp                 ram2   serial  tty12   tty22  tty32  tty42  tty52  tty62  urandom    vcs6    watchdog0
fb0              log      mem           ptmx                ram3   shm     tty13   tty23  tty33  tty43  tty53  tty63  vc-cma     vcsa    zero
```

If you didn´t connect another device on your robot, the device 
`ttyUSB0` should be your RPLidarA1 sensor. 

### Install librxtx-java

Current implementation uses the library `librxtx-java` to manage 
the Serial port communications. This library is very popular on Java 
ecosystem. To install the library on your brick, install the following 
Debian package:

```
sudo apt-get install librxtx-java
```

When the Debian package is finished, you should be the native library on
the following path: `/usr/lib/jni/`

```
robot@ev3dev:~$ ls /usr/lib/jni/
libopencv_java249.so   librxtxI2C.so               librxtxParallel.so       librxtxRS485.so        librxtxRaw.so             librxtxSerial.so
librxtxI2C-2.2pre1.so  librxtxParallel-2.2pre1.so  librxtxRS485-2.2pre1.so  librxtxRaw-2.2pre1.so  librxtxSerial-2.2pre1.so
```

### Add the dependency on the project

To use this project, import the library as a Maven dependency.

```
<dependency>
    <groupId>com.github.ev3dev-lang-java</groupId>
    <artifactId>RPLidar4J</artifactId>
    <version>v0.1.0</version>
</dependency>
```

Further information: https://jitpack.io/#ev3dev-lang-java/RPLidar4J/v0.1.0

### Using the sensor

Create a new Java project on your favourite IDE and add the following 
class on the project:

``` java
package examples;

import ev3dev.sensors.slamtec.RPLidarA1;
import ev3dev.sensors.slamtec.model.Scan;
import lombok.extern.slf4j.Slf4j;

public @Slf4j class Demo {

    public static void main(String[] args) throws Exception {

        log.info("Testing RPLidar on a EV3 Brick with Java");
        final String USBPort = "/dev/ttyUSB0";
        final RPLidarA1 lidar = new RPLidarA1(USBPort);
        lidar.init();

        for(int x = 0; x <= 5; x++){

            final Scan scan = lidar.scan();
            log.info("Iteration: {}, Measures: {}", x, scan.getDistances().size());
            scan.getDistances()
                .stream()
                .filter((measure) -> measure.getQuality() > 10)
                .filter((measure) -> (measure.getAngle() >= 345 || measure.getAngle() <= 15))
                .filter((measure) -> measure.getDistance() <= 50)
                .forEach(System.out::println);
        }

        lidar.close();
        log.info("End demo");
        System.exit(0);
    }
}
```

Once, you have the example in your project, create a Jar with the project
and deploy on your Brick using some Plugin for Maven or Gradle.

To run the example this the command:

```
java -Djava.library.path=/usr/lib/jni/ -cp RPLidar4J-all-0.1.0.jar examples.Demo
```

### Output

```
robot@ev3dev:~$ java -Djava.library.path=/usr/lib/jni/ -cp RPLidar4J-all-0.1.0.jar examples.Demo
2017-03-26 20:07:53 [main] INFO  examples.Demo - Testing RPLidar on a EV3 Brick with Java
2017-03-26 20:07:53 [main] INFO  e.sensors.slamtec.RPLidarA1Driver - Starting a RPLidarA1 instance
2017-03-26 20:07:53 [main] INFO  e.sensors.slamtec.RPLidarA1Driver - Connecting with: /dev/ttyUSB0
Opening port /dev/ttyUSB0
Stable Library
=========================================
Native lib Version = RXTX-2.2pre2
Java lib Version   = RXTX-2.1-7
WARNING:  RXTX Version mismatch
	Jar version = RXTX-2.1-7
	native lib Version = RXTX-2.2pre2
2017-03-26 20:07:55 [main] INFO  examples.Demo - Iteration: 0, Measures: 0
2017-03-26 20:07:56 [main] INFO  examples.Demo - Iteration: 1, Measures: 0
2017-03-26 20:07:57 [main] INFO  examples.Demo - Iteration: 2, Measures: 295
ScanDistance(angle=8, distance=31.5, quality=15, start=false)
ScanDistance(angle=9, distance=30.9, quality=23, start=false)
ScanDistance(angle=10, distance=30.525, quality=27, start=false)
ScanDistance(angle=11, distance=30.35, quality=26, start=false)
ScanDistance(angle=13, distance=30.25, quality=28, start=false)
ScanDistance(angle=14, distance=30.3, quality=27, start=false)
ScanDistance(angle=15, distance=30.45, quality=26, start=false)
2017-03-26 20:07:58 [main] INFO  examples.Demo - Iteration: 3, Measures: 303
ScanDistance(angle=7, distance=31.85, quality=12, start=false)
ScanDistance(angle=9, distance=31.05, quality=20, start=false)
ScanDistance(angle=10, distance=30.65, quality=24, start=false)
ScanDistance(angle=11, distance=30.325, quality=27, start=false)
ScanDistance(angle=12, distance=30.25, quality=28, start=false)
ScanDistance(angle=13, distance=30.25, quality=27, start=false)
ScanDistance(angle=14, distance=30.25, quality=29, start=false)
ScanDistance(angle=15, distance=30.425, quality=27, start=false)
2017-03-26 20:07:59 [main] INFO  examples.Demo - Iteration: 4, Measures: 296
ScanDistance(angle=7, distance=31.85, quality=11, start=false)
ScanDistance(angle=9, distance=31.1, quality=20, start=false)
ScanDistance(angle=10, distance=30.65, quality=25, start=false)
ScanDistance(angle=11, distance=30.35, quality=29, start=false)
ScanDistance(angle=12, distance=30.225, quality=26, start=false)
ScanDistance(angle=13, distance=30.275, quality=26, start=false)
ScanDistance(angle=15, distance=30.425, quality=27, start=false)
2017-03-26 20:08:00 [main] INFO  examples.Demo - Iteration: 5, Measures: 288
ScanDistance(angle=8, distance=31.375, quality=15, start=false)
ScanDistance(angle=9, distance=30.85, quality=20, start=false)
ScanDistance(angle=10, distance=30.475, quality=27, start=false)
ScanDistance(angle=11, distance=30.3, quality=28, start=false)
ScanDistance(angle=12, distance=30.275, quality=27, start=false)
ScanDistance(angle=13, distance=30.25, quality=28, start=false)
ScanDistance(angle=14, distance=30.3, quality=27, start=false)
ScanDistance(angle=15, distance=30.475, quality=25, start=false)
2017-03-26 20:08:00 [main] INFO  examples.Demo - End demo
robot@ev3dev:~$ 
```

## UML Design

![](./docs/uml/graph.png)


## TODO

- Improve the Design solution
- Refactor Service layer
- Add Event-based support
- Develop a ROS node for RPLidar A1
- Add LeJOS Sensor support
- Add RPLidarA2 support
- Add Mock test


