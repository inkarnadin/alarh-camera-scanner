package scanner.brute;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import scanner.CameraScanExecutor;
import scanner.SourceReader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
public class IpBruteFilter {

    private static final Set<String> excludedSet = new HashSet<>();

    public static boolean excludeFakeCamera(String ip) {
        CameraScanExecutor executor = new CameraScanExecutor(new InetSocketAddress(ip, 554));
        executor.setTimeout(2000);
        Optional<String> checkPortResult = executor.call();
        if (checkPortResult.isEmpty()) {
            log.debug("{} is wrong, deleted", ip);
            excludedSet.add(ip);
            return true;
        }
        return false;
    }

    @SneakyThrows
    public static void cleaning(String pathToSourceFile) {
        List<String> baseCameraList = SourceReader.readSource(pathToSourceFile);
        for (String ip : excludedSet)
            baseCameraList.remove(ip);

        try (FileOutputStream stream = new FileOutputStream(new File(pathToSourceFile)); PrintWriter writer = new PrintWriter(stream)) {
            baseCameraList.forEach(writer::println);
        }
        excludedSet.clear();
    }

}