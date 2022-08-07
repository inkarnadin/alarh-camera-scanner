import lombok.SneakyThrows;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParseTest {

    @Test
    @SneakyThrows
    @Ignore("Functionality checking test")
    public void parse() {
        File file = new File("output");
        byte[] fileContents = Files.readAllBytes(file.toPath());

        Pattern ptn = Pattern.compile("[\\w$&+,:;=?@#.*]+");
        Matcher matcher = ptn.matcher(new String(fileContents));

        List<String> list = new ArrayList<>();
        while (matcher.find()) {
            list.add(matcher.group(0));
        }
        System.out.println(list);
    }

}