package scanner.stat;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ReportPrinter {

    public static void print() {
        log.info(ScreenStatGatherer.createReport());
    }

}