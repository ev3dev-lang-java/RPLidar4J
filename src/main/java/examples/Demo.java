package examples;

import ev3dev.sensors.slamtec.RPLidarA1;
import ev3dev.sensors.slamtec.model.Scan;
import lombok.extern.slf4j.Slf4j;

public @Slf4j class Demo {

    public static void main(String[] args) throws Exception {

        final String USBPort = "ttyUSB0";
        final RPLidarA1 lidar = new RPLidarA1(USBPort);
        lidar.init();

        for(int x = 0; x <= 5; x++){
            final long counter = lidar.scan().getDistances()
                    .stream()
                    .count();
            log.info("Measures: {}", counter);
        }

        lidar.close();
        log.info("End demo");
        System.exit(0);
    }
}
