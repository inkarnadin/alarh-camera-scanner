package scanner.brute;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthStateStore {

    public final static AuthStateStore BAD_AUTH = new AuthStateStore(AuthState.NOT_AVAILABLE);

    private AuthState state = AuthState.NOT_AUTH;
    private Optional<String> credentials = Optional.empty();

    public AuthStateStore(AuthState state) {
        this.state = state;
    }

    public boolean isAuth() {
        return state == AuthState.AUTH || state == AuthState.NOT_REQUIRED;
    }

}