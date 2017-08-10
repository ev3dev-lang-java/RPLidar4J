package examples;

import ev3dev.sensors.slamtec.RPLidarA1;
import ev3dev.sensors.slamtec.RPLidarA1Factory;
import lombok.extern.slf4j.Slf4j;

public @Slf4j class Demo8 {

    public static void main(String[] args) throws Exception {

        //Testing new driver
        System.setProperty(RPLidarA1Factory.RPLIDARA1_ENV_KEY, "EXPERIMENTAL");

        log.info("Testing RPLidar on a EV3Dev with Java");
        final String USBPort = "/dev/ttyUSB0";
        final RPLidarA1 lidar = new RPLidarA1(USBPort);
        lidar.init();

        for(int x = 0; x <= 10; x++) {
            lidar.scan();
        }

        lidar.close();
        log.info("End demo");
        System.exit(0);
    }
}
