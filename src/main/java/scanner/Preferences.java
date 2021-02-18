package scanner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Preferences {

    public static final List<String> prefs = new ArrayList<>();

    public static void configure(String[] values) {
        prefs.addAll(Arrays.asList(values));
    }

    public static boolean check(String param) {
        return prefs.contains(param);
    }

    public static Optional<String> get(String param) {
        return prefs.stream().filter(f -> f.contains(param)).findFirst();
    }

}