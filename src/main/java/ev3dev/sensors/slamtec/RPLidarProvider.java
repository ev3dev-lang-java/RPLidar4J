package ev3dev.sensors.slamtec;

import ev3dev.sensors.slamtec.model.Scan;

public interface RPLidarProvider {

    int SCAN_DEGREES = 360;

    void init() throws RPLidarA1ServiceException;
    Scan scan() throws RPLidarA1ServiceException;
    void close() throws RPLidarA1ServiceException;
}
