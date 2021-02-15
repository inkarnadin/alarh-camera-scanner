package scanner;

import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        List<String> params = Arrays.asList(args);

        if (params.contains("-b")) {
            List<String> list = SourceReader.readSource("/range-list.txt");

            final BruteForce bruteForce = new BruteForce();
            int i = 0;
            for (String ip : list) {
                bruteForce.brute(ip);
                System.out.println("progress: " + i++ + "/" + list.size());
            }
        }

        if (params.contains("-c")) {
            List<String> list = SourceReader.readSource("/range-list.txt");

            final PortScanner scanner = new PortScanner("result.txt");
            int c = 0;
            for (String range : list) {
                scanner.prepareSinglePortScanning(range, 8000);
                scanner.scanning();
                scanner.checkCve20134975();
                scanner.flush();

                System.out.println("===================");
                System.out.println("progress: " + c++ + "/" + list.size());
            }
        }
        System.exit(0);
    }

}