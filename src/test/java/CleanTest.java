import lombok.SneakyThrows;
import org.junit.Ignore;
import org.junit.Test;

import java.io.*;
import java.util.*;
import java.util.stream.Stream;

public class CleanTest {

    @Test
    @Ignore
    @SneakyThrows
    public void deleteDuplicatesAndSortSourceList() {
        Set<String> sources = new HashSet<>();

        InputStream in = new FileInputStream(new File("list.txt"));
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            Stream<String> lines = reader.lines();
            lines.forEach(sources::add);
            lines.close();
        }

        List<String> sortedList = new ArrayList<>(sources);
        Collections.sort(sortedList);

        try (FileOutputStream stream = new FileOutputStream(new File("list.txt")); PrintWriter writer = new PrintWriter(stream)) {
            sortedList.forEach(writer::println);
        }
    }

}