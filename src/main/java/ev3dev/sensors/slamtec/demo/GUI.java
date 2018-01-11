package ev3dev.sensors.slamtec.demo;

import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JFrame;

import ev3dev.sensors.slamtec.RPLidarA1ServiceException;

public class GUI extends JFrame implements Runnable
{

	private static final long serialVersionUID = -4490943128993707547L;

	private LidarMapRenderer graph;

	static public void main(String[] args) throws RPLidarA1ServiceException, InterruptedException
	{
		new GUI();

	}

	public GUI() throws RPLidarA1ServiceException, InterruptedException
	{
		this.setBounds(0, 0, 850, 900);

		FlowLayout experimentLayout = new FlowLayout();

		this.setLayout(experimentLayout);

		graph = new LidarMapRenderer();
		graph.setPreferredSize(new Dimension(750, 750));
		this.add(graph);

		setSize(700, 700);
		setLocation(200, 200);
		setVisible(true);

		new Thread(this, "ui").start();

		Thread.sleep(100000);

	}

	@Override
	public void run()
	{
		while (true)
		{
			this.repaint();

			try
			{
				Thread.sleep(50);
			} catch (InterruptedException e)
			{
				throw new RuntimeException("Exiting");
			}
		}
	}

}
