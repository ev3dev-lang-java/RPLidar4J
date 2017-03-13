package ev3dev.sensors.slamtec;

import org.junit.Test;

public class RPLidarA1Test{

	@Test
	public void testDummyScan() throws Exception{
		final RPLidarA1 lidar = new RPLidarA1();
		final Scan distances = lidar.scan();
	}

}