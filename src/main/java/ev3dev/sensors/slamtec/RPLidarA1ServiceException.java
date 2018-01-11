package ev3dev.sensors.slamtec;

public class RPLidarA1ServiceException extends Exception
{

	private static final long serialVersionUID = 1L;

	public RPLidarA1ServiceException(Exception e)
	{
		super(e);
	}

	public RPLidarA1ServiceException(String message)
	{
		super(message);
	}
}
