package scanner.analyze;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

/**
 * POJO for store problem target.
 *
 * @param <T> solution argument type
 */
@Data
@EqualsAndHashCode
@RequiredArgsConstructor
@Deprecated
public class ProblemTarget<T> {

    private final String ip;
    private final String credentials;
    private final Resolve<T> resolve;

}