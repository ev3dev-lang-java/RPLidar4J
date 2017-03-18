package ev3dev.sensors.slamtec;

import ev3dev.sensors.slamtec.model.Scan;
import ev3dev.sensors.slamtec.model.ScanDistance;
import ev3dev.sensors.slamtec.utils.JarResource;
import ev3dev.sensors.slamtec.utils.ProcessManager;
import lombok.extern.slf4j.Slf4j;
import roboticinception.rplidar.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j class RPLidarA1Driver implements RPLidarProvider, RpLidarListener {

    int counter = 0;
    boolean flag = false;
    RpLidarLowLevelDriver driver;
    private List<ScanDistance> distancesTemp = Collections.synchronizedList(new ArrayList<>());
    private List<ScanDistance> distances = Collections.synchronizedList(new ArrayList<>());
    private Scan scan;
    public RPLidarA1Driver() {
        log.trace("Returning a RPLidarA1 Object");
    }

    @Override
    public void init() throws RPLidarA1ServiceException {
        try {
            driver = new RpLidarLowLevelDriver("/dev/ttyUSB0", this);
        } catch (Exception e) {
            throw new RPLidarA1ServiceException(e);
        }
        driver.setVerbose(false);
        driver.sendReset();
        driver.pause(100);
    }

    @Override
    public Scan scan() throws RPLidarA1ServiceException {
        long startTime = System.currentTimeMillis();
        driver.sendReset();
        driver.pause(200);
        driver.sendScan(500);
        driver.pause(1000);
        long endTime   = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        log.info("{}", totalTime);

        if(scan != null){
            return scan;
        }

        return new Scan();
    }

    @Override
    public void close() throws RPLidarA1ServiceException {
        driver.pause(100);
        driver.shutdown();
        driver.pause(100);
    }

    @Override
    public void handleMeasurement(RpLidarMeasurement measurement) {

        if(flag){
            if(measurement.start){
                log.info("{}", counter);
                synchronized (distancesTemp) {
                    distances.clear();
                    distances.addAll(distancesTemp);
                    distancesTemp.clear();
                    scan = new Scan(distances);
                }
                counter= 0;
                flag=false;
            }
        }

        if (measurement.start) {
            flag = true;
        }

        if(flag){
            counter++;
            double deg = measurement.angle / 64.0;
            double r = measurement.distance / 4.0;
            distancesTemp.add(new ScanDistance((int) Math.round(deg), r));
            //log.info("demo");
            //log.info("{} {} {} {}", measurement.start, measurement.quality, Math.round(deg), r);
        }

    }

    @Override
    public void handleDeviceHealth(RpLidarHeath health) {

    }

    @Override
    public void handleDeviceInfo(RpLidarDeviceInfo info) {

    }
}
