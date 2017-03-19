package ev3dev.sensors.slamtec;

import ev3dev.sensors.slamtec.model.Scan;
import ev3dev.sensors.slamtec.model.ScanDistance;
import ev3dev.sensors.slamtec.service.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j class RPLidarA1Driver implements RPLidarProvider, RpLidarListener {

    private boolean closingStatus = false;

    private RpLidarLowLevelDriver driver;
    private final String USBPort;

    private int counter = 0;
    private boolean flag = false;
    private List<ScanDistance> distancesTemp = Collections.synchronizedList(new ArrayList<>());

    private Scan scan;

    public RPLidarA1Driver(final String USBPort) {
        this.USBPort = USBPort;
        log.trace("Starting a RPLidarA1 instance");
    }

    @Override
    public void init() throws RPLidarA1ServiceException {
        try {
            driver = new RpLidarLowLevelDriver(this.USBPort, this);
        } catch (Exception e) {
            throw new RPLidarA1ServiceException(e);
        }
        closingStatus = false;
        driver.setVerbose(false);
        driver.sendReset();
        driver.pause(200);
    }

    @Override
    public Scan scan() throws RPLidarA1ServiceException {
        long startTime = System.currentTimeMillis();
        driver.sendScan(400);
        driver.pause(800);
        long endTime   = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        log.trace("Time consumed: {}", totalTime);

        if(scan != null){
            return scan;
        }

        return new Scan();
    }

    @Override
    public void close() throws RPLidarA1ServiceException {
        closingStatus = true;
        driver.shutdown();
        driver.pause(100);
    }

    @Override
    public void handleMeasurement(RpLidarMeasurement measurement) {

        if(!this.closingStatus){

            if(flag){
                if(measurement.start){
                    log.trace("{}", counter);
                    synchronized (distancesTemp) {
                        final List<ScanDistance> distances = new ArrayList<>();
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
            }

        //}else {
            //log.info("Event received when driver is clossing");
        }

    }

    @Override
    public void handleDeviceHealth(RpLidarHeath health) {

    }

    @Override
    public void handleDeviceInfo(RpLidarDeviceInfo info) {

    }
}
