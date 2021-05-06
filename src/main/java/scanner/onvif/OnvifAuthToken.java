package scanner.onvif;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Simple ONVIF security token storage.
 *
 * @author inkarnadin
 */
@Getter
@RequiredArgsConstructor
public class OnvifAuthToken {

    private final String nonceBase64;
    private final String dateCreated;
    private final String digestBase64;

}