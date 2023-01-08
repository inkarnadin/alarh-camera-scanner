package scanner.runner;

import lombok.extern.slf4j.Slf4j;
import scanner.Preferences;
import scanner.http.ip.IpV4AddressRange;
import scanner.http.ip.RangeUtils;
import scanner.rtsp.RTSPContext;
import scanner.runner.unlock.UnlockModule;
import scanner.runner.unlock.obj.UnlockInput;
import scanner.runner.unlock.obj.UnlockOutput;
import scanner.runner.exploring.ExplorerModule;
import scanner.runner.exploring.obj.ExploreInput;
import scanner.runner.exploring.obj.ExploreOutput;
import scanner.runner.logging.obj.LoggerInput;
import scanner.runner.logging.LoggerModule;

import java.math.BigDecimal;
import java.util.Set;

import static java.math.RoundingMode.FLOOR;

/**
 * Точка входа в процесс проверки возможности разблокировки целевого пула адресов
 *
 * @author inkarnadin
 * on 02-10-2022
 */
@Slf4j
public class NewMainRunner {

    private final ExplorerModule explorerModule = new ExplorerModule();
    private final UnlockModule unlockModule = new UnlockModule();
    private final LoggerModule loggerModule = new LoggerModule();

    /**
     * Метод запуска проверок возможности разблокировки целевого пула адресов.
     * <p/>Из свойств приложения извлекаются два списка - список паролей и список диапазонов для дальнейшего анализа.
     * Далее список диапазонов преобразуется в удобный для перебора формат и итеративно, каждый из диапазонов проходит через
     * ряд шагов обработки:
     * <ol>
     * <li>поиск подходящих целей;</li>
     * <li>получение доступа к потокам найденных ранее целей;</li>
     * <li>запись результата в файлы логов.</li>
     * </ol>
     * <p/>После выполнения всех операций в рамках диапазона происходит сброс контекста - очистка временного хранилища путей
     * до целевого потока в рамках каждого адреса.
     */
    public void run() {
        Set<String> sources = Preferences.getRangesList();
        Set<String> passwords = Preferences.getPasswordsList();

        int completeRange = 0;
        long completeAddress = 0;

        Set<IpV4AddressRange> ranges = RangeUtils.prepare(sources);
        for (IpV4AddressRange range : ranges) {
            // exploring
            ExploreOutput exploreOutput = explorerModule.execute(ExploreInput.builder()
                    .range(range)
                    .build());
            // unlock
            UnlockOutput unlockOutput = unlockModule.execute(UnlockInput.builder()
                    .addresses(exploreOutput.getTargets())
                    .passwords(passwords)
                    .repeatCount(1)
                    .build());
            //logging
            loggerModule.execute(LoggerInput.builder()
                    .targets(unlockOutput.getTargets())
                    .build());

            completeAddress += range.getCount();
            BigDecimal percent = BigDecimal.valueOf((double) completeAddress / RangeUtils.count(ranges) * 100).setScale(2, FLOOR);
            String completePercent = String.format("complete %s/%s (%s%%)", ++completeRange, ranges.size(), percent);
            log.info(completePercent);
            System.out.println(completePercent);

            log.trace("Clear RTSP context");
            RTSPContext.evict();
        }
    }

}