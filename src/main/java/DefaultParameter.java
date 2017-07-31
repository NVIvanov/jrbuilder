import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author nivanov
 *         on 31.07.17.
 */
public class DefaultParameter implements Parameter {
    private String name, type, defaultExpression;

    public DefaultParameter(String name, String type, String defaultExpression) {
        this.name = name;
        this.type = type;
        this.defaultExpression = defaultExpression;
    }

    @Override
    public Element getXML(Document placement) {
        Element parameter = placement.createElement("parameter");
        parameter.setAttribute("name", name);
        parameter.setAttribute("class", type);
        Element defaultValueExpression = placement.createElement("defaultValueExpression");
        defaultValueExpression.appendChild(placement.createTextNode(ReportUtil.formatTextExpression(defaultExpression)));
        parameter.appendChild(defaultValueExpression);
        return parameter;
    }

    @Override
    public String getName() {
        return name;
    }
}
