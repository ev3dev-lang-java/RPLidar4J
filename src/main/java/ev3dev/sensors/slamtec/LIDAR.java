package ev3dev.sensors.slamtec;

public interface LIDAR {

    void init() throws LIDARServiceException;
    Scan scan() throws LIDARServiceException;
    void close() throws LIDARServiceException;
}
