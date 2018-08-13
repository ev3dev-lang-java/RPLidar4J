package ev3dev.sensors.slamtec;

import java.util.Objects;

class RPLidarA1Factory
{

	private static final String RPLIDARA1_ENV_KEY = "FAKE_RPLIDARA1";

	public static RPLidarProvider getInstance(final String USBPort)
	{

		if (Objects.nonNull(System.getProperty(RPLIDARA1_ENV_KEY)))
		{
			return new RPLidarA1Fake(USBPort);
		}

		return new RPLidarA1Driver(USBPort);
	}
}
