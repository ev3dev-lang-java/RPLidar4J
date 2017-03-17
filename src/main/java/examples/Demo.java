package examples;

import ev3dev.sensors.slamtec.RPLidarA1;
import lombok.extern.slf4j.Slf4j;

public @Slf4j class Demo {

    public static void main(String[] args) throws Exception {

        final String USBPort = "ttyUSB0";
        final RPLidarA1 lidar = new RPLidarA1(USBPort);
        lidar.init();
        lidar.scan();
        lidar.scan();
        lidar.close();

    }
}
