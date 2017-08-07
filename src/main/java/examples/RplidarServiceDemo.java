package examples;

import ev3dev.sensors.slamtec.service.RpLidarHighLevelDriver;
import ev3dev.sensors.slamtec.service.RpLidarScan;

import java.util.Date;

public class RplidarServiceDemo {

	private RpLidarHighLevelDriver driver = null;
	private boolean initSuccess = false;
	//private double mm[] = new double[ RpLidarScan.N ];
	private Date lastScanThreshold = new Date();
	private long scanNumber = 0;
	private final String port = "/dev/ttyUSB0";

	public void init(){
		System.setProperty("gnu.io.rxtx.SerialPorts", port);
		driver = new RpLidarHighLevelDriver();
		initSuccess = driver.initialize(port, 100);
		//Let's just pretend it worked...
		initSuccess = true;
	}

	public void close(){
		driver.stop();
		initSuccess = false;
	}

	public void pollScan() {

		//int scanCount = 0;
		if(initSuccess) {
			RpLidarScan scan = new RpLidarScan();
			if (!driver.blockCollectScan(scan, 0)) { //10000
				System.out.println("Scan wasn't ready yet");
			} else {
				scanNumber++;

				//scan.convertMilliMeters(mm);

				//RpLidarScan.N: rplidar data is stored in a sparse array of 360 degrees * 64 degreefractions
				for (int j = 0; j < RpLidarScan.N; j++) {
					Date scanTime = new Date(scan.time[j]);
					if( scan.distance[j] != 0 && scanTime.after(lastScanThreshold)) {
						System.out.println(scan.distance.length);

					}
				}

				lastScanThreshold = new Date();
			}
		}else {
			//Lidar not initialized
		}
	}

}