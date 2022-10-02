package scanner.runner.breaking;

import lombok.Getter;

import java.util.Objects;

@Getter
public class Credentials {

    private String login;
    private String password;

    public Credentials(String creds) {
        String[] data = creds.split(":");
        if (data.length == 2) {
            login = data[0];
            password = data[1];
        }
    }

    public boolean isPresent() {
        return Objects.nonNull(login) && Objects.nonNull(password);
    }

    public String get() {
        return isPresent()
                ? login.concat(":".concat(password))
                : null;
    }

    public static Credentials empty() {
        return new Credentials("");
    }

}