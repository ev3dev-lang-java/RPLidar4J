package examples;

import java.util.concurrent.CountDownLatch;

import ev3dev.sensors.slamtec.RPLidarA1;
import ev3dev.sensors.slamtec.RPLidarProviderListener;
import ev3dev.sensors.slamtec.model.Scan;
import ev3dev.sensors.slamtec.service.RpLidarDeviceInfo;
import lombok.extern.slf4j.Slf4j;

public @Slf4j class Continous
{

	public static void main(String[] args) throws Exception
	{

		log.info("Testing RPLidar on a EV3Dev with Java");
		final String USBPort = "/dev/ttyUSB0";
		final RPLidarA1 lidar = new RPLidarA1(USBPort);
		lidar.init();

		final CountDownLatch latch = new CountDownLatch(30);

		lidar.continuousScanning();

		lidar.addListener(new RPLidarProviderListener()
		{

			@Override
			public void scanFinished(Scan scan)
			{
				final long counter = scan.getDistances().stream().count();
				log.info(" Measures: {}", counter);
				latch.countDown();

			}

			@Override
			public void deviceInfo(RpLidarDeviceInfo info)
			{
			}
		});

		latch.await();

		lidar.close();
		log.info("End demo");
		System.exit(0);
	}
}
