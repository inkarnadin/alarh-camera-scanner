package scanner.ffmpeg;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * Класс остановки указанного внешнего (системного) процесса.
 *
 * @author inkarnadin
 * on 01-01-20222
 */
@Slf4j
@RequiredArgsConstructor
public class FFMpegProcessKiller {

    private final Process process;

    /**
     * Метод прерывания определенного процесса через системную консоль.
     */
    public void kill() {
        try {
            createKillerProcess().start();
            Thread.sleep(1000);
        } catch (IOException | InterruptedException e) {
            process.destroy();
            log.warn("({}) Error during kill process pid {}", e.getMessage(), process.pid());
        }
    }

    /**
     * Метод создание команды для инициации остановки системного процесса.
     *
     * @return объект для создания команды остановки внешнего процесса
     */
    private ProcessBuilder createKillerProcess() {
        return new ProcessBuilder()
                .command("kill", String.valueOf(process.pid()));
    }

}