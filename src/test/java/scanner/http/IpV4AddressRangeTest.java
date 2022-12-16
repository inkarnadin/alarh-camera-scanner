package scanner.http;

import org.junit.Test;
import scanner.exception.InetAddressException;
import scanner.http.ip.IpV4AddressRange;

import static org.junit.Assert.*;

public class IpV4AddressRangeTest {

    @Test
    public void getAddresses_small() {
        IpV4AddressRange range = new IpV4AddressRange("2.1.2.0-2.1.2.255");
        assertEquals(256, range.getAddresses().size());
    }

    @Test
    public void getAddresses_large() {
        IpV4AddressRange range = new IpV4AddressRange("255.1.0.1-255.5.255.255");
        assertEquals(327679, range.getAddresses().size());
    }

    @Test
    public void get_first() {
        IpV4AddressRange range = new IpV4AddressRange("255.1.0.1-255.5.255.255");
        assertEquals("255.1.0.1", range.first());
    }

    @Test
    public void range_contains_ip() {
        IpV4AddressRange range = new IpV4AddressRange("255.1.0.1-255.5.255.255");
        assertTrue(range.contains("255.1.0.3"));
    }

    @Test
    public void range_not_contains_ip() {
        IpV4AddressRange range = new IpV4AddressRange("255.1.0.1-255.5.255.255");
        assertFalse(range.contains("255.0.0.3"));
    }

    @Test(expected = InetAddressException.class)
    public void range_contains_ip_wrong() {
        IpV4AddressRange range = new IpV4AddressRange("255.1.0.1-255.5.255.255");
        range.contains("1000.0.0.0");
    }

    @Test
    public void check_format_equals() {
        IpV4AddressRange range = new IpV4AddressRange("10.100.12.01-10.100.12.001");
        assertEquals(1, range.getAddresses().size());
    }

    @Test
    public void check_count() {
        IpV4AddressRange range = new IpV4AddressRange("10.100.12.0-10.100.12.255");
        assertEquals(255L, range.getCount());
    }

    @Test(expected = InetAddressException.class)
    public void getIp_wrong_address() {
        IpV4AddressRange range = new IpV4AddressRange("0.0.0.0-10.100.12.255");
        range.getAddresses();
    }

}