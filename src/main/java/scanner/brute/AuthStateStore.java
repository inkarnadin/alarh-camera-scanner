package scanner.brute;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
public class AuthStateStore {

    private AuthState state = AuthState.NOT_AUTH;
    private Optional<String> credentials = Optional.empty();

    public boolean isAuth() {
        return state == AuthState.AUTH || state == AuthState.NOT_REQUIRED;
    }

}