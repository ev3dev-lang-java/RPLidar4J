package ev3dev.sensors.slamtec;

public interface RPLidarProvider {

    int SCAN_DEGREES = 360;

    void init() throws LIDARServiceException;
    Scan scan() throws LIDARServiceException;
    void close() throws LIDARServiceException;
}
