package ev3dev.sensors.slamtec;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import ev3dev.sensors.slamtec.model.Scan;
import ev3dev.sensors.slamtec.model.ScanDistance;
import ev3dev.sensors.slamtec.service.RpLidarDeviceInfo;
import ev3dev.sensors.slamtec.service.RpLidarHeath;
import ev3dev.sensors.slamtec.service.RpLidarListener;
import ev3dev.sensors.slamtec.service.RpLidarLowLevelDriver;
import ev3dev.sensors.slamtec.service.RpLidarMeasurement;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class RPLidarA1Driver implements RPLidarProvider, RpLidarListener
{

	private AtomicBoolean closingStatus;

	private RpLidarLowLevelDriver driver;
	private final String USBPort;

	private volatile boolean flag = false;
	private final List<ScanDistance> distancesTemp = Collections.synchronizedList(new ArrayList<>());

	private final List<RPLidarProviderListener> listenerList = new CopyOnWriteArrayList<>();

	public RPLidarA1Driver(final String USBPort)
	{
		this.USBPort = USBPort;
		this.closingStatus = new AtomicBoolean(false);
		if (log.isInfoEnabled())
		{
			log.info("Starting a RPLidarA1 instance");
		}
	}

	@Override
	public void init() throws RPLidarA1ServiceException, InterruptedException
	{

		if (log.isInfoEnabled())
		{
			log.info("Connecting with: {}", this.USBPort);
		}
		File f = new File(this.USBPort);
		if (!f.exists() || f.isDirectory())
		{
			log.error("This device is not valid: {}", this.USBPort);
			throw new RPLidarA1ServiceException("This device is not valid: " + this.USBPort);
		}

		try
		{
			driver = new RpLidarLowLevelDriver(this.USBPort, this);
			// TODO Improve this Exception handling
		} catch (Exception e)
		{
			throw new RPLidarA1ServiceException(e);
		}
		closingStatus = new AtomicBoolean(false);
		driver.setVerbose(false);
		driver.sendReset();

		// for v2 only - I guess this command is ignored by v1
		// driver.sendStartMotor(660);

		driver.pause(200);
	}

	@Override
	public Scan scan() throws RPLidarA1ServiceException, InterruptedException
	{

		flag = false;
		distancesTemp.clear();
		driver.sendScanA1();

		// the first scan is always incomplete, so wait for 2 scans
		final CountDownLatch latch = new CountDownLatch(2);

		final AtomicReference<Scan> distances = new AtomicReference<>();

		RPLidarProviderListener listener = new RPLidarProviderListener()
		{

			@Override
			public void scanFinished(Scan scan)
			{

				distances.set(scan);

				latch.countDown();

			}
		};
		addListener(listener);

		latch.await(15, TimeUnit.SECONDS);
		removeListener(listener);
		return distances.get();

	}

	@Override
	public void continousScan() throws RPLidarA1ServiceException
	{
		flag = false;
		distancesTemp.clear();
		driver.sendScanA1();
		log.warn("Initiated continous scanning");
	}

	@Override
	public void stopScanning() throws RPLidarA1ServiceException
	{
		driver.sendStop();
		log.warn("Initiated continous scanning");
	}

	@Override
	public void close() throws RPLidarA1ServiceException
	{
		closingStatus = new AtomicBoolean(true);
		// driver.sendStopMotor();
		driver.shutdown();
		driver.pause(100);
	}

	@Override
	public void addListener(RPLidarProviderListener listener)
	{
		listenerList.add(listener);
	}

	@Override
	public void removeListener(RPLidarProviderListener listener)
	{
		listenerList.remove(listener);
	}

	@Override
	public void handleMeasurement(final RpLidarMeasurement measurement)
	{

		if (!closingStatus.get())
		{

			if (flag)
			{
				if (measurement.start)
				{
					synchronized (distancesTemp)
					{
						final List<ScanDistance> distances = new ArrayList<>();
						distances.addAll(distancesTemp);
						distancesTemp.clear();

						for (RPLidarProviderListener listener : listenerList)
						{
							listener.scanFinished(new Scan(distances));
						}

					}

					flag = false;
				}
			}

			if (measurement.start)
			{
				flag = true;
			}

			if (flag)
			{
				int angle = new Float(measurement.angle / 64.0f).intValue();
				float distance = (measurement.distance / 4.0f) / 10.0f;
				distancesTemp.add(new ScanDistance(angle, distance, measurement.quality, measurement.start));
			}
		}

	}

	// Not used at the moment

	@Override
	public void handleDeviceHealth(RpLidarHeath health)
	{

	}

	@Override
	public void handleDeviceInfo(RpLidarDeviceInfo info)
	{

	}

}
