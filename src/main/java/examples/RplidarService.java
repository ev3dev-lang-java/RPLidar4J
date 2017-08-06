package examples;

import ev3dev.sensors.slamtec.service.RpLidarScan;

import java.util.Date;

public class RplidarService {

	private RpLidarHighLevelDriver driver = null;
	private boolean initSuccess = false;
	private double mm[] = new double[ RpLidarScan.N ];
	private Date lastScanThreshold = new Date();
	private long scanNumber = 0;

	private void init(){
		System.setProperty("gnu.io.rxtx.SerialPorts", "/dev/ttyUSB0");
		driver = new RpLidarHighLevelDriver();
		initSuccess = driver.initialize("/dev/ttyUSB0",100);
		//Let's just pretend it worked...
		initSuccess = true;
	}
	
	private void close(){
		driver.stop();
		initSuccess = false;
	}
	
	private void pollScan() {

		//int scanCount = 0;
		if(initSuccess) {
			RpLidarScan scan = new RpLidarScan();
			if (!driver.blockCollectScan(scan, 0)) { //10000
				System.out.println("Scan wasn't ready yet");
			} else {
				scanNumber++;

				scan.convertMilliMeters(mm);
				
				//RpLidarScan.N: rplidar data is stored in a sparse array of 360 degrees * 64 degreefractions
				for (int j = 0; j < RpLidarScan.N; j++) {
					Date scanTime = new Date(scan.time[j]);
					if( scan.distance[j] != 0 && scanTime.after(lastScanThreshold)) {
						System.out.println(scan.distance[j]);
					}
				}
				//Send the lidar detection to the UI
				lastScanThreshold = new Date();

			}
		}else {
			//Lidar not initialized
		}
	}

	public static void main(String[] args){

		RplidarService rplidarService = new RplidarService();
		rplidarService.init();

		int counter = 0;
		boolean flag = true;
		while (flag){

			rplidarService.pollScan();

			counter++;
			System.out.println("Counter: " + counter);

			if(counter > 5){
				break;
			}

		}

		rplidarService.close();

	}
}