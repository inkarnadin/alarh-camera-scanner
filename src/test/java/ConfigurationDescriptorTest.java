import okhttp3.Response;
import org.junit.Test;
import scanner.crypto.ConfigurationDescriptor;
import scanner.http.HttpClient;

import java.io.InputStream;

public class ConfigurationDescriptorTest {

    @Test
    public void testDectypt() {
        HttpClient client = new HttpClient();
        Response response = client.execute("http://81.25.57.11/System/configurationFile?auth=YWRtaW46MTEK");
        InputStream inputStream = response.body().byteStream();
        System.out.println(ConfigurationDescriptor.decrypt(inputStream));
    }

}