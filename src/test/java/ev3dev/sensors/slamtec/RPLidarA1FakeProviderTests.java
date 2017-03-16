package ev3dev.sensors.slamtec;

import lombok.extern.slf4j.Slf4j;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public  @Slf4j class RPLidarA1FakeProviderTests{

	@BeforeClass
	public static void runOnceBeforeClass() {
		System.setProperty("FAKE_RPLIDARA1", "true");
	}

	@Ignore
	@Test
	public void getDistanceStreamTest() throws Exception {
		
		final RPLidarA1 lidar = new RPLidarA1();
		lidar.init();
		lidar.scan().getDistances()
			.stream()
			.forEach(System.out::println);
		lidar.close();
	}

	@Test
	public void return360DistanceTest() throws Exception {

		final RPLidarA1 lidar = new RPLidarA1();
		lidar.init();
		final Scan scan = lidar.scan();
		lidar.close();

		assertThat(scan, is(notNullValue()));
		assertThat(scan.getDistances().size(), is(RPLidarProvider.SCAN_DEGREES));
	}
}