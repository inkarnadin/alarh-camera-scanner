package scanner;

import lombok.extern.slf4j.Slf4j;
import scanner.report.MapToHTMLTableConverter;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;

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
        String template = openTemplate();

        if (template.length() == 0)
            return;

        String report = template.replace("$title", "Report")
                .replace("$scanReport", MapToHTMLTableConverter.convert(SCAN_GATHERER.getData()))
                .replace("$timeReport", MapToHTMLTableConverter.convert(TIME_GATHERER.getData()))
                .replace("$effectiveReport", MapToHTMLTableConverter.convert(EFFICIENCY_GATHERER.getData()))
                .replace("$ffmpegReport", MapToHTMLTableConverter.convert(SCREEN_GATHERER.getData()));

        saveReport(report);
    }

    private String openTemplate() {
        try {
            Path path = Paths.get(ClassLoader.getSystemResource("report.html").toURI());
            return Files.readString(path);
        } catch (Exception xep) {
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