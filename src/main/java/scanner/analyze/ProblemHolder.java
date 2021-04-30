package scanner.analyze;

import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

/**
 * Store for addresses which will be checking again.
 *
 * @author inkarnadin
 */
public class ProblemHolder {

    @Getter
    private static final Set<ProblemTarget<?>> store = new HashSet<>();

    public static boolean save(ProblemTarget<?> target) {
        return store.add(target);
    }



}