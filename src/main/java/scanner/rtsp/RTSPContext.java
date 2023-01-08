package scanner.rtsp;

import java.util.HashMap;
import java.util.Map;

/**
 * Контекст определения базового путь для проверки учетных данных.
 * <p>Иногда базовый путь к целевому адресу не позволяет проверить, можно ли авторизоваться и возвращает код 404.
 * Класс {@code RTSPContext} нужен для сохранения выбора пути проверки для каждого проверяемого хоста.
 * Его значение определяется при первом запросе с пустыми учетными данными. Это позволяет избежать ненужных проверок по некорректному
 * пути.
 * <p/>Режим <b>ORTODOX</b> вернется по умолчанию.
 *
 * @author inkarnadin
 * on 01-01-2022
 */
public class RTSPContext {

    private static final Map<String, TransportMode> storage = new HashMap<>();

    /**
     * Метод устанавливает определенное значение базового пути для указанного адреса. Для каждого адреса доступен только
     * один активный базовый путь, устанавливаемое значение перезапишет предыдущее.
     *
     * @param ip целевой адрес
     * @param mode активный базовый путь
     */
    public static void set(String ip, TransportMode mode) {
        storage.put(ip, mode);
    }

    /**
     * Получение активного базового пути для указанного целевого адреса.
     *
     * @param ip целевой адрес
     * @return активный базовый путь, по умолчанию <b>ORTHODOX</b>
     */
    public static TransportMode get(String ip) {
        return storage.getOrDefault(ip, TransportMode.ORTHODOX);
    }

    /**
     * Метод сброса состояния контекста.
     */
    public static void evict() {
        storage.clear();
    }

}