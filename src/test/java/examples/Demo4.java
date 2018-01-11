package examples;

import ev3dev.sensors.slamtec.RPLidarA1;
import ev3dev.sensors.slamtec.RPLidarA1ServiceException;
import ev3dev.sensors.slamtec.RPLidarProviderListener;
import ev3dev.sensors.slamtec.model.Scan;
import ev3dev.sensors.slamtec.service.RpLidarDeviceInfo;
import lombok.extern.slf4j.Slf4j;

public @Slf4j class Demo4
{

	private static volatile int samplesPerSecond;

	public static void main(String[] args) throws Exception
	{

		log.info("Testing RPLidar on a EV3Dev with Java");

		final String USBPort = "/dev/ttyUSB0";
		final RPLidarA1 lidar = new RPLidarA1(USBPort);
		lidar.init();

		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				System.out.println("Close Lidar");
				try
				{
					lidar.close();
				} catch (RPLidarA1ServiceException e)
				{
					e.printStackTrace();
				}
			}
		}));

		lidar.addListener(new RPLidarProviderListener()
		{
			@Override
			public void scanFinished(final Scan scan)
			{
				final int counter = scan.getDistances().size();
				// log.info("Measures: {}", counter);

				synchronized (this)
				{
					samplesPerSecond += counter;
				}
			}

			@Override
			public void deviceInfo(RpLidarDeviceInfo info)
			{
			}
		});

		int counter = 0;

		boolean flag = true;
		while (flag)
		{

			lidar.oneShotScan();

			counter++;
			log.info("Counter: {}, Samples: ;{}", counter, samplesPerSecond);
			samplesPerSecond = 0;

			if (counter > 500)
			{
				break;
				// log.info("RESET");
				// lidar.close();
				// lidar.init();
				// counter=0;
			}
		}

		lidar.close();
		log.info("End");
		System.exit(0);
	}

}
