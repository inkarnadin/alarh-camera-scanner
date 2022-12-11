package scanner.report;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Convert map to HTML table with two columns.
 *
 * @author inkarnadin
 */
@Deprecated
public class MapToHTMLTableConverter {

    /**
     * Convert map to table.
     *
     * @param values map of values
     * @return html table as string
     */
    public static String convert(Map<String, String> values) {
        List<String> rowList = new ArrayList<>();
        for (Map.Entry<String, String> entry : values.entrySet()) {
            String row = new StringBuilder()
                    .append(contain("td", entry.getKey()))
                    .append(contain("td", entry.getValue())).toString();
            rowList.add(contain("tr", row));
        }

        return contain("table", String.join("", rowList));
    }

    private static String contain(String tagName, String value) {
        return String.format("<%s>%s</%s>", tagName, value, tagName);
    }

}