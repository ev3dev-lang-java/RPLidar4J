package ev3dev.sensors.slamtec;

import ev3dev.sensors.slamtec.utils.JarResource;
import ev3dev.sensors.slamtec.utils.ProcessManager;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public @Slf4j class RPLidarA1 implements LIDAR {

    final private ProcessManager processManager;
    final private JarResource jarResource;

    final private String SLAMTEC_BINARY = "ultra_simple";
    private String SLAMTEC_PATH;

    public RPLidarA1() {
        this.processManager = new ProcessManager();
        this.jarResource = new JarResource();
    }

    @Override
    public void init() throws LIDARServiceException {

        try {
            processManager.execute("pkill " + SLAMTEC_BINARY);

            SLAMTEC_PATH = jarResource.export("/" + SLAMTEC_BINARY);
            log.trace(SLAMTEC_PATH);

            final File file = new File(SLAMTEC_PATH);
            if(file.exists()){
                log.trace("Is Execute allow : " + file.canExecute());
                log.trace("Is Write allow : " + file.canWrite());
                log.trace("Is Read allow : " + file.canRead());
                file.setExecutable(true);
                file.setReadable(true);
                file.setWritable(true);
            }

        } catch (IOException e) {
            throw new LIDARServiceException(e.getLocalizedMessage());
        }

    }

    @Override
    public Scan scan() throws LIDARServiceException {

        try{

            final Process p = Runtime.getRuntime().exec(SLAMTEC_PATH);
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

        }catch(IOException e){
            log.error(e.getLocalizedMessage());
            throw new LIDARServiceException(e.getLocalizedMessage());
        }

        return null;

    }

    @Override
    public void close() throws LIDARServiceException {

        try {

            final File file = new File(SLAMTEC_PATH);
            if(file.delete()){
                log.info("{} is deleted!", file.getName());
            }else{
                log.trace("Delete operation is failed.");
            }

            processManager.execute("pkill " + SLAMTEC_BINARY);

        }catch(IOException e){
            log.error(e.getLocalizedMessage());
            throw new LIDARServiceException(e.getLocalizedMessage());
        }
    }

}
