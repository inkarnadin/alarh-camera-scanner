package scanner.runner.unlock.repeat;

import org.junit.Assert;
import org.junit.Test;
import scanner.runner.Target;
import scanner.runner.unlock.Credentials;
import scanner.runner.unlock.UnlockType;
import scanner.runner.unlock.obj.UnlockInput;
import scanner.runner.unlock.obj.UnlockOutput;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public class RepeatScannerTest {

    @Test
    public void scanning_success() {
        RepeatScanner repeatScanner = new RepeatScanner();
        Target brokenTarget1 = Target.builder().host("1.1.1.1").unlockType(UnlockType.CVE).credentials(Credentials.empty()).isFreeStream(true).build();
        Target brokenTarget2 = Target.builder().host("2.2.2.2").unlockType(UnlockType.UNBROKEN).credentials(Credentials.empty()).build();
        UnlockInput unlockInput = UnlockInput.builder()
                .addresses(Collections.emptySet())
                .repeatCount(1)
                .passwords(Set.of("123", "321"))
                .build();

        Set<Target> successRepeat = repeatScanner.scanning(
                unlockInput,
                Set.of(brokenTarget1, brokenTarget2),
                bi -> {
                    Set<Target> repeatAddresses = bi.getAddresses().stream()
                            .map(x -> Target.builder()
                                        .host(x.getHost())
                                        .isFreeStream(true)
                                        .credentials(Credentials.empty())
                                        .build())
                            .collect(Collectors.toSet());
                    return UnlockOutput.builder().targets(repeatAddresses).build();
                }
        );
        Assert.assertEquals(1, successRepeat.size());

        Target target = new ArrayList<>(successRepeat).get(0);
        Assert.assertEquals("2.2.2.2", target.getHost());
        Assert.assertEquals(UnlockType.REPEAT_EMPTY, target.getUnlockType());
        Assert.assertTrue(target.isFreeStream());
        Assert.assertTrue(target.isBroken());
    }

}