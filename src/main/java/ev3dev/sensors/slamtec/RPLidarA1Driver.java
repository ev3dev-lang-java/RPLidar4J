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
		} catch (Exception e)
		{
			throw new RPLidarA1ServiceException(e);
		}
		closingStatus = new AtomicBoolean(false);
		driver.setVerbose(false);
		driver.sendReset();

		driver.sendStartMotor(660);

		final CountDownLatch latch = new CountDownLatch(1);

		RPLidarProviderListener listener = new RPLidarProviderListener()
		{

			@Override
			public void scanFinished(Scan scan)
			{
			}

			@Override
			public void deviceInfo(RpLidarDeviceInfo info)
			{
				// by waiting for the device info we can be sure that the
				// RPLidar has reset
				latch.countDown();
			}
		};
		addListener(listener);

		driver.sendGetInfo();

		latch.await(5, TimeUnit.SECONDS);
		removeListener(listener);

	}

	@Override
	public Scan scan() throws RPLidarA1ServiceException, InterruptedException
	{
		return oneShotScan();
	}

	@Override
	public Scan oneShotScan() throws RPLidarA1ServiceException, InterruptedException
	{

		flag = false;
		distancesTemp.clear();
		driver.sendScan();

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

			@Override
			public void deviceInfo(RpLidarDeviceInfo info)
			{
			}
		};
		addListener(listener);

		latch.await(15, TimeUnit.SECONDS);
		removeListener(listener);
		return distances.get();

	}

	@Override
	public void continuousScanning() throws RPLidarA1ServiceException
	{
		flag = false;
		distancesTemp.clear();
		driver.sendScan();
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
		driver.sendStopMotor();
		driver.shutdown();
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
				float angle = new Float(measurement.angle / 64.0f);
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
		for (RPLidarProviderListener listener : listenerList)
		{
			listener.deviceInfo(info);
		}
	}

	@Override
	public Scan getNextScan() throws RPLidarA1ServiceException, InterruptedException
	{
		final CountDownLatch latch = new CountDownLatch(1);
		AtomicReference<Scan> ret = new AtomicReference<>();

		RPLidarProviderListener listener = new RPLidarProviderListener()
		{

			@Override
			public void scanFinished(Scan scan)
			{
				ret.set(scan);
				latch.countDown();
			}

			@Override
			public void deviceInfo(RpLidarDeviceInfo info)
			{

			}
		};
		addListener(listener);

		if (!latch.await(5, TimeUnit.SECONDS))
		{
			log.warn("Failed to get a scan");
		}
		removeListener(listener);
		return ret.get();
	}

}
