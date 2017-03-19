package ev3dev.sensors.slamtec;

import ev3dev.sensors.slamtec.model.Scan;
import lombok.extern.slf4j.Slf4j;

public @Slf4j class RPLidarA1 implements RPLidarProvider {

    private final RPLidarProvider rpLidarProvider;
    private final String USBPort;

    public RPLidarA1(final String USBPort) {
        this.rpLidarProvider = RPLidarA1Factory.getInstance(USBPort);
        this.USBPort = USBPort;
    }

    @Override
    public void init() throws RPLidarA1ServiceException {
        rpLidarProvider.init();
    }

    @Override
    public Scan scan() throws RPLidarA1ServiceException {
        return rpLidarProvider.scan();
    }

    @Override
    public void close() throws RPLidarA1ServiceException {
        rpLidarProvider.close();
    }
}
