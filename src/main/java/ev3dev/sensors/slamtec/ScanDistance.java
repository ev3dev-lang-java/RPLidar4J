package ev3dev.sensors.slamtec;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

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
