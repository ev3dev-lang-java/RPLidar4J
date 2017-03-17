package ev3dev.sensors.slamtec.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Scan {

    private List<ScanDistance> distances = Collections.synchronizedList(new ArrayList<ScanDistance>());

    public Scan(final List<ScanDistance> distances) {
        this.distances = distances;
    }

    public List<ScanDistance> getDistances() {
        return distances;
    }
}
