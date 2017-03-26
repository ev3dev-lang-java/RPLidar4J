package ev3dev.sensors.slamtec;

import ev3dev.sensors.slamtec.model.Scan;
import ev3dev.sensors.slamtec.model.ScanDistance;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j class RPLidarA1Fake implements RPLidarProvider{

    public RPLidarA1Fake(final String USBPort){
        log.trace("Starting a Fake RPLidarA1 Object");
    }

    @Override
    public void init() throws RPLidarA1ServiceException {

    }

    @Override
    public Scan scan() throws RPLidarA1ServiceException {
        final List<ScanDistance> distances = Collections.synchronizedList(new ArrayList<>());
        for(int angle = 0; angle < 360; angle++){
            distances.add(new ScanDistance(angle,Math.random() * 4000 + 1, 1, false));
        }
        return new Scan(distances);
    }

    @Override
    public void close() throws RPLidarA1ServiceException {

    }
}
