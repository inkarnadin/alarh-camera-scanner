package scanner;

import com.google.common.base.Stopwatch;
import lombok.extern.slf4j.Slf4j;
import scanner.recover.RecoveryManager;
import scanner.runner.MainRunner;
import scanner.runner.ReportRunner;
import scanner.stat.TimeStatItem;

import java.util.concurrent.TimeUnit;

import static scanner.stat.StatDataHolder.TIME_GATHERER;

@Slf4j
public class Main {

    public static void main(String[] args) {
        System.out.println("Started...");
        Stopwatch timer = Stopwatch.createStarted();

        Preferences.configure(args);
        RecoveryManager.recover();

        new MainRunner().run();

        TIME_GATHERER.set(TimeStatItem.TOTAL_TIME, timer.elapsed(TimeUnit.MILLISECONDS));
        timer.stop();

        new ReportRunner().run();

        RecoveryManager.dropBackup();

        System.out.println("Ended...");
        System.exit(0);
    }

}