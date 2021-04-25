package scanner.ffmpeg;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import scanner.stat.ScreenStatEnum;
import scanner.stat.ScreenStatGatherer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * FFmpeg console output reader.
 *
 * @author inkarnadin
 */
@Slf4j
@RequiredArgsConstructor
public class FFmpegLogReader implements Runnable {

    private final Process process;

    /**
     * Read FFmpeg console output.
     */
    @Override
    public void run() {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = br.readLine()) != null) {
                log.info(line);
                builder.append(line);
            }
            ScreenStatGatherer.gather(builder.toString());
        } catch (IOException e) {
            ScreenStatGatherer.increment(ScreenStatEnum.UNEXPECTED_ERROR);
            log.warn("Read buffer error: pid = {}, message = {}", process.pid(), e.getMessage());
        }
    }

}