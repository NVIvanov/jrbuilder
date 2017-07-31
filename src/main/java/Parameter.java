import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author nivanov
 *         on 31.07.17.
 */
public interface Parameter {
    Element getXML(Document placement);
    String getName();
}
