package ev3dev.sensors.slamtec;

public class Scan {

    private final int[] distances;

    public Scan(int[] distances) {
        this.distances = distances;
    }

    public int[] getDistances() {
        return distances;
    }
}
