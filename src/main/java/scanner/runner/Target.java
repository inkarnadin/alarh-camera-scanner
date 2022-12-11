package scanner.runner;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import scanner.runner.breaking.BreakType;
import scanner.runner.breaking.Credentials;

import java.util.Objects;
import java.util.StringJoiner;

/**
 * Объект цели (удаленного адреса и его атрибутов).
 *
 * @author inkarnadin
 * on 01-01-2022
 */
@Getter
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Target {

    @EqualsAndHashCode.Include
    private final String host;
    private final String path;
    private final Credentials credentials;
    private final boolean isFreeStream;
    @Setter
    private BreakType breakType;

    /**
     * Метод получения статуса цели.
     * <p> Критерием того, что цель разблокирована служит один из следующих факторов - наличие авторизационных учетных
     * данных или статус "свободной" цели (то есть, не требующей авторизации).
     *
     * @return вернет {@code true}, если цель разблокирована, иначе {@code false}
     */
    public boolean isBroken() {
        return (Objects.nonNull(credentials) && credentials.isPresent()) || isFreeStream;
    }

    public String asFullFormattedString() {
        return (isFreeStream)
                ? new StringJoiner(":").add(host).add(path).add("").add("").add(breakType.getDescription()).toString()
                : new StringJoiner(":").add(host).add(path).add(credentials.get()).add(breakType.getDescription()).toString();
    }

}