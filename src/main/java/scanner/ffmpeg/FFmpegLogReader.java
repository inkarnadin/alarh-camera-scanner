package scanner.ffmpeg;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import scanner.stat.ScreenStatItem;
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
    private final String ip;
    private final String credentials;

    /**
     * Read FFmpeg console output.
     */
    @Override
    public void run() {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            StringBuilder output = new StringBuilder();
            while ((line = br.readLine()) != null) {
                log.info(line);
                output.append(line);
            }
            FFmpegInspector.inspect(output.toString(), ip, credentials);
        } catch (IOException e) {
            ScreenStatGatherer.increment(ScreenStatItem.UNEXPECTED_ERROR);
            log.warn("Read buffer error: pid = {}, message = {}", process.pid(), e.getMessage());
        }
    }

}