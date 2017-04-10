package ev3dev.sensors.slamtec;

import ev3dev.sensors.slamtec.model.Scan;
import lombok.extern.slf4j.Slf4j;
import org.junit.BeforeClass;
import org.junit.Test;

public @Slf4j class RPLidarA1FakeEventsTests implements RPLidarProviderListener {

	@BeforeClass
	public static void runOnceBeforeClass() {
		System.setProperty("FAKE_RPLIDARA1", "true");
	}

	@Test
	public void lidarEventTest() throws Exception {

		final String USBPort = "ttyUSB0";
		final RPLidarA1 lidar = new RPLidarA1(USBPort);
        lidar.addListener(this);
		lidar.init();
        Thread.sleep(2000);
        lidar.close();
        log.info("End");
        System.exit(0);
	}

    @Override
    public Scan scanFinished(final Scan scan) {
        final long counter = scan.getDistances()
            .stream()
            .count();
		log.info("Measures: {}", counter);
        return scan;
    }
}