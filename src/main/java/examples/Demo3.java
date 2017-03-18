package examples;

import ev3dev.sensors.slamtec.service.*;
import lombok.extern.slf4j.Slf4j;

public @Slf4j class Demo3 implements RpLidarListener {

    int counter = 0;
    boolean flag = false;

    @Override
    public void handleMeasurement(RpLidarMeasurement measurement) {

        if(flag){
            if(measurement.start){
                log.info("{}", counter);
                counter= 0;
                flag=false;
            }
        }

        if (measurement.start) {
            flag = true;
        }

        if(flag){
            counter++;
        }

        double deg = measurement.angle / 64.0;
        double r = measurement.distance / 4.0;
        //log.info("demo");
        //log.info("{} {} {} {}", measurement.start, measurement.quality, deg, r);
    }

    @Override
    public void handleDeviceHealth(RpLidarHeath health) {
        health.print();
    }

    @Override
    public void handleDeviceInfo(RpLidarDeviceInfo info) {
        System.out.println("Got device info packet");
        info.print();
    }

    public static void main(String[] args) throws Exception {

        RpLidarLowLevelDriver driver = new RpLidarLowLevelDriver("/dev/ttyUSB0", new Demo3());
        driver.setVerbose(false);
        driver.sendReset();
        driver.pause(100);

        //service.sendGetInfo(1000);
        //service.sendGetHealth(1000);

        for(int x = 0; x < 15; x++ ){
            long startTime = System.currentTimeMillis();
            log.info("Iteration: {}", x);
            driver.sendReset();
            driver.pause(200);
            driver.sendScan(500);
            driver.pause(1000);
            long endTime   = System.currentTimeMillis();
            long totalTime = endTime - startTime;
            log.info("{}", totalTime);
        }

        //service.pause(100);
        driver.shutdown();
        driver.pause(100);
        System.exit(0);
    }
}