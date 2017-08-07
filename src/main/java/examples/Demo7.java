package examples;

public class Demo7 {

	private final String port = "/dev/ttyUSB0";

	public static void main(String[] args){

		RplidarServiceDemo rplidarService = new RplidarServiceDemo();
		rplidarService.init();

		int counter = 0;
		boolean flag = true;
		while (flag){

			rplidarService.pollScan();

			counter++;
			System.out.println("Counter: " + counter);

			if(counter > 50){
				break;
			}

		}

		rplidarService.close();

	}
}