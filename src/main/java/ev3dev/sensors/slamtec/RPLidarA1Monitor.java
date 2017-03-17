package ev3dev.sensors.slamtec;

import ev3dev.sensors.slamtec.model.Scan;
import ev3dev.sensors.slamtec.utils.JarResource;
import ev3dev.sensors.slamtec.utils.ProcessManager;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

@Slf4j class  RPLidarA1Monitor extends Thread implements RPLidarProvider {

    final private ProcessManager processManager;
    final private JarResource jarResource;

    final private String SLAMTEC_BINARY = "ultra_simple";
    private String SLAMTEC_PATH;

    ProcessBuilder ps;
    Process pr;
    BufferedReader in;

    public RPLidarA1Monitor() {
        log.trace("Returning a RPLidarA1 Object");
        this.processManager = new ProcessManager();
        this.jarResource = new JarResource();
    }

    public void run(){

    }

    @Override
    public void init() throws RPLidarA1ServiceException {

        try {
            processManager.execute("pkill " + SLAMTEC_BINARY);

            SLAMTEC_PATH = jarResource.export("/" + SLAMTEC_BINARY);
            log.trace(SLAMTEC_PATH);

            final File file = new File(SLAMTEC_PATH);
            if(file.exists()){
                file.setExecutable(true);
                file.setReadable(true);
                file.setWritable(true);
                log.trace("Is Execute allow : " + file.canExecute());
                log.trace("Is Write allow : " + file.canWrite());
                log.trace("Is Read allow : " + file.canRead());
            }

            //First time
            log.debug("Trying to connect first time");
            log.debug(SLAMTEC_PATH + " ttyUSB0");

            ps = new ProcessBuilder(SLAMTEC_PATH);
            ps.redirectErrorStream(true);

            pr = ps.start();
            in = new BufferedReader(new InputStreamReader(pr.getInputStream()));

            Thread.sleep(10000);

        } catch (IOException e) {
            throw new RPLidarA1ServiceException(e.getLocalizedMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public Scan scan() throws RPLidarA1ServiceException {

        try{

            Thread.sleep(2000);


            String line;
            int i = 0;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
                i++;
                if(i > 10){
                    break;
                }
            }

            log.info("{}", pr.isAlive());

            log.info("ok!");



        }catch(IOException e){
            log.error(e.getLocalizedMessage());
            throw new RPLidarA1ServiceException(e.getLocalizedMessage());
        } catch (InterruptedException e) {
            log.error(e.getLocalizedMessage());
            throw new RPLidarA1ServiceException(e.getLocalizedMessage());

        }

        return null;
    }

    public void close() throws RPLidarA1ServiceException {

        try {
            Thread.sleep(1000);

            in.close();
            pr.destroy();

            final File file = new File(SLAMTEC_PATH);
            if(file.delete()){
                log.info("{} is deleted!", file.getName());
            }else{
                log.trace("Delete operation is failed.");
            }

            processManager.execute("pkill " + SLAMTEC_BINARY);

        }catch(InterruptedException| IOException e){
            log.error(e.getLocalizedMessage());
            throw new RPLidarA1ServiceException(e.getLocalizedMessage());
        }
    }

}
