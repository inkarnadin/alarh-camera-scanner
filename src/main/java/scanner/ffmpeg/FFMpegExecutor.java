package scanner.ffmpeg;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import scanner.rtsp.RTSPPath;
import scanner.stat.ScreenStatItem;

import java.io.File;
import java.util.concurrent.CompletableFuture;

import static scanner.rtsp.RTSPPath.STANDARD;
import static scanner.stat.StatDataHolder.SCREEN_GATHERER;

/**
 * Класс управления внешней программой FFmpeg для получения данных изображения.
 *
 * @author inkarnadin
 * on 01-01-2022
 */
@Slf4j
public class FFMpegExecutor {

    /**
     * Метод сохранения одного кадра с помощью внешней программы FFMpeg с последующей остановкой внешнего процесса.
     * <p>После выполнения сохранения производится проверка на успешный статус операции (успехом считается наличие заданного
     * файла в нужной директории).
     *
     * @param credentials учетные данные
     * @param ip целевой адрес
     * @param path базовый путь
     */
    @SneakyThrows
    public static void saveFrame(String credentials, String ip, RTSPPath path) {
        CompletableFuture.supplyAsync(() -> new FFMpegFrameSaver(ip, credentials, path).get())
                .thenAccept(x -> {
                    if (x.isAlive())
                        x.destroy();
                    log.trace("Process ({}) was ended successfully", x.pid());
                })
                .get();

        File file = new File(String.format("result/screen/%s.jpg", ip));
        if (file.exists())
            SCREEN_GATHERER.increment(ScreenStatItem.SUCCESS);
    }

    /**
     * Метод сохранения одного кадра с помощью внешней программы FFMpeg с последующей остановкой внешнего процесса.
     * <p>После выполнения сохранения производится проверка на успешный статус операции (успехом считается наличие заданного
     * файла в нужной директории).
     * <p>Использует базовый путь по умолчанию.
     *
     * @param credentials учетные данные
     * @param ip целевой адрес
     */
    @SneakyThrows
    public static void saveFrame(String credentials, String ip) {
        SCREEN_GATHERER.increment(ScreenStatItem.ALL);
        saveFrame(credentials, ip, STANDARD);
    }

}