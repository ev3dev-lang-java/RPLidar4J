package ev3dev.sensors.slamtec;

import ev3dev.sensors.slamtec.model.Scan;
import ev3dev.sensors.slamtec.model.ScanDistance;
import ev3dev.sensors.slamtec.service.*;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Slf4j class RPLidarA1Driver implements RPLidarProvider, RpLidarListener {

    private boolean closingStatus = false;

    private RpLidarLowLevelDriver driver;
    private final String USBPort;

    private List<ScanDistance> distancesTemp = Collections.synchronizedList(new ArrayList<>());

    public RPLidarA1Driver(final String USBPort) {
        this.USBPort = USBPort;
        if(log.isInfoEnabled()){
            log.info("Starting a RPLidarA1 instance");
        }
    }

    @Override
    public void init() throws RPLidarA1ServiceException {
        if(log.isInfoEnabled()){
            log.info("Connecting with: {}", this.USBPort);
        }
        File f = new File(this.USBPort);
        if(!f.exists() || f.isDirectory()) {
            log.error("This device is not valid: {}", this.USBPort);
            throw new RPLidarA1ServiceException("This device is not valid: " + this.USBPort);
        }

        try {
            driver = new RpLidarLowLevelDriver(this.USBPort, this);
        //TODO Improve this Exception handling
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
        driver.sendScan(300);
        driver.pause(700);
        final List<ScanDistance> distances = new ArrayList<>();
        distances.addAll(distancesTemp);
        distancesTemp.clear();
        distances.sort(Comparator.comparing(ScanDistance::getAngle));
        return new Scan(Collections.unmodifiableList(distances));
    }

    @Override
    public void close() throws RPLidarA1ServiceException {
        closingStatus = true;
        driver.shutdown();
        driver.pause(100);
    }

    @Override
    public void addListener(RPLidarProviderListener listener) {

    }

    @Override
    public void removeListener(RPLidarProviderListener listener) {

    }

    @Override
    public void handleMeasurement(RpLidarMeasurement measurement) {

        if(!this.closingStatus) {

            //TODO This conversion should be incorporated in RpLidarLowLevelDriver
            int angle = new Double(measurement.angle / 64.0).intValue();
            double distance = (measurement.distance / 4.0) / 10.0;

            if(!this.containAngle(distancesTemp, angle)){
                distancesTemp.add(new ScanDistance(angle, distance, measurement.quality, measurement.start));
            }
        }
    }

    public boolean containAngle(final List<ScanDistance> list, final int angle){
        return list.stream().filter(o -> o.getAngle() == angle).findFirst().isPresent();
    }

    //Not used at the moment

    @Override
    public void handleDeviceHealth(RpLidarHeath health) {

    }

    @Override
    public void handleDeviceInfo(RpLidarDeviceInfo info) {

    }
}
