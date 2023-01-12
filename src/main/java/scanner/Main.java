package scanner;

import lombok.extern.slf4j.Slf4j;
import scanner.recover.RecoveryManager;
import scanner.runner.Runner;

@Slf4j
public class Main {

    public static void main(String[] args) {
        System.out.println("Started...");

        Preferences.configure(args);
        RecoveryManager.recover();

        new Runner().run();

        RecoveryManager.dropBackup();

        System.out.println("Ended...");
        System.exit(0);
    }

}