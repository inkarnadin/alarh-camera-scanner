package scanner;

import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;

@Slf4j
public class Main {

    public static void main(String[] args) {
        Preferences.configure(args);

        String source = Preferences.get("-source");
        String passwords = Preferences.get("-passwords");

        if (source.isEmpty()) {
            log.error("source cannot be empty!");
            System.exit(0);
        }

        List<String> listSources = SourceReader.readSource(source);
        List<String> listPasswords = SourceReader.readSource(passwords);

        if (listPasswords.isEmpty())
            listPasswords.addAll(Collections.singletonList("asdf1234"));

        new XScanRunner(listSources).run();
        new XBruteRunner(listSources, listPasswords).run();

        new XReportRunner().run();

        System.exit(0);
    }

}