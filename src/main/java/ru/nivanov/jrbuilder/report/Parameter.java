package ru.nivanov.jrbuilder.report;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import ru.nivanov.jrbuilder.utils.ReportUtil;

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

    Element getXML(Document placement) {
        Element parameter = placement.createElement("parameter");
        parameter.setAttribute("name", name);
        parameter.setAttribute("class", type);
        Element defaultValueExpression = placement.createElement("defaultValueExpression");
        defaultValueExpression.appendChild(ReportUtil.formatTextExpression(defaultExpression, placement));
        parameter.appendChild(defaultValueExpression);
        return parameter;
    }

    Element getDatasetParameterXML(Document placement) {
        Element datasetParameter = placement.createElement("datasetParameter");
        datasetParameter.setAttribute("name", name);
        Element expression = placement.createElement("datasetParameterExpression");
        expression.appendChild(ReportUtil.formatTextExpression("$P{" + name + "}", placement));
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

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDefaultExpression(String defaultExpression) {
        this.defaultExpression = defaultExpression;
    }
}
