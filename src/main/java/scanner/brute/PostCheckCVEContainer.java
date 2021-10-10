package scanner.brute;

import lombok.Getter;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Post check CVE container.
 *
 * Passwords found as a result of exploiting the vulnerability may coincide with passwords of
 * other devices within the range for devices that are not in the vulnerability.
 *
 * To test this, a re-run of the range of values is performed with all passwords discovered
 * through the vulnerability. In this case, it is important to exclude checking for an empty
 * password and checking for already checked hosts.
 *
 * @author inkarnadin
 */
public class PostCheckCVEContainer {

    @Getter
    private final Set<String> additionalPasswords = new HashSet<>();
    private final Set<String> excludeAddresses = new HashSet<>();

    /**
     * Sign of checking that the container is not empty. If the container is empty, there is
     * no point in performing an additional range run. Moreover, if the container is not empty,
     * it makes no sense to check the availability of the stream without a password.
     *
     * @return {@code true} if exists additional passwords, else false
     */
    boolean isEmpty() {
        return additionalPasswords.isEmpty();
    }

    /**
     * Add a password that will be used when re-running the range.
     *
     * @param credentials login and password as string "login:password"
     */
    void addCredentials(String credentials) {
        if (Objects.isNull(credentials))
            return;

        String[] pair = credentials.split(":");

        if (pair.length == 2)
            additionalPasswords.add(pair[1]);
    }

    /**
     * Exclude ip from repeat check list.
     *
     * @param ip target address
     */
    void excludeAddress(String ip) {
        excludeAddresses.add(ip);
    }

    /**
     * Exclude from the incoming address list those that have already been successfully
     * verified and the password has been found.
     *
     * @param sources list of addresses
     * @return a new list of addresses
     */
    public List<String> updateAddressList(List<String> sources) {
        return sources.stream()
                .filter(f -> !excludeAddresses.contains(f))
                .collect(Collectors.toList());
    }

}