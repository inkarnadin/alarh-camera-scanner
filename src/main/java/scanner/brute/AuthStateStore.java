package scanner.brute;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
public class AuthStateStore {

    private AuthState state;
    private String ip;
    private Optional<String> credentials;

    public boolean isAuth() {
        return state == AuthState.AUTH;
    }

}