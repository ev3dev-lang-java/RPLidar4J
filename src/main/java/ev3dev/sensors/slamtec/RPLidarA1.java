package ev3dev.sensors.slamtec;

import ev3dev.sensors.slamtec.model.Scan;

/**
 * RPLidarA1, is the entry point to use this library.
 *
 * This class provide the mechanism to manage a RPLidarA1
 */
public class RPLidarA1 implements RPLidarProvider
{

	private final RPLidarProvider rpLidarProvider;

	public RPLidarA1(final String USBPort)
	{
		this.rpLidarProvider = RPLidarA1Factory.getInstance(USBPort);
	}

	@Override
	public void init() throws RPLidarA1ServiceException, InterruptedException
	{
		rpLidarProvider.init();
	}

	@Override
	public Scan scan() throws RPLidarA1ServiceException, InterruptedException
	{
		return oneShotScan();
	}

	@Override
	public Scan oneShotScan() throws RPLidarA1ServiceException, InterruptedException
	{
		return rpLidarProvider.oneShotScan();
	}

	@Override
	public void close() throws RPLidarA1ServiceException
	{
		rpLidarProvider.close();
	}

	@Override
	public void addListener(RPLidarProviderListener listener)
	{
		rpLidarProvider.addListener(listener);
	}

	@Override
	public void removeListener(RPLidarProviderListener listener)
	{
		rpLidarProvider.removeListener(listener);
	}

	@Override
	public void continuousScanning() throws RPLidarA1ServiceException
	{
		rpLidarProvider.continuousScanning();

	}

	@Override
	public void stopScanning() throws RPLidarA1ServiceException
	{
		rpLidarProvider.stopScanning();

	}
}
