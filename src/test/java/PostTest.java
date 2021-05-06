import lombok.SneakyThrows;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.junit.Ignore;
import org.junit.Test;
import scanner.http.HttpClient;
import scanner.onvif.OnvifTokenMaker;
import scanner.onvif.OnvifAuthToken;

public class PostTest {

    @Ignore
    @Test
    @SneakyThrows
    public void getSystemDateAndTime() {
        String body = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<s:Envelope xmlns:s=\"http://www.w3.org/2003/05/soap-envelope\">\n" +
                "    <s:Body xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\n" +
                "        <GetSystemDateAndTime xmlns=\"http://www.onvif.org/ver10/device/wsdl\"/>\n" +
                "    </s:Body>\n" +
                "</s:Envelope>";

        RequestBody requestBody = RequestBody.create(body, MediaType.parse("XML"));
        String path = "http://195.91.132.250/onvif/device_service";

        Response response = HttpClient.executePost(path, requestBody);
        System.out.println(response.body().string());
    }

    @Ignore
    @Test
    @SneakyThrows
    public void getDeviceInfo() {
        OnvifAuthToken token = OnvifTokenMaker.createToken("1qaz2wsx");

        String body = String.format("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://www.w3.org/2003/05/soap-envelope\"\n" +
                "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n" +
                "xmlns:tt=\"http://www.onvif.org/ver10/schema\"\n" +
                "xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext1.0.xsd\"\n" +
                "xmlns:wsu=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility1.0.xsd\"\n" +
                "xmlns:trt=\"http://www.onvif.org/ver10/media/wsdl\"" +
                "xmlns:tds=\"http://www.onvif.org/ver10/device/wsdl\">\n" +
                "    <SOAP-ENV:Header>\n" +
                "        <wsse:Security>\n" +
                "            <wsse:UsernameToken>\n" +
                "                <wsse:Username>admin</wsse:Username>\n" +
                "                <wsse:Password Type=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wssusername-token-profile1.0#PasswordDigest\">%s</wsse:Password>\n" +
                "                <wsse:Nonce>%s</wsse:Nonce>\n" +
                "                <wsu:Created>%s</wsu:Created>\n" +
                "            </wsse:UsernameToken>\n" +
                "        </wsse:Security>\n" +
                "    </SOAP-ENV:Header>\n" +
                "    <SOAP-ENV:Body>\n" +
                "        <trt:GetStreamUri>\n" +
                "            <trt:StreamSetup xsi:type=\"tt:StreamSetup\">\n" +
                "                <tt:Stream xsi:type=\"tt:StreamType\">RTP-Unicast</tt:Stream>\n" +
                "                <tt:Transport xsi:type=\"tt:Transport\">\n" +
                "                    <tt:Protocol xsi:type=\"tt:TransportProtocol\">UDP</tt:Protocol>\n" +
                "                </tt:Transport>\n" +
                "            </trt:StreamSetup>\n" +
                "            <trt:ProfileToken xsi:type=\"tt:ReferenceToken\">Profile_1</trt:ProfileToken>\n" +
                "        </trt:GetStreamUri>\n" +
                "    </SOAP-ENV:Body>\n" +
                "</SOAP-ENV:Envelope>", token.getDigestBase64(), token.getNonceBase64(), token.getDateCreated());

        RequestBody requestBody = RequestBody.create(body, MediaType.parse("XML"));
        String path = "http://195.91.132.250/onvif/media_service";

        Response response = HttpClient.executePost(path, requestBody);
        System.out.println(response.body().string());
    }

}