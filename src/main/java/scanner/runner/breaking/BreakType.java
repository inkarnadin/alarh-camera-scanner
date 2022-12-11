package scanner.runner.breaking;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Перечисление типов разблокировки для маркировки типа примененного алгоритма при разблокировке цели.
 *
 * @author inkarnadin
 * on 01-01-2022
 */
@Getter
@RequiredArgsConstructor
public enum BreakType {

    CVE("<cve>"),
    EMPTY("<empty>"),
    BRUTE("<brute>"),
    REPEAT("<repeat>"),
    REPEAT_EMPTY("<repeat_empty>"),
    UNBROKEN("<none>");

    private final String description;

}
