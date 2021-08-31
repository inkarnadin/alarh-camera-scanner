package scanner;

import lombok.extern.slf4j.Slf4j;
import scanner.report.MapToHTMLTableConverter;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.file.*;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static scanner.stat.StatDataHolder.*;

/**
 * Create report logic class.
 *
 * @author inkarnadin
 */
@Slf4j
public class XReportRunner extends AbstractRunner {

    private final static String emptyTemplate = "";

    /**
     * Create report in HTML format.
     */
    public void run() {
        try {
            String template = openTemplate();

            if (template.length() == 0)
                return;

            String report = template.replace("$title", "Report")
                    .replace("$scanReport", MapToHTMLTableConverter.convert(SCAN_GATHERER.getData()))
                    .replace("$timeReport", MapToHTMLTableConverter.convert(TIME_GATHERER.getData()))
                    .replace("$effectiveReport", MapToHTMLTableConverter.convert(EFFICIENCY_GATHERER.getData()))
                    .replace("$ffmpegReport", MapToHTMLTableConverter.convert(SCREEN_GATHERER.getData()));

            saveReport(report);
        } catch (Exception xep) {
            log.warn("report creating error: {}", xep.getMessage());
        }
    }

    private String openTemplate() {
        try {
            InputStream in = getClass().getResourceAsStream("/report.html");
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            return reader.lines().collect(Collectors.joining());
        } catch (Exception xep) {
            xep.printStackTrace();
            log.warn("error during opening report template: {}", xep.getMessage());
        }
        return emptyTemplate;
    }

    private void saveReport(String report) {
        try {
            Path path = Paths.get(String.format("result//report-%s.html", Instant.now().toString()));
            Files.createFile(path);
            Files.writeString(path, report);
        } catch (Exception xep) {
            log.warn("error during save report: {}", xep.getMessage());
        }
    }

}