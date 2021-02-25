package scanner.brute;

/**
 * Authorization state that reflects the result of the attempt.
 * <ul>
 *    <li>{@link AuthState#AUTH} - authorization was successful </li>
 *    <li>{@link AuthState#NOT_AUTH} - authorization was failure </li>
 *    <li>{@link AuthState#NOT_REQUIRED} - authorization was successful with empty credentials or more then one credentials</li>
 *    <li>{@link AuthState#NOT_AVAILABLE} - target is unavailable, impossible checking</li>
 *    <li>{@link AuthState#UNKNOWN_STATE} - unknown state of attempt such as bad param, bad request, server error and etc</li>
 * <ul/>
 * @author inkarnadin
 */
public enum AuthState {

    AUTH,
    NOT_AUTH,
    NOT_REQUIRED,
    NOT_AVAILABLE,
    UNKNOWN_STATE

}