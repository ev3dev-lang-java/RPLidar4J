package ev3dev.sensors.slamtec;

import ev3dev.sensors.slamtec.utils.JarResource;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public @Slf4j class RPLidarA1 implements LIDAR {

    @Override
    public Scan scan() {

        try{

            final Process p2 = Runtime.getRuntime().exec("pkill ultra_simple");
            p2.destroy();

            final String fullPath3 = JarResource.export("/ultra_simple");
            log.trace(fullPath3);

            final File file = new File(fullPath3);
            if(file.exists()){
                log.trace("Is Execute allow : " + file.canExecute());
                log.trace("Is Write allow : " + file.canWrite());
                log.trace("Is Read allow : " + file.canRead());
                file.setExecutable(true);
                file.setReadable(true);
                file.setWritable(true);
            }

            final Process p = Runtime.getRuntime().exec(fullPath3);
            final BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            final StringBuffer output = new StringBuffer();
            String line = "";
            int i = 0;
            while ((line = in.readLine())!= null) {
                log.trace(line);
                output.append(line + "\n");
                i++;
                if(i > 10){
                    break;
                }
            }
            in.close();
            p.destroy();

            if(file.delete()){
                System.out.println(file.getName() + " is deleted!");
            }else{
                log.trace("Delete operation is failed.");
            }
            final Process p3 = Runtime.getRuntime().exec("pkill ultra_simple");
            p3.destroy();
        }catch(Exception e){
            log.error(e.getLocalizedMessage());
        }

        return null;

    }

}
