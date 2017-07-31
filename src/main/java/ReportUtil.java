/**
 * @author nivanov
 *         on 31.07.17.
 */
public final class ReportUtil {

    private ReportUtil(){}

    public static String formatTextExpression(String text){
        return "<![CDATA[" + text + "]]>";
    }
}
