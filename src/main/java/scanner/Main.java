package scanner;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;

@Slf4j
public class Main {

    public static void main(String[] args) {
        Preferences.configure(args);

        String source = Preferences.get("-source");
        String passwords = Preferences.get("-passwords");

        if (Objects.isNull(source)) {
            log.error("source cannot be empty!");
            System.exit(0);
        }

        List<String> listSources = SourceReader.readSource(source);
        List<String> listPasswords = SourceReader.readSource(passwords);

        if (listPasswords.isEmpty())
            listPasswords.addAll(Preferences.getDefaultPasswordList());

        RescueManager.restore();

        new XScanRunner(listSources).run();
        new XBruteRunner(listSources, listPasswords).run();

        new XReportRunner().run();

        System.exit(0);
    }

}