package examples;

import ev3dev.sensors.slamtec.RPLidarA1;
import ev3dev.sensors.slamtec.RPLidarA1Factory;
import ev3dev.sensors.slamtec.RPLidarProviderListener;
import ev3dev.sensors.slamtec.model.Scan;
import ev3dev.sensors.slamtec.model.ScanDistance;
import lombok.extern.slf4j.Slf4j;

public @Slf4j class Demo8 {

    private static volatile int samplesPerSecond;

    public static void main(String[] args) throws Exception {

        //Testing new driver
        System.setProperty(RPLidarA1Factory.RPLIDARA1_ENV_KEY, "EXPERIMENTAL");

        log.info("Testing RPLidar on a EV3Dev with Java");
        final String USBPort = "/dev/ttyUSB0";
        final RPLidarA1 lidar = new RPLidarA1(USBPort);
        lidar.init();

        lidar.addListener(new RPLidarProviderListener() {
            @Override
            public void scanFinished(final Scan scan) {
                final int counter = scan.getDistances().size();

                log.info("{}",counter);

                /*
                for (ScanDistance scanDistance: scan.getDistances()) {
                    log.info("Angle: {}, Distance: {}, Quality: {}", scanDistance.getAngle()/64f, scanDistance.getDistance(), scanDistance.getQuality());
                }*/

                /*
                synchronized (this) {
                    samplesPerSecond += counter;
                }
                */
            }
        });

        int counter = 0;

        boolean flag = true;
        while(flag){

            lidar.scan();

            counter++;
            //log.info("Counter: {}, Samples: ;{}, {}", counter, samplesPerSecond, scan.getDistances().size());
            log.info("Counter: {}", counter);
            //samplesPerSecond = 0;

            if(counter > 50){
                break;
            }
        }

        lidar.close();
        log.info("End demo");
        System.exit(0);
    }
}
