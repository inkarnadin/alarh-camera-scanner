package scanner.rtsp;

import lombok.SneakyThrows;
import scanner.Preferences;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

import static scanner.Preferences.SOCKET_WAITING;

/**
 * Класс управления состоянием сокет-соединения.
 *
 * @author inkarnadin
 * on 01-01-2022
 */
public class SocketConnector {

    private final static int TIMEOUT = Integer.parseInt(Preferences.get(SOCKET_WAITING));

    private final Socket socket = new Socket();

    private final InetSocketAddress address;

    /**
     * Конструктор создания объекта управления состоянием сокета.
     *
     * @param ip целевой адрес
     * @param port целевой порт
     */
    public SocketConnector(String ip, int port) {
        this.address = new InetSocketAddress(ip, port);
    }

    /**
     * Метод открытия сокет-соединения с указанным таймаутом на блокировку.
     *
     * @throws IOException ошибка ввода-вывода последует в случае неудачной попытки открытия соединения
     */
    public void open() throws IOException {
        socket.setSoTimeout(TIMEOUT);
        socket.connect(this.address, TIMEOUT);
    }

    /**
     * Метод получения состояния сокет-соединения.
     *
     * @return {@code true} если соединение активно, иначе {@code false}
     */
    public boolean isConnected() {
        return socket.isConnected();
    }

    /**
     * Метод закрытия сокет-соединения.
     * <p>Игнорирует получение ошибки ввода-вывода.
     */
    public void close() {
        try {
            socket.close();
        } catch (IOException ignored) {}
    }

    /**
     * Метод перехвата входящих данных по сокет соединению для возможности последующего прерывания без блокировки потока.
     *
     * @return входящий поток данных
     */
    @SneakyThrows
    public BufferedReader input() {
        return new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    /**
     * Метод перехвата исходящих данных по сокет соединению для возможности последующего прерывания без блокировки потока.
     *
     * @return исходящий поток данных
     */
    @SneakyThrows
    public BufferedWriter output() {
        return new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

}