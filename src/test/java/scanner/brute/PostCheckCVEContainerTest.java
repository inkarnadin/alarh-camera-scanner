package scanner.brute;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class PostCheckCVEContainerTest {

    @Test
    public void testNonEmpty() {
        PostCheckCVEContainer checkCVEContainer = new PostCheckCVEContainer();
        checkCVEContainer.add("8.10.20.11", "check:check");

        assertFalse(checkCVEContainer.isEmpty());
    }

    @Test
    public void testIsEmpty() {
        PostCheckCVEContainer checkCVEContainer = new PostCheckCVEContainer();

        assertTrue(checkCVEContainer.isEmpty());
    }

    @Test
    public void testAddPassword() {
        PostCheckCVEContainer checkCVEContainer = new PostCheckCVEContainer();
        checkCVEContainer.add("8.10.20.11", "check:check");
        checkCVEContainer.add("8.10.20.1", "check:check");
        checkCVEContainer.add("8.10.20.12", "test:password");

        Set<String> passwords = new HashSet<>();
        passwords.add("check");
        passwords.add("password");

        assertArrayEquals(passwords.toArray(), checkCVEContainer.getAdditionalPasswords().toArray());
    }

    @Test
    public void testUpdateAddressList() {
        PostCheckCVEContainer checkCVEContainer = new PostCheckCVEContainer();
        checkCVEContainer.add("8.10.20.1", "check:check");
        checkCVEContainer.add("8.10.20.12", "test:password");

        List<String> addresses = new ArrayList<>();
        addresses.add("8.10.20.1");
        addresses.add("8.10.20.12");
        addresses.add("8.10.20.13");
        addresses.add("8.10.20.18");

        List<String> remain = new ArrayList<>();
        remain.add("8.10.20.13");
        remain.add("8.10.20.18");

        assertArrayEquals(remain.toArray(), checkCVEContainer.updateAddressList(addresses).toArray());
    }

}