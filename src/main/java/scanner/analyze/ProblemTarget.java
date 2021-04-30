package scanner.analyze;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@Data
@EqualsAndHashCode
@RequiredArgsConstructor
public class ProblemTarget<T> {

    private final String ip;
    private final String credentials;
    private final Resolve<T> resolve;

}