# RPLidar4J

A Java development to provide support for [RPLidar A1](http://www.slamtec.com/en/Lidar)

![](https://raw.githubusercontent.com/ev3dev-lang-java/RPLidar4J/master/docs/piStormsV2_RPLidarA1.jpg)

## TODO

- Add Mock support
- Add OS Detection (The project is designed for EV3 & Raspi Boards)

```
java -jar /home/robot/RPLidar4J-all-0.1.0.jar
ev3dev#0|2017-03-13 23:54:23 [main] TRACE ev3dev.sensors.slamtec.RPLidarA1 - /home/robot/ultra_simple
ev3dev#0|2017-03-13 23:54:23 [main] TRACE ev3dev.sensors.slamtec.RPLidarA1 - Is Execute allow : false
ev3dev#0|2017-03-13 23:54:23 [main] TRACE ev3dev.sensors.slamtec.RPLidarA1 - Is Write allow : true
ev3dev#0|2017-03-13 23:54:23 [main] TRACE ev3dev.sensors.slamtec.RPLidarA1 - Is Read allow : true
ev3dev#0|2017-03-13 23:54:27 [main] TRACE ev3dev.sensors.slamtec.RPLidarA1 - Ultra simple LIDAR data grabber for RPLIDAR.
ev3dev#0|2017-03-13 23:54:27 [main] TRACE ev3dev.sensors.slamtec.RPLidarA1 - Version: 1.5.7
ev3dev#0|2017-03-13 23:54:27 [main] TRACE ev3dev.sensors.slamtec.RPLidarA1 - RPLIDAR S/N: 95C299F3C1E39AF2A2E09AF006133230
ev3dev#0|2017-03-13 23:54:27 [main] TRACE ev3dev.sensors.slamtec.RPLidarA1 - Firmware Ver: 1.14
ev3dev#0|2017-03-13 23:54:27 [main] TRACE ev3dev.sensors.slamtec.RPLidarA1 - Hardware Rev: 0
ev3dev#0|2017-03-13 23:54:27 [main] TRACE ev3dev.sensors.slamtec.RPLidarA1 - RPLidar health status : 0
ev3dev#0|2017-03-13 23:54:27 [main] TRACE ev3dev.sensors.slamtec.RPLidarA1 -    theta: 0.19 Dist: 02868.25 Q: 12 
ev3dev#0|2017-03-13 23:54:27 [main] TRACE ev3dev.sensors.slamtec.RPLidarA1 -    theta: 1.33 Dist: 02950.25 Q: 12 
ev3dev#0|2017-03-13 23:54:27 [main] TRACE ev3dev.sensors.slamtec.RPLidarA1 -    theta: 2.47 Dist: 03035.00 Q: 13 
ev3dev#0|2017-03-13 23:54:27 [main] TRACE ev3dev.sensors.slamtec.RPLidarA1 -    theta: 3.58 Dist: 03127.00 Q: 19 
ev3dev#0|2017-03-13 23:54:27 [main] TRACE ev3dev.sensors.slamtec.RPLidarA1 -    theta: 4.73 Dist: 03245.75 Q: 15 
```

# Getting Started:

```
ls /dev
```

You should see something similar:

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

if you type:

```
robot@ev3dev:/dev$ lsusb
Bus 001 Device 004: ID 10c4:ea60 Cygnal Integrated Products, Inc. CP210x UART Bridge / myAVR mySmartUSB light
Bus 001 Device 003: ID 0424:ec00 Standard Microsystems Corp. SMSC9512/9514 Fast Ethernet Adapter
Bus 001 Device 002: ID 0424:9514 Standard Microsystems Corp. 
Bus 001 Device 001: ID 1d6b:0002 Linux Foundation 2.0 root hub
```

```
cd ~
mkdir slamtec          
mkdir slamtec/rplidara1
cd slamtec/rplidara1/
wget http://www.slamtec.com/download/lidar/sdk/rplidar_sdk.1.4.5.7z
wget http://www.slamtec.com/download/lidar/sdk/rplidar_sdk_v1.5.7.zip
sudo apt-get install p7zip-full
sudo apt-get install unzip
7z x ~/slamtec/rplidara1/rplidar_sdk_v1.4.5.7z
7z x ~/slamtec/rplidara1/rplidar_sdk_v1.5.7.zip
unzip rplidar_sdk_v1.5.7.zip -d rplidar_sdk_v1.5.7
sudo apt-get install build-essential
cd rplidar_sdk_v1.5.7/sdk
make
```

```
cd ./output/Linux/Release
./simple_grabber /dev/ttyUSB0
./ultra_simple /dev/ttyUSB0
```

## Output

```
   theta: 0.66 Dist: 02918.75 Q: 12 
   theta: 1.77 Dist: 03004.25 Q: 12 
   theta: 2.92 Dist: 03089.75 Q: 12 
   theta: 4.06 Dist: 03160.75 Q: 19 
   theta: 5.17 Dist: 03285.00 Q: 14 
   theta: 6.33 Dist: 03235.25 Q: 22 
   theta: 7.48 Dist: 03222.75 Q: 22 
   theta: 8.62 Dist: 03183.50 Q: 28 
   theta: 9.77 Dist: 00000.00 Q: 0 
   theta: 10.91 Dist: 00000.00 Q: 0 
   theta: 12.08 Dist: 03062.00 Q: 14 
   theta: 13.17 Dist: 00000.00 Q: 0 
   theta: 15.94 Dist: 00573.75 Q: 17 
   theta: 17.17 Dist: 00563.00 Q: 21 
   theta: 18.11 Dist: 00551.50 Q: 21 
   theta: 19.31 Dist: 00542.50 Q: 21 
   theta: 20.52 Dist: 00535.00 Q: 24 
   theta: 21.53 Dist: 00528.25 Q: 23 
   theta: 22.70 Dist: 00522.25 Q: 26 
   theta: 24.05 Dist: 00517.50 Q: 24 
   theta: 25.22 Dist: 00511.75 Q: 24 
   theta: 26.36 Dist: 00507.75 Q: 23 
   theta: 27.50 Dist: 00501.50 Q: 25 
   theta: 28.55 Dist: 00499.50 Q: 26 
   theta: 29.64 Dist: 00494.75 Q: 24 
   theta: 30.78 Dist: 00491.50 Q: 24 
   theta: 32.14 Dist: 00488.25 Q: 23 
   theta: 33.14 Dist: 00485.00 Q: 25 
   theta: 34.42 Dist: 00482.50 Q: 25 
   theta: 35.41 Dist: 00480.25 Q: 25 
   theta: 36.58 Dist: 00478.50 Q: 25 
   theta: 37.67 Dist: 00476.50 Q: 23 
   theta: 38.84 Dist: 00474.50 Q: 25 
   theta: 40.14 Dist: 00472.50 Q: 27 
   theta: 41.25 Dist: 00471.25 Q: 25 
   theta: 42.50 Dist: 00471.00 Q: 22 
   theta: 43.55 Dist: 00470.25 Q: 25 
   theta: 44.75 Dist: 00469.75 Q: 23 
   theta: 45.77 Dist: 00470.00 Q: 24 
   theta: 46.92 Dist: 00470.00 Q: 22 
   theta: 48.14 Dist: 00469.25 Q: 26 
   theta: 49.39 Dist: 00469.50 Q: 23 
   theta: 50.39 Dist: 00469.75 Q: 22 
   theta: 51.55 Dist: 00470.75 Q: 24 
   theta: 52.73 Dist: 00471.50 Q: 26 
   theta: 53.98 Dist: 00472.00 Q: 25 
   theta: 54.97 Dist: 00473.75 Q: 25 
   theta: 56.03 Dist: 00475.25 Q: 27 
   theta: 57.36 Dist: 00477.25 Q: 25 
   theta: 58.53 Dist: 00479.00 Q: 24 
   theta: 59.39 Dist: 00480.25 Q: 23 
   theta: 60.81 Dist: 00482.50 Q: 26 
   theta: 61.72 Dist: 00485.50 Q: 24 
   theta: 62.80 Dist: 00488.50 Q: 24 
   theta: 64.17 Dist: 00490.25 Q: 23 
   theta: 65.27 Dist: 00493.25 Q: 25 
   theta: 66.19 Dist: 00497.50 Q: 27 
   theta: 67.56 Dist: 00500.50 Q: 25 
   theta: 68.53 Dist: 00503.75 Q: 24 
   theta: 69.78 Dist: 00509.00 Q: 23 
   theta: 70.89 Dist: 00512.75 Q: 23 
   theta: 72.06 Dist: 00517.50 Q: 24 
   theta: 73.02 Dist: 00522.50 Q: 23 
   theta: 74.30 Dist: 00528.00 Q: 23 
   theta: 75.20 Dist: 00533.75 Q: 23 
   theta: 76.31 Dist: 00540.50 Q: 22 
   theta: 77.58 Dist: 00546.75 Q: 22 
   theta: 78.66 Dist: 00552.25 Q: 22 
   theta: 79.81 Dist: 00559.00 Q: 24 
   theta: 80.97 Dist: 00566.75 Q: 25 
   theta: 82.06 Dist: 00574.00 Q: 21 
   theta: 83.19 Dist: 00582.25 Q: 22 
   theta: 84.23 Dist: 00589.75 Q: 21 
   theta: 85.34 Dist: 00599.00 Q: 20 
   theta: 86.47 Dist: 00609.50 Q: 22 
   theta: 87.50 Dist: 00618.50 Q: 20 
   theta: 88.75 Dist: 00630.00 Q: 23 
   theta: 89.92 Dist: 00642.25 Q: 22 
   theta: 90.97 Dist: 00654.25 Q: 20 
   theta: 92.05 Dist: 00668.50 Q: 19 
   theta: 93.12 Dist: 00684.00 Q: 21 
   theta: 94.22 Dist: 00698.75 Q: 18 
   theta: 95.45 Dist: 00714.00 Q: 19 
   theta: 96.48 Dist: 00732.75 Q: 20 
   theta: 97.64 Dist: 00748.75 Q: 21 
   theta: 98.77 Dist: 00770.00 Q: 18 
   theta: 99.77 Dist: 00000.00 Q: 0 
   theta: 99.88 Dist: 00792.00 Q: 16 
   theta: 100.91 Dist: 00000.00 Q: 0 
   theta: 102.45 Dist: 02133.00 Q: 26 
   theta: 103.17 Dist: 00000.00 Q: 0 
   theta: 104.31 Dist: 00000.00 Q: 0 
   theta: 105.89 Dist: 02065.75 Q: 18 
   theta: 107.03 Dist: 02033.50 Q: 33 
   theta: 108.19 Dist: 02018.00 Q: 20 
   theta: 109.31 Dist: 02004.00 Q: 13 
   theta: 110.45 Dist: 01990.50 Q: 9 
   theta: 111.64 Dist: 01964.75 Q: 25 
   theta: 112.78 Dist: 01941.50 Q: 12 
   theta: 113.92 Dist: 01931.00 Q: 36 
   theta: 115.09 Dist: 01917.25 Q: 39 
   theta: 116.22 Dist: 01896.75 Q: 9 
   theta: 117.39 Dist: 01886.75 Q: 26 
   theta: 118.53 Dist: 01874.25 Q: 20 
   theta: 119.64 Dist: 01865.25 Q: 11 
   theta: 120.80 Dist: 01856.00 Q: 39 
   theta: 121.92 Dist: 01852.25 Q: 33 
   theta: 123.08 Dist: 01843.00 Q: 12 
   theta: 124.22 Dist: 01834.50 Q: 45 
   theta: 125.39 Dist: 01824.00 Q: 39 
   theta: 126.56 Dist: 01821.50 Q: 39 
   theta: 127.66 Dist: 01814.00 Q: 19 
   theta: 128.81 Dist: 01807.50 Q: 41 
   theta: 129.97 Dist: 01804.75 Q: 40 
   theta: 131.12 Dist: 01809.25 Q: 32 
   theta: 132.23 Dist: 01797.00 Q: 39 
   theta: 133.41 Dist: 01797.50 Q: 37 
   theta: 134.52 Dist: 01801.25 Q: 39 
   theta: 135.66 Dist: 01798.75 Q: 40 
   theta: 136.81 Dist: 01800.75 Q: 38 
   theta: 137.94 Dist: 01803.00 Q: 38 
   theta: 139.09 Dist: 01802.00 Q: 27 
   theta: 140.20 Dist: 01805.75 Q: 24 
   theta: 141.36 Dist: 01807.75 Q: 40 
   theta: 142.47 Dist: 01819.00 Q: 42 
   theta: 143.61 Dist: 01824.00 Q: 39 
   theta: 144.75 Dist: 01830.25 Q: 38 
   theta: 145.92 Dist: 01832.25 Q: 12 
   theta: 147.03 Dist: 01843.00 Q: 13 
   theta: 148.19 Dist: 01849.00 Q: 39 
   theta: 149.34 Dist: 01856.25 Q: 43 
   theta: 150.47 Dist: 01874.00 Q: 20 
   theta: 151.62 Dist: 01885.75 Q: 9 
   theta: 152.77 Dist: 01886.25 Q: 39 
   theta: 153.88 Dist: 01908.00 Q: 9 
   theta: 155.02 Dist: 01909.25 Q: 17 
   theta: 156.14 Dist: 01928.00 Q: 37 
   theta: 157.30 Dist: 01952.50 Q: 16 
   theta: 158.42 Dist: 01955.00 Q: 11 
   theta: 159.58 Dist: 01980.25 Q: 37 
   theta: 160.58 Dist: 02002.00 Q: 11 
   theta: 161.73 Dist: 02020.00 Q: 34 
   theta: 162.83 Dist: 02048.50 Q: 18 
   theta: 163.56 Dist: 00000.00 Q: 0 
   theta: 165.12 Dist: 02080.50 Q: 32 
   theta: 166.23 Dist: 02112.00 Q: 28 
   theta: 167.38 Dist: 02131.50 Q: 32 
   theta: 168.48 Dist: 02158.50 Q: 12 
   theta: 169.25 Dist: 00000.00 Q: 0 
   theta: 170.75 Dist: 02209.50 Q: 28 
   theta: 171.89 Dist: 02262.00 Q: 10 
   theta: 173.02 Dist: 02281.25 Q: 28 
   theta: 174.12 Dist: 02324.50 Q: 13 
   theta: 175.27 Dist: 02356.50 Q: 18 
   theta: 176.41 Dist: 02404.00 Q: 14 
   theta: 177.53 Dist: 02441.75 Q: 24 
   theta: 178.38 Dist: 00000.00 Q: 0 
   theta: 179.77 Dist: 02519.75 Q: 12 
   theta: 180.64 Dist: 00000.00 Q: 0 
   theta: 182.03 Dist: 02582.50 Q: 26 
   theta: 183.20 Dist: 02530.00 Q: 28 
   theta: 184.48 Dist: 02489.50 Q: 26 
   theta: 185.61 Dist: 02459.50 Q: 25 
   theta: 186.78 Dist: 02413.00 Q: 26 
   theta: 187.89 Dist: 02386.50 Q: 25 
   theta: 189.05 Dist: 02352.00 Q: 28 
   theta: 190.20 Dist: 02310.25 Q: 28 
   theta: 191.36 Dist: 02287.75 Q: 32 
   theta: 192.48 Dist: 02257.75 Q: 30 
   theta: 193.62 Dist: 02228.00 Q: 29 
   theta: 194.78 Dist: 02201.50 Q: 32 
   theta: 195.92 Dist: 02179.25 Q: 37 
   theta: 197.06 Dist: 02149.25 Q: 30 
   theta: 198.23 Dist: 02131.75 Q: 30 
   theta: 199.39 Dist: 02114.00 Q: 35 
   theta: 200.50 Dist: 02097.00 Q: 33 
   theta: 201.67 Dist: 02075.75 Q: 32 
   theta: 202.83 Dist: 02054.50 Q: 35 
   theta: 203.94 Dist: 02038.00 Q: 38 
   theta: 205.11 Dist: 02025.25 Q: 36 
   theta: 206.25 Dist: 02005.75 Q: 37 
   theta: 207.38 Dist: 01991.50 Q: 42 
   theta: 208.55 Dist: 01985.50 Q: 45 
   theta: 209.67 Dist: 01969.25 Q: 39 
   theta: 210.86 Dist: 01963.25 Q: 43 
   theta: 212.02 Dist: 01952.25 Q: 40 
   theta: 213.16 Dist: 01944.00 Q: 42 
   theta: 214.28 Dist: 01932.25 Q: 38 
   theta: 215.44 Dist: 01928.75 Q: 38 
   theta: 216.58 Dist: 01919.25 Q: 36 
   theta: 217.69 Dist: 01915.50 Q: 35 
   theta: 218.86 Dist: 01909.50 Q: 38 
   theta: 220.00 Dist: 01900.25 Q: 38 
   theta: 221.17 Dist: 01904.50 Q: 38 
   theta: 222.31 Dist: 01897.50 Q: 38 
   theta: 223.45 Dist: 01895.75 Q: 41 
   theta: 224.56 Dist: 01895.75 Q: 42 
   theta: 225.72 Dist: 01896.50 Q: 42 
   theta: 226.84 Dist: 01891.75 Q: 42 
   theta: 228.02 Dist: 01897.50 Q: 38 
   theta: 229.16 Dist: 01899.25 Q: 38 
   theta: 230.28 Dist: 01907.25 Q: 38 
   theta: 231.42 Dist: 01906.50 Q: 38 
   theta: 232.56 Dist: 01911.50 Q: 38 
   theta: 233.69 Dist: 01918.75 Q: 37 
   theta: 234.83 Dist: 01928.75 Q: 38 
   theta: 235.94 Dist: 01933.75 Q: 38 
   theta: 237.11 Dist: 01939.25 Q: 38 
   theta: 238.23 Dist: 01945.00 Q: 38 
   theta: 239.36 Dist: 01956.50 Q: 41 
   theta: 240.52 Dist: 01966.25 Q: 43 
   theta: 241.66 Dist: 01977.00 Q: 40 
   theta: 242.80 Dist: 01985.75 Q: 43 
   theta: 243.89 Dist: 01997.25 Q: 40 
   theta: 245.05 Dist: 02014.75 Q: 35 
   theta: 246.20 Dist: 02029.00 Q: 37 
   theta: 247.34 Dist: 02041.00 Q: 38 
   theta: 248.47 Dist: 02061.75 Q: 37 
   theta: 249.59 Dist: 02079.00 Q: 30 
   theta: 250.70 Dist: 02101.75 Q: 31 
   theta: 251.86 Dist: 02118.75 Q: 36 
   theta: 253.03 Dist: 02137.00 Q: 34 
   theta: 254.11 Dist: 02168.25 Q: 37 
   theta: 255.28 Dist: 02186.25 Q: 32 
   theta: 256.50 Dist: 02207.50 Q: 30 
   theta: 257.64 Dist: 02235.50 Q: 29 
   theta: 258.80 Dist: 02267.50 Q: 34 
   theta: 259.94 Dist: 02295.25 Q: 37 
   theta: 261.08 Dist: 02242.75 Q: 25 
   theta: 262.27 Dist: 02184.00 Q: 26 
   theta: 263.41 Dist: 02120.75 Q: 26 
   theta: 264.56 Dist: 02070.75 Q: 28 
   theta: 265.73 Dist: 02025.00 Q: 29 
   theta: 266.89 Dist: 01971.50 Q: 27 
   theta: 268.03 Dist: 01933.00 Q: 30 
   theta: 269.20 Dist: 01885.75 Q: 30 
   theta: 270.36 Dist: 01852.25 Q: 31 
   theta: 271.47 Dist: 01815.00 Q: 32 
   theta: 272.64 Dist: 01789.00 Q: 33 
   theta: 273.83 Dist: 01752.50 Q: 36 
   theta: 274.95 Dist: 01722.25 Q: 33 
   theta: 276.09 Dist: 01689.25 Q: 36 
   theta: 277.31 Dist: 01663.50 Q: 38 
   theta: 278.41 Dist: 01637.00 Q: 34 
   theta: 279.56 Dist: 01609.25 Q: 35 
   theta: 280.70 Dist: 01659.75 Q: 28 
   theta: 281.72 Dist: 01681.50 Q: 28 
   theta: 283.00 Dist: 01320.00 Q: 17 
   theta: 284.23 Dist: 01268.75 Q: 24 
   theta: 285.38 Dist: 01217.00 Q: 20 
   theta: 286.52 Dist: 01169.50 Q: 23 
   theta: 287.70 Dist: 01112.25 Q: 18 
   theta: 288.95 Dist: 01051.75 Q: 18 
   theta: 290.08 Dist: 01006.50 Q: 21 
   theta: 291.27 Dist: 00970.75 Q: 25 
   theta: 292.50 Dist: 00933.50 Q: 24 
   theta: 293.64 Dist: 00899.25 Q: 25 
   theta: 294.89 Dist: 00866.75 Q: 28 
   theta: 296.02 Dist: 00837.75 Q: 27 
   theta: 297.17 Dist: 00809.75 Q: 27 
   theta: 298.39 Dist: 00786.75 Q: 30 
   theta: 299.50 Dist: 00781.75 Q: 33 
   theta: 300.61 Dist: 00801.75 Q: 31 
   theta: 301.77 Dist: 00812.50 Q: 28 
   theta: 302.86 Dist: 00827.50 Q: 30 
   theta: 303.86 Dist: 00905.25 Q: 15 
   theta: 305.00 Dist: 00895.50 Q: 18 
   theta: 306.19 Dist: 00890.00 Q: 15 
   theta: 307.34 Dist: 00887.00 Q: 15 
   theta: 308.34 Dist: 00886.50 Q: 17 
   theta: 309.52 Dist: 00886.50 Q: 16 
   theta: 310.69 Dist: 00884.25 Q: 16 
   theta: 311.88 Dist: 00882.00 Q: 17 
   theta: 312.95 Dist: 00879.75 Q: 17 
   theta: 314.14 Dist: 00881.00 Q: 18 
   theta: 315.27 Dist: 00886.50 Q: 16 
   theta: 316.33 Dist: 00892.50 Q: 15 
   theta: 317.44 Dist: 00921.00 Q: 10 
   theta: 318.44 Dist: 01045.00 Q: 16 
   theta: 319.55 Dist: 01054.50 Q: 16 
   theta: 320.66 Dist: 01069.00 Q: 13 
   theta: 320.77 Dist: 00000.00 Q: 0 
   theta: 321.91 Dist: 00000.00 Q: 0 
   theta: 323.47 Dist: 02908.25 Q: 21 
   theta: 324.62 Dist: 02913.25 Q: 22 
   theta: 325.77 Dist: 02918.75 Q: 22 
   theta: 327.83 Dist: 00803.50 Q: 19 
   theta: 328.75 Dist: 00000.00 Q: 0 
   theta: 329.03 Dist: 00788.50 Q: 30 
   theta: 330.92 Dist: 01080.50 Q: 21 
   theta: 332.09 Dist: 01081.75 Q: 33 
   theta: 332.17 Dist: 00000.00 Q: 0 
   theta: 333.70 Dist: 03033.25 Q: 26 
   theta: 334.84 Dist: 03061.00 Q: 22 
   theta: 335.58 Dist: 00000.00 Q: 0 
   theta: 337.09 Dist: 03118.25 Q: 16 
   theta: 337.86 Dist: 00000.00 Q: 0 
   theta: 339.39 Dist: 02867.50 Q: 11 
   theta: 340.48 Dist: 03285.00 Q: 12 
   theta: 341.66 Dist: 02975.25 Q: 10 
   theta: 342.42 Dist: 00000.00 Q: 0 
   theta: 343.89 Dist: 03390.25 Q: 17 
   theta: 345.66 Dist: 01084.00 Q: 34 
   theta: 346.89 Dist: 01070.00 Q: 32 
   theta: 348.36 Dist: 00779.75 Q: 32 
   theta: 349.25 Dist: 00000.00 Q: 0 
   theta: 349.48 Dist: 00797.75 Q: 22 
   theta: 350.39 Dist: 00000.00 Q: 0 
   theta: 351.53 Dist: 00000.00 Q: 0 
   theta: 353.12 Dist: 03764.50 Q: 13 
   theta: 354.28 Dist: 03884.75 Q: 17 
   theta: 355.42 Dist: 03896.75 Q: 11 
   theta: 356.64 Dist: 02867.50 Q: 15 
   theta: 357.80 Dist: 02839.00 Q: 13 
S  theta: 358.38 Dist: 02815.75 Q: 13 
   theta: 359.52 Dist: 02841.25 Q: 13
```

# Python

```
curl -O https://bootstrap.pypa.io/get-pip.py
sudo python get-pip.py
sudo pip install pyserial
python LidarPrint2.py /dev/ttyUSB0
``` 


```
4224, 0
5250, 466
5506, 370
4994, 298
6018, 228
5762, 166
6274, 122
6786, 68
5506, 34
6017, 508
6273, 472
6273, 430
6017, 398
5761, 366
6529, 340
6273, 326
6785, 296
6529, 278
6017, 252
6273, 246
6529, 222
6529, 210
7041, 200
6529, 192
5761, 188
5761, 184
6529, 186
6529, 182
6017, 178
6529, 184
6273, 180
6529, 182
7297, 186
6273, 192
5761, 210
6785, 214
6529, 230
6529, 240
6273, 258
6273, 274
6017, 290
6017, 314
6529, 332
6529, 360
6017, 376
5761, 406
6017, 432
6017, 468
5761, 500
6274, 22
6018, 54
5762, 108
5762, 146
5506, 190
5506, 240
5506, 292
6018, 338
5506, 402
5506, 462
5506, 8
4994, 68
5762, 154
5506, 216
5250, 290
4994, 372
5250, 480
5506, 52
4482, 162
5250, 258
4994, 398
4738, 20
4738, 138
4738, 284
5506, 412
4483, 82
131, 266
128, 0
5504, 0
7816, 140
4488, 412
136, 140
8576, 0
7559, 332
7047, 152
8839, 30
5767, 316
6535, 198
8583, 38
9639, 444
2439, 356
6023, 150
9607, 158
2695, 112
2951, 494
2695, 412
3975, 402
9863, 260
9863, 282
8327, 188
3719, 162
4999, 76
10375, 80
5767, 72
9095, 0
9094, 482
9863, 26
8582, 474
9351, 24
9607, 4
7047, 12
5511, 30
10375, 78
6535, 84
9871, 176
3719, 172
5255, 246
4487, 324
10375, 332
4999, 374
2695, 486
7815, 148
5767, 88
3463, 244
8583, 330
8839, 438
7815, 36
8583, 176
3207, 304
5511, 456
7815, 146
5767, 198
6535, 370
7304, 100
136, 244
4480, 0
5512, 68
4232, 362
6280, 90
2952, 320
8328, 58
2440, 244
3209, 164
2441, 316
3977, 182
137, 492
4736, 0
2953, 326
7050, 166
6281, 498
7305, 156
7049, 272
7049, 482
7305, 198
7049, 426
8841, 166
7560, 452
7304, 206
7816, 506
8584, 296
8328, 80
8584, 360
8584, 172
8840, 500
8840, 422
9352, 228
8840, 58
9351, 446
9351, 312
9863, 180
9863, 56
10119, 486
10375, 382
10375, 288
9351, 208
9607, 172
9607, 52
9095, 10
9607, 490
9607, 376
9607, 356
10631, 338
10631, 276
10375, 260
10375, 250
10375, 256
10375, 246
10375, 252
10375, 228
10375, 254
10375, 260
9607, 314
9607, 338
9607, 404
9607, 442
9607, 500
9607, 38
9863, 104
9351, 186
11143, 248
9607, 316
9863, 394
9351, 480
9351, 62
9095, 174
9863, 276
9095, 460
8328, 74
8840, 242
8072, 406
9352, 42
9096, 194
7816, 378
7560, 78
7304, 231
8072, 455
6024, 179
6280, 197
7560, 219
5768, 291
6536, 369
6791, 485
7815, 73
7303, 285
8839, 439
7303, 91
8327, 323
8583, 61
8582, 299
9094, 49
9094, 329
8326, 79
8838, 365
6022, 165
7302, 105
4230, 501
4998, 257
5765, 189
6788, 267
4740, 381
5508, 489
5764, 489
5764, 95
6531, 269
```