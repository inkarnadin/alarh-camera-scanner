package scanner.report;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class MapToHTMLTableConverterTest {

    @Test
    public void convert_success() {
        Map<String, String> values = new HashMap<>() {{
            put("valueForColumn1", "valueForColumn2");
        }};
        String controlStringValue = "<table><tr><td>valueForColumn1</td><td>valueForColumn2</td></tr></table>";

        Assert.assertEquals(controlStringValue, MapToHTMLTableConverter.convert(values));
    }

}