package scanner.runner.breaking;

import lombok.Getter;

import java.util.Objects;

/**
 * Объект, содержащий сведения об учетных данных разблокированной цели.
 *
 * @author inkarnadin
 * on 01-01-2022
 */
@Getter
public class Credentials {

    private String login;
    private String password;

    /**
     * Конструктор для базового создания объекта хранения учетных данных.
     *
     * @param creds учетные данные строкой вида {@code login:password}
     */
    public Credentials(String creds) {
        String[] data = creds.split(":");
        if (data.length == 2) {
            login = data[0];
            password = data[1];
        }
    }

    /**
     * Метод получения статуса учетных данных.
     *
     * @return {@code true} если учетные данные в полной мере (логин и пароль) представлены в объекте, иначе вернет
     * {@code false}
     */
    public boolean isPresent() {
        return Objects.nonNull(login) && Objects.nonNull(password);
    }

    /**
     * Метод получения учетных данных.
     *
     * @return если учетные данные представлены в объекте, вернет их в виде строки, разделенной двоеточием, иначе вернет
     * {@code null}
     */
    public String get() {
        return isPresent()
                ? login.concat(":".concat(password))
                : null;
    }

    /**
     * Метод получения пустого объекта учетных данных, который может быть использован как корректная заглушка.
     *
     * @return пустой объект хранения учетных данных
     */
    public static Credentials empty() {
        return new Credentials("");
    }

}