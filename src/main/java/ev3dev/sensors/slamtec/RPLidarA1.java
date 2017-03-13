package ev3dev.sensors.slamtec;

import ev3dev.sensors.slamtec.utils.ExportPython;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class RPLidarA1 implements LIDAR {

    @Override
    public Scan scan() {

        try{

            final String fullPath = ExportPython.ExportResource("/LIDARDummy.py");
            final Process p = Runtime.getRuntime().exec("python " + fullPath);
            final BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            final StringBuffer output = new StringBuffer();
            String line = "";
            int i = 0;
            while ((line = in.readLine())!= null) {
                System.out.println(line);
                output.append(line + "\n");
                i++;
                if(i > 10){
                    break;
                }
            }
            in.close();
        }catch(Exception e){
            System.out.println(e.getMessage());
        }

        return null;

    }

}
