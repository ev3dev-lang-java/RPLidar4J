package ev3dev.sensors.slamtec;

import ev3dev.sensors.slamtec.model.Scan;
import ev3dev.sensors.slamtec.model.ScanDistance;
import ev3dev.sensors.slamtec.service.*;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j class RPLidarA1Driver2 extends RpLidarHighLevelDriver implements RPLidarProvider, RpLidarListener {

    private boolean initSuccess = false;
    private AtomicBoolean closingStatus;

    //private RpLidarHighLevelDriver driver = null;
    private final String USBPort;

    private int counter = 0;
    private boolean flag = false;
    private List<ScanDistance> distancesTemp = Collections.synchronizedList(new ArrayList<>());
    private Scan scan;

    private final List<RPLidarProviderListener> listenerList = Collections.synchronizedList(new ArrayList());

    public RPLidarA1Driver2(final String USBPort) {
        this.USBPort = USBPort;
        this.closingStatus = new AtomicBoolean(false);
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
            System.setProperty("gnu.io.rxtx.SerialPorts", this.USBPort);
            initSuccess = this.initialize(this.USBPort, 100);
            //Let's just pretend it worked...
            initSuccess = true;
        //TODO Improve this Exception handling
        } catch (Exception e) {
            throw new RPLidarA1ServiceException(e);
        }
    }

    @Override
    public Scan scan() throws RPLidarA1ServiceException {

        //int scanCount = 0;
        if(initSuccess) {
            RpLidarScan scan = new RpLidarScan();
            if (!this.blockCollectScan(scan, 0)) { //10000
                System.out.println("Scan wasn't ready yet");
            } else {

                //scan.convertMilliMeters(mm);

            }
        }else {
            //Lidar not initialized
        }

        final List<ScanDistance> distances = new ArrayList<>();
        synchronized(distancesTemp){
            distances.addAll(distancesTemp);
            distancesTemp.clear();
        }
        distances.sort(Comparator.comparing(ScanDistance::getAngle));
        return new Scan(Collections.unmodifiableList(distances));
    }

    @Override
    public void close() throws RPLidarA1ServiceException {
        closingStatus = new AtomicBoolean(true);
        this.stop();
        initSuccess = false;
    }

    @Override
    public void addListener(RPLidarProviderListener listener) {
        listenerList.add(listener);
    }

    @Override
    public void removeListener(RPLidarProviderListener listener) {
        listenerList.remove(listener);
    }

    @Override
    public void handleMeasurement(final RpLidarMeasurement measurement) {

        if(!closingStatus.get()){

            if(flag){
                if(measurement.start){
                    log.trace("{}", counter);
                    synchronized (distancesTemp) {
                        final List<ScanDistance> distances = new ArrayList<>();
                        distances.addAll(distancesTemp);
                        distancesTemp.clear();
                        scan = new Scan(distances);

                        for (RPLidarProviderListener listener : listenerList) {
                            listener.scanFinished(new Scan(distances));
                        }

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
                int angle = new Float(measurement.angle / 64.0f).intValue();
                float distance = (measurement.distance / 4.0f) / 10.0f;
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
