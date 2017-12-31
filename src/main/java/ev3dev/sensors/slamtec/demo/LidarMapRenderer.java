package ev3dev.sensors.slamtec.demo;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.JPanel;

import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.RotationOrder;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import ev3dev.sensors.slamtec.RPLidarA1;
import ev3dev.sensors.slamtec.RPLidarA1ServiceException;
import ev3dev.sensors.slamtec.RPLidarProviderListener;
import ev3dev.sensors.slamtec.model.Scan;
import ev3dev.sensors.slamtec.model.ScanDistance;
import ev3dev.sensors.slamtec.service.RpLidarDeviceInfo;
import lombok.extern.slf4j.Slf4j;

public @Slf4j class LidarMapRenderer extends JPanel
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	AtomicReference<BufferedImage> currentImage = new AtomicReference<>();

	LidarMapRenderer() throws RPLidarA1ServiceException, InterruptedException
	{
		log.info("Testing RPLidar on a EV3Dev with Java");
		final String USBPort = "/dev/ttyUSB0";
		final RPLidarA1 lidar = new RPLidarA1(USBPort);
		lidar.init();

		lidar.continuousScanning();

		lidar.addListener(new RPLidarProviderListener()
		{

			@Override
			public void scanFinished(Scan scan)
			{
				render(scan);

			}

			@Override
			public void deviceInfo(RpLidarDeviceInfo info)
			{
			}
		});

	}

	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.drawImage(currentImage.get(), 0, 0, this);

	}

	public void render(Scan scan)
	{

		BufferedImage image = new BufferedImage(700, 700, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = image.createGraphics();
		g2.setColor(new Color(255, 255, 255));

		for (ScanDistance measurement : scan.getDistances())
		{

			double angle = measurement.getAngle();
			double distance = measurement.getDistance();

			Rotation rotation = new Rotation(RotationOrder.XYZ, 0, 0, Math.toRadians(angle));
			Vector3D vector = new Vector3D(0, distance, 0);

			Vector3D result = rotation.applyTo(vector);

			image.getGraphics().draw3DRect((int) result.getX() + 350, (int) result.getY() + 350, 1, 1, false);

		}

		currentImage.set(image);

	}

}
