import lombok.SneakyThrows;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;

public class XorDecodeTest {

    @org.junit.Test
    @SneakyThrows
    public void test() {
        File file = new File("decryptedoutput");
        byte[] fileContents = Files.readAllBytes(file.toPath());
        byte[] xorOutput = new byte[fileContents.length];

        byte[] key = {(byte) 0x73, (byte) 0x8B, (byte) 0x55, (byte) 0x44};

        for (int i = 0; i < fileContents.length; i++) {
            xorOutput[i] = (byte) ((int) fileContents[i] ^ (int) key[i % key.length]);
        }

        FileOutputStream stream = new FileOutputStream("plaintextOutput");
        stream.write(xorOutput);
    }

}