package ev3dev.sensors.slamtec;

import ev3dev.sensors.slamtec.model.Scan;
import ev3dev.sensors.slamtec.model.ScanDistance;
import ev3dev.sensors.slamtec.service.*;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j class RPLidarA1Driver implements RPLidarProvider, RpLidarListener {

    private boolean closingStatus = false;

    private RpLidarLowLevelDriver driver;
    private final String USBPort;

    private List<ScanDistance> distancesTemp = Collections.synchronizedList(new ArrayList<>());

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
        driver.sendScan(300);
        driver.pause(700);
        long endTime   = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        log.trace("Time consumed: {}", totalTime);

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
    public void handleMeasurement(RpLidarMeasurement measurement) {

        if(!this.closingStatus) {

            int angle = new Double(measurement.angle / 64.0).intValue();
            double distance = measurement.distance / 4.0;

            if(!this.containAngle(distancesTemp, angle)){
                distancesTemp.add(new ScanDistance(angle, distance));
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
