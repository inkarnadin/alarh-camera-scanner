package scanner.crypto;

import lombok.SneakyThrows;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfigurationDecrypt {

    /**
     * <p>Decrypt HikVision configuration file through AES with static key. Similar console command:</br>
     *
     * <pre>
     * openssl enc -d -in configurationFile -out output -aes-128-ecb -K 279977f62f6cfd2d91cd75b889ce0c9a
     * </pre>
     *
     * <p>Let's decrypt the content using the XOR command.
     * <p>Parse all words from result and get potential login and password values.
     *
     * @param inputStream - input byte array
     * @return - login & password values. If not found - return all valid words
     */
    @SneakyThrows
    public static String decrypt(InputStream inputStream) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, generateKey());

            byte[] buffer = new byte[64];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byte[] output = cipher.update(buffer, 0, bytesRead);
                if (output != null)
                    outputStream.write(output);
                // ignore doFinal(), doesn't matter
            }

            // xor content
            byte[] content = outputStream.toByteArray();
            byte[] xorOutput = new byte[content.length];
            byte[] key = {(byte) 0x73, (byte) 0x8B, (byte) 0x55, (byte) 0x44};

            for (int i = 0; i < content.length; i++)
                xorOutput[i] = (byte) ((int) content[i] ^ (int) key[i % key.length]);

            return parse(new String(xorOutput), false);
        }
    }

    public static String parse(String input, boolean isRaw) {
        Pattern ptn = Pattern.compile("[\\w$&+,:;=?@#.*]+");
        Matcher matcher = ptn.matcher(input);

        List<String> list = new ArrayList<>();
        String[] logins = new String[] { "admin", "admln" };

        boolean isLogin = false;
        String activeLogin = "admin";

        while (matcher.find()) {
            String val = matcher.group(0);
            if (isRaw) {
                list.add(val);
                continue;
            }
            if (isLogin) {
                list.add(String.format("%s: %s", activeLogin, val));
                isLogin = false;
            }
            if (Arrays.asList(logins).contains(val)) {
                isLogin = true;
                activeLogin = val;
            }
        }

        // if login-pass pair more then two - remove default
        if (list.size() > 1)
            list.remove("admin: 12345");

        // if login and pass not found - return all found values
        if (list.isEmpty() && !isRaw)
            return parse(input, true);

        return list.toString();
    }

    @SneakyThrows
    private static SecretKey generateKey() {
        // certain fixed key for success decrypt
        String passphrase = "279977f62f6cfd2d91cd75b889ce0c9a";
        int len = passphrase.length();
        byte[] key = new byte[len / 2];
        for (int i = 0; i < len; i += 2)
            key[i / 2] = (byte) ((Character.digit(passphrase.charAt(i), 16) << 4) + Character.digit(passphrase.charAt(i + 1), 16));

        return new SecretKeySpec(key, "AES");
    }

}