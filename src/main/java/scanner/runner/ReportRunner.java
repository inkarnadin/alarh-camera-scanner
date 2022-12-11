package scanner.runner;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import scanner.report.MapToHTMLTableConverter;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Objects;
import java.util.stream.Collectors;

import static scanner.stat.StatDataHolder.*;

/**
 * Creation report logic class.
 *
 * @author inkarnadin
 */
@Slf4j
@Deprecated
public class ReportRunner extends AbstractRunner {

    /**
     * Method for creation report in HTML format.
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

    /**
     * Method for open report template from file by fixed path {@code /report.html}.
     *
     * @return report as string or empty string if template not found
     */
    private String openTemplate() {
        try {
            InputStream is = getClass().getResourceAsStream("/report.html");
            if (Objects.nonNull(is)) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                return reader.lines().collect(Collectors.joining());
            }
        } catch (Exception xep) {
            xep.printStackTrace();
            log.warn("error during opening report template: {}", xep.getMessage());
        }
        return "";
    }

    /**
     * Method for save filled data report.
     */
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