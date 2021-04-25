package scanner.ffmpeg;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * Process killer.
 *
 * @author inkarnadin
 *
 */
@Slf4j
@RequiredArgsConstructor
public class FFmpegProcessKiller {

    private final Process process;

    /**
     * Kill certain process through console command.
     */
    public void kill() {
        try {
            createProcess().start();
            Thread.sleep(1000);
        } catch (IOException | InterruptedException e) {
            process.destroy();
            log.warn("({}) Error during kill process pid {}", e.getMessage(), process.pid());
        }
    }

    private ProcessBuilder createProcess() {
        return new ProcessBuilder()
                .command("kill", String.valueOf(process.pid()));
    }

}