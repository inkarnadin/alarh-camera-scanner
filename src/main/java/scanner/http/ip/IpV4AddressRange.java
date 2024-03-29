package scanner.http.ip;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import scanner.exception.InetAddressException;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Класс, содержащий сведения о рабочем диапазоне IP-адресов.
 *
 * @author inkarnadin
 * on 31-05-2022
 */
@Slf4j
@Getter
@EqualsAndHashCode
public class IpV4AddressRange implements Comparable<IpV4AddressRange> {

    private final String sourceRange;
    private final String name;
    private final BigInteger startIp;
    private final BigInteger stopIp;
    private final long count;

    private boolean isLarge;

    /**
     * Конструктор создания объекта диапазона адресов.
     * <p>В качестве обязательного параметра принимает значение диапазона в формате <i>11.10.1.0-11.10.1.255</i>.
     * @param range диапазон адресов в строковом представлении заданного формата
     *
     * @throws InetAddressException исключение, которое может возникнуть при ошибках обработки адреса
     */
    @SneakyThrows
    public IpV4AddressRange(String range) {
        String[] rangeAddresses = range.split("-");

        sourceRange = range;
        name = rangeAddresses[0];

        byte[] start = InetAddress.getByName(rangeAddresses[0]).getAddress();
        startIp = new BigInteger(1, start);

        byte[] stop = InetAddress.getByName(rangeAddresses[1]).getAddress();
        stopIp = new BigInteger(1, stop);

        count = stopIp.subtract(startIp).longValue() + 1L;
        if (count > 255) {
            isLarge = true;
        }
    }

    /**
     * Метод получения списка всех адресов текущего диапазона.
     *
     * @return список IP-адресов
     */
    public Set<InetSocketAddress> getAddresses() {
        Set<InetSocketAddress> results = new HashSet<>();
        BigInteger inc = new BigInteger("1");
        BigInteger bufferIp = startIp;

        results.add(getIp(bufferIp));
        while (!Objects.equals(bufferIp, stopIp)) {
            bufferIp = bufferIp.add(inc);
            InetSocketAddress inetSocketAddress = getIp(bufferIp);
            results.add(inetSocketAddress);
        }
        return results;
    }

    /**
     * Метод получения начального IP-адреса диапазона.
     *
     * @return IP-адрес в строковом представлении
     */
    public String first() {
        return sourceRange.split("-")[0];
    }

    /**
     * Метод проверки вхождения IP-адреса в строковом представлении в диапазон.
     * @param ip проверяемый IP-адрес в строковом представлении
     *
     * @throws InetAddressException исключение, возникающее при передаче неверного значения проверяемого адреса
     * @return результат проверки - {@code true}, если IP-адрес входит в диапазон, иначе {@code false}
     */
    public boolean contains(String ip) {
        try {
            BigInteger currentIp = new BigInteger(1, InetAddress.getByName(ip).getAddress());
            return (this.startIp.compareTo(currentIp) < 0 && stopIp.compareTo(currentIp) > 0);
        } catch (Exception xep) {
            log.warn("wrong IP address: {}", xep.getMessage());
            throw new InetAddressException(xep.getMessage(), xep);
        }
    }

    /**
     * Метод получения объекта {@link InetSocketAddress} из значения текущего IP-адреса.
     * <p>Если массив байтов, представляющий собой IP-адрес, содержит более 4 байтов (имеет начальный знаковый байт), то
     * первый байт должен быть обрезан - значение IP-адреса всегда положительное числовое значение.
     *
     * @throws InetAddressException исключение, которое возникает в результате неверной обработки адреса удаленного хоста
     * @return текущий {@link InetSocketAddress}
     */
    private InetSocketAddress getIp(BigInteger currentIp) {
        try {
            byte[] startIpBytes = currentIp.toByteArray();
            byte[] truncatedBytes = (startIpBytes.length == 5)
                    ? Arrays.copyOfRange(startIpBytes, 1, 5)
                    : startIpBytes;
            return new InetSocketAddress(InetAddress.getByAddress(truncatedBytes), 554);
        } catch (Exception xep) {
            log.warn("wrong IP address: {}", xep.getMessage());
            throw new InetAddressException(xep.getMessage(), xep);
        }
    }

    /**
     * Метод сравнения двух IP-диапазонов.
     * @param range диапазон адресов для сравнения
     * @return -1, 0 или 1 если значение меньше чем, равно или больше чем заданное.
     */
    @Override
    public int compareTo(@NotNull IpV4AddressRange range) {
        return this.startIp.compareTo(range.startIp);
    }

}