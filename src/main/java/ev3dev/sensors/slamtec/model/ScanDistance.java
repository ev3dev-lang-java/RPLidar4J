package ev3dev.sensors.slamtec.model;

import lombok.Value;

@Value
public class ScanDistance {

    private final int angle;
    private final double distance;
}
