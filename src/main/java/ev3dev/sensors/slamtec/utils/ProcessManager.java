package ev3dev.sensors.slamtec.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

public @Slf4j class ProcessManager {

    public void execute(final String command) throws IOException {

        try {
            final Process process = Runtime.getRuntime().exec(command);
            process.destroy();
        } catch (IOException e) {
            log.error(e.getLocalizedMessage());
            throw new IOException(e.getLocalizedMessage());
        }

    }
}
