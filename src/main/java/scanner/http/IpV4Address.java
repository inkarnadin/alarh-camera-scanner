package scanner.http;

import lombok.Data;

@Data
public class IpV4Address {

    private final int part1;
    private final int part2;
    private final int part3;
    private final int part4;

    private final String ipAsString;

    public IpV4Address(String ip) {
        String[] splitIp = ip.split("\\.");
        this.part1 = Integer.parseInt(splitIp[0]);
        this.part2 = Integer.parseInt(splitIp[1]);
        this.part3 = Integer.parseInt(splitIp[2]);
        this.part4 = Integer.parseInt(splitIp[3]);

        this.ipAsString = ip;
    }

    public IpV4Address(int part1, int part2, int part3, int part4) {
        this.part1 = part1;
        this.part2 = part2;
        this.part3 = part3;
        this.part4 = part4;

        ipAsString = part1 + "." + part2 + "." + part3 + "." + part4;
    }

}
