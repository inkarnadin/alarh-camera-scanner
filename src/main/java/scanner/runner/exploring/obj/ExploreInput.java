package scanner.runner.exploring.obj;

import lombok.Builder;
import lombok.Getter;
import scanner.http.ip.IpV4AddressRange;

@Getter
@Builder
public class ExploreInput {

    private final IpV4AddressRange range;

}