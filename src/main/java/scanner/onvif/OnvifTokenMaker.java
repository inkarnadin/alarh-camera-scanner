package scanner.onvif;

import lombok.SneakyThrows;

import java.security.MessageDigest;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.UUID;

/**
 * Onvif security token maker.
 *
 * @author inkarnadin
 */
public class OnvifTokenMaker {

    private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ").withZone(ZoneId.of("UTC"));

    /**
     * Create special digest for access via ONVIF.
     *
     * @param password user password
     * @return onvif token storage
     */
    public static OnvifAuthToken createToken(String password) {
        String nonce = UUID.randomUUID().toString();
        String dateCreated = ZonedDateTime.now().format(formatter);
        String digest = signing(nonce + dateCreated + password);

        return new OnvifAuthToken(Base64.getEncoder().encodeToString(nonce.getBytes()), dateCreated, digest);
    }

    @SneakyThrows
    private static String signing(String message) {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        md.reset();
        md.update(message.getBytes());
        byte[] digest = md.digest();
        return new String(Base64.getEncoder().encode(digest));
    }

}