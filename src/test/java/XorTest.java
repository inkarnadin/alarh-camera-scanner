import lombok.SneakyThrows;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;

public class XorTest {

    @Test
    @SneakyThrows
    @Ignore("Functionality checking test")
    public void xor() {
        File file = new File("decrypted");
        byte[] fileContents = Files.readAllBytes(file.toPath());
        byte[] xorOutput = new byte[fileContents.length];

        byte[] key = {(byte) 0x73, (byte) 0x8B, (byte) 0x55, (byte) 0x44};

        for (int i = 0; i < fileContents.length; i++) {
            xorOutput[i] = (byte) ((int) fileContents[i] ^ (int) key[i % key.length]);
        }

        FileOutputStream stream = new FileOutputStream("output");
        stream.write(xorOutput);
    }

}