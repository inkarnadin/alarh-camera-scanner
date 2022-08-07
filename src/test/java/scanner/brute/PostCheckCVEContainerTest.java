package scanner.brute;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class PostCheckCVEContainerTest {

    @Test
    public void cve_non_empty() {
        PostCheckCVEContainer checkCVEContainer = new PostCheckCVEContainer();
        checkCVEContainer.addCredentials("check:check");

        assertFalse(checkCVEContainer.isEmpty());
    }

    @Test
    public void cve_is_empty() {
        PostCheckCVEContainer checkCVEContainer = new PostCheckCVEContainer();

        assertTrue(checkCVEContainer.isEmpty());
    }

    @Test
    public void cve_add_password() {
        PostCheckCVEContainer checkCVEContainer = new PostCheckCVEContainer();
        checkCVEContainer.addCredentials("check:check");
        checkCVEContainer.addCredentials("check:check");
        checkCVEContainer.addCredentials("test:password");

        Set<String> passwords = new HashSet<>();
        passwords.add("check");
        passwords.add("password");

        assertArrayEquals(passwords.toArray(), checkCVEContainer.getAdditionalPasswords().toArray());
    }

    @Test
    public void cve_exclude_address() {
        PostCheckCVEContainer checkCVEContainer = new PostCheckCVEContainer();
        checkCVEContainer.excludeAddress("8.10.20.1");
        checkCVEContainer.excludeAddress("8.10.20.12");

        Set<String> addresses = new HashSet<>();
        addresses.add("8.10.20.1");
        addresses.add("8.10.20.12");
        addresses.add("8.10.20.13");
        addresses.add("8.10.20.18");

        Set<String> remain = new HashSet<>();
        remain.add("8.10.20.13");
        remain.add("8.10.20.18");

        assertArrayEquals(remain.toArray(), checkCVEContainer.updateAddressList(addresses).toArray());
    }

}