package ev3dev.sensors.slamtec;

import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

public  @Slf4j class RPLidarFactory {

    private static final String RPLIDARA1_ENV_KEY = "FAKE_RPLIDARA1";

    public static RPLidarProvider getInstance() {

        final boolean fakeProvider = (Objects.nonNull(System.getProperty(RPLIDARA1_ENV_KEY))) ? true : false;
        if(fakeProvider){
            log.trace("Returning a Fake RPLidarA1 Object");
            return new FakeRPLidarA1();
        }else {
            log.trace("Returning a RPLidarA1 Object");
            return new RPLidarA1Monitor();
        }
    }

}
