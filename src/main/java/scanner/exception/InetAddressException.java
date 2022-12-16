package scanner.exception;

/**
 * Исключение, относящееся к неверной обработке адреса удаленного хоста.
 *
 * @author inkarnadin
 * on 17-12-2022
 */
public class InetAddressException extends RuntimeException {

    /**
     * Базовый конструктор созданию исключения.
     *
     * @param message текст сообщений при ошибке
     * @param cause причина исключения
     */
    public InetAddressException(String message, Throwable cause) {
        super(message, cause);
    }

}