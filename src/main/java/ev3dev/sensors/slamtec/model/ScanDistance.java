package ev3dev.sensors.slamtec.model;

import lombok.*;

@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class ScanDistance {

    @NonNull
    @Getter
    private final int angle;

    @NonNull
    @Getter
    private final double distance;
}
