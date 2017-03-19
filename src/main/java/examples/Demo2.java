package examples;

import ev3dev.sensors.slamtec.RPLidarA1;
import lombok.extern.slf4j.Slf4j;

public @Slf4j class Demo2 {

    public static void main(String[] args) throws Exception {

        log.info("Testing RPLidar on a EV3Dev with Java");
        final String USBPort = "/dev/ttyUSB0";
        final RPLidarA1 lidar = new RPLidarA1(USBPort);
        lidar.init();

        for(int x = 0; x <= 2; x++){
            lidar.scan().getDistances()
                    .stream()
                    .forEach(System.out::println);
        }

        lidar.close();
        log.info("End demo");
        System.exit(0);
    }
}
