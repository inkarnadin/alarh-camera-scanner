package scanner.ffmpeg;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import scanner.stat.ScreenStatItem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static scanner.stat.StatDataHolder.SCREEN_GATHERER;

/**
 * FFmpeg console output reader.
 *
 * @author inkarnadin
 */
@Slf4j
@RequiredArgsConstructor
public class FFMpegLogReader implements Runnable {

    private final Process process;

    /**
     * Read FFMpeg console output.
     */
    @Override
    public void run() {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            StringBuilder output = new StringBuilder();
            while ((line = br.readLine()) != null) {
                output.append(line);
            }
        } catch (IOException e) {
            SCREEN_GATHERER.increment(ScreenStatItem.UNEXPECTED_ERROR);
            log.warn("Read buffer error: pid = {}, message = {}", process.pid(), e.getMessage());
        }
    }

}