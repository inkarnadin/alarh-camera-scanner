package scanner.runner;

import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import scanner.Preferences;
import scanner.http.ip.IpV4AddressRange;
import scanner.http.ip.RangeUtils;
import scanner.recover.RecoveryElement;
import scanner.recover.RecoveryManager;

import java.lang.reflect.Field;
import java.util.*;

import static scanner.recover.RecoveryElement.STOP_SCAN_RANGE;

public class RangeUtilsTest {

    private static final Set<String> list = new TreeSet<>();

    @BeforeClass
    public static void init() {
        list.add("163.35.0.0-163.35.255.255");
        list.add("163.36.0.0-163.36.255.255");
        list.add("163.37.0.0-163.37.255.255");
        list.add("163.38.0.0-163.38.255.255");
    }

    @Test
    public void prepare_with_recovery() {
        Preferences.configure(new String[] {"-recovery_scanning:true", "-source:list.txt"});
        setStopScanValue("163.36.0.0-163.36.255.255");
        List<IpV4AddressRange> preparedList = RangeUtils.prepare(list);

        System.out.println(preparedList);
        Assert.assertEquals(3, preparedList.size());
        Assert.assertTrue(preparedList.stream().anyMatch(f -> Objects.equals(f.getSourceRange(), "163.36.0.0-163.36.255.255")));
        Assert.assertTrue(preparedList.stream().anyMatch(f -> Objects.equals(f.getSourceRange(), "163.37.0.0-163.37.255.255")));
        Assert.assertTrue(preparedList.stream().anyMatch(f -> Objects.equals(f.getSourceRange(), "163.38.0.0-163.38.255.255")));
    }

    @Test
    public void prepare_with_recovery_missing_range() {
        Preferences.configure(new String[] {"-recovery_scanning:true", "-source:list.txt"});
        setStopScanValue("163.30.0.0-163.30.255.255");
        List<IpV4AddressRange> preparedList = RangeUtils.prepare(list);

        System.out.println(preparedList);
        Assert.assertTrue(preparedList.isEmpty());
    }

    @Test
    public void prepare_without_recovery() {
        Preferences.configure(new String[] {"-recovery_scanning:false", "-source:list.txt"});
        setStopScanValue("163.36.0.0-163.36.255.255");
        List<IpV4AddressRange> preparedList = RangeUtils.prepare(list);

        System.out.println(preparedList);
        Assert.assertEquals(4, preparedList.size());
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    private static void setStopScanValue(String stopScanValue) {
        Field field = RecoveryManager.class.getDeclaredField("restoredData");
        field.setAccessible(true);
        Map<RecoveryElement, String> map = (Map<RecoveryElement, String>) field.get(RecoveryManager.class);
        map.put(STOP_SCAN_RANGE, stopScanValue);
        System.out.println(map);
        field.set(RecoveryManager.class, map);
        field.setAccessible(false);
    }

}