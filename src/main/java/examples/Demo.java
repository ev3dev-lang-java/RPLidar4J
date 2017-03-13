package examples;

import ev3dev.sensors.slamtec.RPLidarA1;
import lombok.extern.slf4j.Slf4j;

public @Slf4j class Demo {

    public static void main(String[] args) {
        RPLidarA1 lidar = new RPLidarA1();
        lidar.scan();
    }

}
