package scanner.brute;

import lombok.SneakyThrows;
import scanner.Preferences;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Wrapper for socket management.
 *
 * @author inkarnadin
 */
public class SocketConnector {

    private final static int timeout = Integer.parseInt(Preferences.get("-bw")) * 1000;

    private final Socket socket = new Socket();

    private final InetSocketAddress address;

    public SocketConnector(String ip, int port) {
        this.address = new InetSocketAddress(ip, port);
    }

    /**
     * Open socket connection with certain timeouts.
     *
     * @throws IOException if problem with connection.
     */
    public void open() throws IOException {
        socket.setSoTimeout(timeout);
        socket.connect(this.address, timeout);
    }

    /**
     * Get connection status.
     *
     * @return true if connection is live.
     */
    public boolean isConnected() {
        return socket.isConnected();
    }

    /**
     * Close socket connection.
     */
    public void close() {
        try {
            socket.close();
        } catch (IOException ignored) {}
    }

    /**
     * Intercept the input stream.
     *
     * @return socket input stream.
     */
    @SneakyThrows
    public BufferedReader input() {
        return new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    /**
     * Intercept the output stream.
     *
     * @return socket output stream.
     */
    @SneakyThrows
    public BufferedWriter output() {
        return new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

}