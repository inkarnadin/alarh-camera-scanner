package scanner.runner.exploring;

import lombok.extern.slf4j.Slf4j;
import scanner.http.ip.IpV4AddressRange;
import scanner.runner.Target;
import scanner.runner.exploring.obj.ExploreInput;
import scanner.runner.exploring.obj.ExploreOutput;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Exploring module class.
 *
 * @author inkarnadin
 * on 02-10-2022
 */
@Slf4j
public class ExplorerModule {

    /**
     * Метод запуска начала сканирования адресов на предмет принадлежности к целевым портам.
     *
     * @param input входной объект-обертка для сканирования
     * @return выходной объект - результат сканирования
     */
    public ExploreOutput execute(ExploreInput input) {
        Set<String> result = new HashSet<>();

        IpV4AddressRange range = input.getRange();
        try {
            Set<InetSocketAddress> addresses = range.getAddresses();

            CameraScanner scanner = new CameraScanner();
            result = scanner.scanning(addresses);
            log.info("range [{}], success: {}/{}", range.getName(), result.size(), addresses.size());
            log.info("addresses {}", result);
        } catch (Exception xep) {
            log.error("error during check ip range [{}]: {}", range.getName(), xep.getMessage());
        }

        return ExploreOutput.builder()
                .targets(result.stream()
                        .map(val -> Target.builder()
                                .host(val)
                                .build())
                        .collect(Collectors.toSet()))
                .build();
    }

}