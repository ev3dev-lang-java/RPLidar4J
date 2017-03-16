package ev3dev.sensors.slamtec;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FakeRPLidarA1 implements RPLidarProvider{

    @Override
    public void init() throws LIDARServiceException {

    }

    @Override
    public Scan scan() throws LIDARServiceException {
        final List<ScanDistance> distances = Collections.synchronizedList(new ArrayList<>());
        for(int angle = 0; angle < 360; angle++){
            distances.add(new ScanDistance(angle,Math.random() * 4000 + 1));
        }
        return new Scan(distances);
    }

    @Override
    public void close() throws LIDARServiceException {

    }
}
