package ev3dev.sensors.slamtec;

import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j class RPLidarA1Factory {

    private static final String RPLIDARA1_ENV_KEY = "FAKE_RPLIDARA1";

    public static RPLidarProvider getInstance(final String USBPort) {

        if(Objects.nonNull(System.getProperty(RPLIDARA1_ENV_KEY))){
            return new RPLidarA1Fake(USBPort);
        }

        return new RPLidarA1Driver(USBPort);
    }
}
