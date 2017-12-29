package ev3dev.sensors.slamtec.service;

import java.util.concurrent.TimeUnit;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import jssc.SerialPortList;
import lombok.extern.slf4j.Slf4j;

/**
 * Low level service for RPLidar. Just sends and receives packets. Doesn't
 * attempt to filter bad data or care about timeouts.
 *
 * @author Peter Abeles
 */
public @Slf4j class RpLidarLowLevelDriver implements SerialPortEventListener
{

	// out going packet types
	public static final byte SYNC_BYTE0 = (byte) 0xA5;
	public static final byte SYNC_BYTE1 = (byte) 0x5A;
	public static final byte STOP = (byte) 0x25;
	public static final byte RESET = (byte) 0x40;
	public static final byte SCAN = (byte) 0x20;
	public static final byte FORCE_SCAN = (byte) 0x21;
	public static final byte GET_INFO = (byte) 0x50;
	public static final byte GET_HEALTH = (byte) 0x52;

	// in coming packet types
	public static final byte RCV_INFO = (byte) 0x04;
	public static final byte RCV_HEALTH = (byte) 0x06;
	public static final byte RCV_SCAN = (byte) 0x81;
	private static final byte START_MOTOR = (byte) 0xF0;

	// buffer for out going data
	byte[] dataOut = new byte[1024];

	// flag to turn on and off verbose debugging output
	boolean verbose = true;

	// Storage for incoming packets
	RpLidarHeath health = new RpLidarHeath();
	RpLidarDeviceInfo deviceInfo = new RpLidarDeviceInfo();
	RpLidarMeasurement measurement = new RpLidarMeasurement();
	RpLidarListener listener;

	// if it is in scanning mode. When in scanning mode it just parses
	// measurement packets
	boolean scanning = false;

	// Type of packet last recieved
	int lastReceived = 0;
	private SerialPort serialPort;
	private String portName;
	private boolean connected;

	/**
	 * Initializes serial connection
	 *
	 * @param portName
	 *            Path to serial port
	 * @param listener
	 *            Listener for in comming packets
	 * @throws Exception
	 */
	public RpLidarLowLevelDriver(final String portName, final RpLidarListener listener) throws Exception
	{

		log.info("Opening port " + portName);

		this.portName = portName;

		this.listener = listener;

		if (portExists(portName))
		{
			boolean success;
			serialPort = new SerialPort(portName);
			try
			{
				success = serialPort.openPort();
				if (success)
				{

					serialPort.setParams(SerialPort.BAUDRATE_115200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
							SerialPort.PARITY_NONE);

					serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);

					// Listen for incoming data
					serialPort.addEventListener(this);
					serialPort.setDTR(false);

					log.info("Successfully opened serial port.");
				} else
				{
					log.error("Error opening serial port");
					serialPort = null;
					throw new RuntimeException("Error opening serial port");
				}

			} catch (SerialPortException ex)
			{
				log.error("Error opening serial port, error: " + ex.getMessage());
				serialPort = null;
				throw new RuntimeException(ex);
			}

		} else
		{
			log.error("Port: '" + portName + "' does not exist.");
			throw new RuntimeException("Port: '" + portName + "' does not exist.");
		}

		connected = true;

	}

	private boolean portExists(String portId)
	{
		// Check if requested port exists
		for (String portName : portList())
		{
			if (portName.equals(portId))
				return true;
		}
		return false;
	}

	/**
	 * Get the list of available serial ports.
	 * 
	 * @return String[] of serial ports.
	 */
	public String[] portList()
	{
		String[] portNames = SerialPortList.getPortNames();
		if (portNames.length == 0)
		{
			log.error("No serial ports found!");
		}
		return portNames;
	}

	/**
	 * Pauses for the specified number of milliseconds
	 */
	public void pause(long milli)
	{
		synchronized (this)
		{
			try
			{
				TimeUnit.MILLISECONDS.sleep(milli);
			} catch (InterruptedException e)
			{
			}
		}
	}

	/**
	 * Shuts down the serial connection and threads
	 */
	public void shutdown()
	{

		try
		{
			if (serialPort != null)
				serialPort.closePort();
			log.info("Closing serial port: '" + portName + "'");
		} catch (SerialPortException ex)
		{
			log.error("Failed to close serial port: '" + portName + "', error: " + ex.getMessage());
		}
		connected = false;
		serialPort = null;

	}

	/**
	 * Request that it enter scan mode
	 *
	 * @param timeout
	 *            Blocking time. Resends packet periodically. <= 0 means no
	 *            blocking.
	 * @return true if successful
	 */
	public void sendScan()
	{
		sendNoPayLoad(SCAN);
	}

	public void sendScanA1()
	{
		sendNoPayLoad(SCAN);
		// scanning = true;
	}

	/**
	 * Sends a STOP packet
	 */
	public void sendStop()
	{
		scanning = false;
		sendNoPayLoad(STOP);
	}

	/**
	 * Sends a reset packet which will put it into its initial state
	 * 
	 * @throws InterruptedException
	 */
	public void sendReset() throws InterruptedException
	{
		log.warn("Resetting RPLidar");
		scanning = false;
		sendNoPayLoad(RESET);
		TimeUnit.MILLISECONDS.sleep(1500);
	}

	/**
	 * Requests that a sensor info packet be sent
	 *
	 * @param timeout
	 *            Blocking time. Resends packet periodically. <= 0 means no
	 *            blocking.
	 * @return true if successful
	 */
	public void sendGetInfo()
	{
		sendNoPayLoad(GET_INFO);
	}

	/**
	 * Requests that a sensor health packet be sent
	 *
	 * @param timeout
	 *            Blocking time. Resends packet periodically. <= 0 means no
	 *            blocking.
	 * @return true if successful
	 */
	public void sendGetHealth()
	{

		sendNoPayLoad(GET_HEALTH);

	}

	/**
	 * Sends a command with no data payload
	 */
	private void sendNoPayLoad(byte command)
	{
		if (verbose)
		{
			log.debug("Sending command 0x%02x\n", command & 0xFF);
		}

		dataOut[0] = SYNC_BYTE0;
		dataOut[1] = command;

		try
		{
			send(dataOut, 0, 2);
		} catch (SerialPortException e)
		{
			log.error(e.getLocalizedMessage());
			// TODO Think in a Low Level exception
			e.printStackTrace();
		}
	}

	/**
	 * Send byte[] to serial port.
	 * 
	 * @param bytes
	 *            Bytes to send.
	 * @return True on success, False on failure.
	 * @throws SerialPortException
	 */
	private boolean send(byte[] bytes, int start, int length) throws SerialPortException
	{
		if (connected)
		{

			byte[] output = new byte[length];
			for (int i = 0; i < length; i++)
			{
				output[i] = bytes[i];
			}

			log.debug("Sending byte array, of size: '" + output.length + "' to serial port. " + length);
			serialPort.writeBytes(output);
			return true;

		} else
		{
			log.error("Serial port not connected, use connect() first.");
			throw new RuntimeException("Serial port not connected, use connect() first.");
		}
	}

	/**
	 * Sends a command with data payload
	 */
	private void sendPayLoad(byte command, byte[] payLoad)
	{
		if (verbose)
		{
			System.out.printf("Sending command 0x%02x\n", command & 0xFF);
		}

		dataOut[0] = SYNC_BYTE0;
		dataOut[1] = command;

		// add payLoad and calculate checksum
		dataOut[2] = (byte) payLoad.length;
		int checksum = 0 ^ dataOut[0] ^ dataOut[1] ^ (dataOut[2] & 0xFF);

		for (int i = 0; i < payLoad.length; i++)
		{
			dataOut[3 + i] = payLoad[i];
			checksum ^= dataOut[3 + i];
		}

		// add checksum - now total length is 3 + payLoad.length + 1
		dataOut[3 + payLoad.length] = (byte) checksum;

		try
		{
			send(dataOut, 0, 3 + payLoad.length + 1);

		} catch (SerialPortException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Sends a command with data payload - int
	 */
	private void sendPayLoad(byte command, int payLoadInt)
	{
		byte[] payLoad = new byte[2];

		// load payload little Endian
		payLoad[0] = (byte) payLoadInt;
		payLoad[1] = (byte) (payLoadInt >> 8);

		sendPayLoad(command, payLoad);
	}

	/**
	 * Sends a start motor command
	 */
	public void sendStartMotor(int speed)
	{
		sendPayLoad(START_MOTOR, speed);
	}

	/**
	 * Sends a stop motor command
	 */
	public void sendStopMotor()
	{
		sendPayLoad(START_MOTOR, 0);
	}

	/**
	 * Searches for and parses all complete packets inside data
	 */
	protected int parseData(byte[] data, int length)
	{

		int offset = 0;

		if (verbose)
		{
			StringBuilder sb = new StringBuilder("parseData length = ").append(length);
			for (int i = 0; i < length; i++)
			{
				sb.append("%02x ").append(data[i] & 0xFF);
			}
			log.info(sb.toString());
		}

		// search for the first good packet it can find
		while (true)
		{
			if (scanning)
			{
				if (offset + 5 > length)
				{
					return offset;
				}

				if (parseScan(data, offset, 5))
				{
					offset += 5;
				} else
				{
					if (verbose)
						log.info("--- Bad Packet ---");
					offset += 1;
				}
			} else
			{
				// see if it has consumed all the data
				if (offset + 1 + 4 + 1 > length)
				{
					return offset;
				}

				byte start0 = data[offset];
				byte start1 = data[offset + 1];

				if (start0 == SYNC_BYTE0 && start1 == SYNC_BYTE1)
				{
					int info = ((data[offset + 2] & 0xFF)) | ((data[offset + 3] & 0xFF) << 8)
							| ((data[offset + 4] & 0xFF) << 16) | ((data[offset + 5] & 0xFF) << 24);

					int packetLength = info & 0x3FFFFFFF;
					// int sendMode = (info >> 30) & 0xFF;
					byte dataType = data[offset + 6];

					if (verbose)
					{
						log.info("packet 0x%02x length = %d\n", dataType, packetLength);
					}
					// see if it has received the entire packet
					if (offset + 2 + 5 + packetLength > length)
					{
						if (verbose)
						{
							log.info("  waiting for rest of the packet");
						}
						return offset;
					}

					if (parsePacket(data, offset + 2 + 4 + 1, packetLength, dataType))
					{
						lastReceived = dataType & 0xFF;
						offset += 2 + 5 + packetLength;
					} else
					{
						offset += 2;
					}
				} else
				{
					offset++;
				}
			}
		}
	}

	protected boolean parsePacket(byte[] data, int offset, int length, byte type)
	{
		switch (type)
		{
		case RCV_INFO: // INFO
			return parseDeviceInfo(data, offset, length);

		case RCV_HEALTH: // health
			return parseHealth(data, offset, length);

		case RCV_SCAN: // scan and force-scan
			if (parseScan(data, offset, length))
			{
				scanning = true;
				return true;
			}
			break;
		default:
			log.debug("Unknown packet type = 0x%02x\n", type);
		}
		return false;
	}

	protected boolean parseHealth(byte[] data, int offset, int length)
	{
		if (length != 3)
		{
			log.debug("  bad health packet");
			return false;
		}

		health.status = data[offset] & 0xFF;
		health.error_code = (data[offset + 1] & 0xFF) | ((data[offset + 2] & 0xFF) << 8);

		listener.handleDeviceHealth(health);
		return true;
	}

	protected boolean parseDeviceInfo(byte[] data, int offset, int length)
	{
		if (length != 20)
		{
			log.debug("  bad device info");
			return false;
		}

		deviceInfo.model = data[offset] & 0xFF;
		deviceInfo.firmware_minor = data[offset + 1] & 0xFF;
		deviceInfo.firmware_major = data[offset + 2] & 0xFF;
		deviceInfo.hardware = data[offset + 3] & 0xFF;

		for (int i = 0; i < 16; i++)
		{
			deviceInfo.serialNumber[i] = data[offset + 4 + i];
		}

		listener.handleDeviceInfo(deviceInfo);
		return true;
	}

	protected boolean parseScan(byte[] data, int offset, int length)
	{

		if (length != 5)
			return false;

		byte b0 = data[offset];
		byte b1 = data[offset + 1];

		boolean start0 = (b0 & 0x01) == 1;
		boolean start1 = (b0 & 0x02) >> 1 == 1;

		// log.warn("s0 ,s1 " + start0 + " " + start1);

		// start0 should never equal start1
		if (start0 == start1)
			return false;

		// check bit
		if ((b1 & 0x01) != 1)
			return false;

		measurement.timeMilli = System.currentTimeMillis();
		measurement.start = start0;
		measurement.quality = (b0 & 0xFF) >> 2;
		measurement.angle = ((b1 & 0xFF) | ((data[offset + 2] & 0xFF) << 8)) >> 1;
		measurement.distance = ((data[offset + 3] & 0xFF) | ((data[offset + 4] & 0xFF) << 8));

		listener.handleMeasurement(measurement);
		return true;
	}

	public void setVerbose(boolean verbose)
	{
		this.verbose = verbose;
	}

	byte[] lastData = new byte[0];

	@Override
	public void serialEvent(SerialPortEvent serialPortEvent)
	{
		int size;

		try
		{

			byte[] ldata = serialPort.readBytes();

			// log.warn("bytes received " + ldata.length + " prepending " +
			// lastData.length);

			// concatenate the left over data from the previous parse with the
			// new incoming data into a new buffer
			int lastLength = lastData.length;
			int dataLength = ldata.length;
			size = lastLength + dataLength;
			byte[] data = new byte[size];

			for (int i = 0; i < size; i++)
			{
				if (i < lastLength)
				{
					data[i] = lastData[i];
				} else
				{
					data[i] = ldata[i - lastLength];
				}
			}

			// parse the buffer
			int lastsize = 0;
			while (size > 0 && size != lastsize)
			{
				int used = parseData(data, size);
				// log.warn(size + " - " + used);
				lastData = new byte[size - used];
				// shift the buffer over by the amount read
				for (int i = 0; i < size - used; i++)
				{
					data[i] = data[i + used];
					lastData[i] = data[i];
				}
				lastsize = size;
				size -= used;
			}

		} catch (Exception e)
		{

			e.printStackTrace();
		}

	}
}
