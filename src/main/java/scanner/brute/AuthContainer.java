package scanner.brute;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class AuthContainer {

    @Getter
    private final String ip;
    private final Map<String, AuthState> auth = new HashMap<>();

    public void put(String password, AuthState state) {
        auth.put(password, state);
    }

    public List<String> getOnlyAuth() {
        return auth.entrySet().stream()
                .filter(x -> x.getValue() == AuthState.AUTH)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public AuthState getFirst() {
        return auth.getOrDefault(null, AuthState.NOT_AVAILABLE);
    }

}