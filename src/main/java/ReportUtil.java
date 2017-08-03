import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author nivanov
 *         on 31.07.17.
 */
public final class ReportUtil {
    private static Properties properties;
    private ReportUtil(){}

    static {
        properties = new Properties();
        try {
            properties.load(new FileInputStream("src/main/resources/application.properties"));
        } catch (IOException e) {
            e.printStackTrace();
            properties = null;
        }
    }

    public static String formatTextExpression(String text){
        return "<![CDATA[" + text + "]]>";
    }

    public static String parseTextExpression(String text) {
        Pattern pattern = Pattern.compile("<!\\[CDATA\\[(.*)]]>");
        Matcher matcher = pattern.matcher(text);
        if (matcher.matches()) {
            return matcher.group(1);
        } else {
            return "";
        }
    }

    public static String getProperty(String key) {
        if (properties == null) {
            throw new IllegalStateException("properties not initialized");
        }
        return properties.getProperty(key);
    }
}
