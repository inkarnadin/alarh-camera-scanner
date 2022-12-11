package scanner.http.ip;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
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
@RequiredArgsConstructor
@EqualsAndHashCode
public class IpV4AddressRange implements Comparable<IpV4AddressRange> {

    private String sourceRange;
    private String name;
    private BigInteger startIp;
    private BigInteger stopIp;
    private long count;
    private boolean isLarge;

    /**
     * Конструктор создания объекта диапазона адресов.
     * <p>В качестве обязательного параметра принимает значение диапазона в формате <i>11.10.1.0-11.10.1.255</i>.
     * @param range диапазон адресов в строковом представлении заданного формата
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

        count = stopIp.subtract(startIp).longValue();
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
     * @return результат проверки - {@code true}, если IP-адрес входит в диапазон, иначе {@code false}
     */
    @SneakyThrows
    public boolean contains(String ip) {
        BigInteger currentIp = new BigInteger(1, InetAddress.getByName(ip).getAddress());
        return (this.startIp.compareTo(currentIp) < 0 && stopIp.compareTo(currentIp) > 0);
    }

    /**
     * Метод получения объекта {@link InetSocketAddress} из значения текущего IP-адреса.
     * <p>Если массив байтов, представляющий собой IP-адрес, содержит более 4 байтов (имеет начальный знаковый байт), то
     * первый байт должен быть обрезан - значение IP-адреса всегда положительное числовое значение.
     *
     * @return текущий {@link InetSocketAddress}
     */
    @SneakyThrows
    private InetSocketAddress getIp(BigInteger currentIp) {
        try {
            byte[] startIpBytes = currentIp.toByteArray();
            byte[] truncatedBytes = (startIpBytes.length == 5)
                    ? Arrays.copyOfRange(startIpBytes, 1, 5)
                    : startIpBytes;
            return new InetSocketAddress(InetAddress.getByAddress(truncatedBytes), 554);
        } catch (Exception xep) {
            log.warn("wrong byte array of IP address: {}", xep.getMessage());
            throw xep;
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