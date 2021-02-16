import okhttp3.Response;
import org.junit.Ignore;
import org.junit.Test;
import scanner.crypto.ConfigurationDecrypt;
import scanner.http.HttpClient;

import java.io.InputStream;

public class ConfigurationDescriptorTest {

    @Test
    @Ignore
    public void testDectypt() {
        HttpClient client = new HttpClient();
        Response response = client.execute("http://81.25.57.11/System/configurationFile?auth=YWRtaW46MTEK");
        InputStream inputStream = response.body().byteStream();
        System.out.println(ConfigurationDecrypt.decrypt(inputStream));
    }

}