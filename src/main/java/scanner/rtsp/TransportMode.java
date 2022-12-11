package scanner.rtsp;

/**
 * Перечисление допустимых типов базового пути запроса для обращения по RTSP.
 * <p>Данное перечисление подразумевает под собой возможность изменения базового пути, по которому производится проверка.
 * <ul>
 * <li>{@link TransportMode#ORTHODOX} - проверка производится по корневому пути (без модификаций)</li>
 * <li>{@link TransportMode#SPECIAL} - проверка производится по указанному специальному пути</li>
 * </ul>
 *
 * @author inkarnadin
 * on 01-01-2022
 */
public enum TransportMode {

    ORTHODOX,
    SPECIAL

}
