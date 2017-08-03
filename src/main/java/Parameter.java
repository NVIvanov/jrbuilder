import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author nivanov
 *         on 31.07.17.
 */
public class Parameter {
    private String name, type, defaultExpression;

    public Parameter(String name, String type, String defaultExpression) {
        this.name = name;
        this.type = type;
        this.defaultExpression = defaultExpression;
    }

    public Element getXML(Document placement) {
        Element parameter = placement.createElement("parameter");
        parameter.setAttribute("name", name);
        parameter.setAttribute("class", type);
        Element defaultValueExpression = placement.createElement("defaultValueExpression");
        defaultValueExpression.appendChild(placement.createTextNode(ReportUtil.formatTextExpression(defaultExpression)));
        parameter.appendChild(defaultValueExpression);
        return parameter;
    }

    public Element getDatasetParameterXML(Document placement) {
        Element datasetParameter = placement.createElement("datasetParameter");
        datasetParameter.setAttribute("name", name);
        Element expression = placement.createElement("datasetParameterExpression");
        expression.appendChild(placement.createTextNode(ReportUtil.formatTextExpression("${" + name + "}")));
        datasetParameter.appendChild(expression);
        return datasetParameter;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getDefaultExpression() {
        return defaultExpression;
    }
}
