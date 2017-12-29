package ev3dev.sensors.slamtec;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.lessThanOrEqualTo;

import org.junit.BeforeClass;
import org.junit.Test;

import ev3dev.sensors.slamtec.model.Scan;

public class RPLidarA1FakeTests
{

	@BeforeClass
	public static void runOnceBeforeClass()
	{
		System.setProperty("FAKE_RPLIDARA1", "true");
	}

	@Test
	public void getDistanceStreamTest() throws Exception
	{

		final String USBPort = "ttyUSB0";
		final RPLidarA1 lidar = new RPLidarA1(USBPort);
		lidar.init();
		lidar.oneShotScan().getDistances().stream().forEach(System.out::println);
		lidar.close();
	}

	@Test
	public void return360DistanceTest() throws Exception
	{

		final String USBPort = "ttyUSB0";
		final RPLidarA1 lidar = new RPLidarA1(USBPort);
		lidar.init();
		final Scan scan = lidar.oneShotScan();
		lidar.close();

		assertThat(scan, is(notNullValue()));
		assertThat(scan.getDistances().size(), is(lessThanOrEqualTo(RPLidarProvider.SCAN_DEGREES)));
	}

}