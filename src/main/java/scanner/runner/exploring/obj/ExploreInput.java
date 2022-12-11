package scanner.runner.exploring.obj;

import lombok.Builder;
import lombok.Getter;
import scanner.http.ip.IpV4AddressRange;

/**
 * Input explore object wrapper.
 *
 * @author inkarnadin
 * on 01-10-2022
 */
@Getter
@Builder
public class ExploreInput {

    private final IpV4AddressRange range;

}