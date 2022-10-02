package scanner.runner;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import scanner.runner.breaking.BreakType;
import scanner.runner.breaking.Credentials;

import java.util.Objects;
import java.util.StringJoiner;

@Getter
@Builder
public class Target {

    private final String host;
    private final String path;
    private final Credentials credentials;
    private final boolean isFreeStream;
    @Setter
    private BreakType breakType;

    public boolean isBroken() {
        return (Objects.nonNull(credentials) && credentials.isPresent()) || isFreeStream;
    }

    public String asFullFormattedString() {
        return (isFreeStream)
                ? new StringJoiner(":").add(host).add(path).add("").add("").add(breakType.name()).toString()
                : new StringJoiner(":").add(host).add(path).add(credentials.get()).add(breakType.name()).toString();
    }

}