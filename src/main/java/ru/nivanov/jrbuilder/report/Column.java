package ru.nivanov.jrbuilder.report;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import ru.nivanov.jrbuilder.utils.ReportUtil;

/**
 * @author nivanov
 *         on 31.07.17.
 */
public class Column {
    private String displayName, valueFunction, type, uuid, color;
    private Integer width;

    public Column(String displayName, String valueFunction, Integer width, String type, String uuid, String color) {
        this.displayName = displayName;
        this.valueFunction = valueFunction;
        this.width = width;
        this.type = type;
        this.uuid = uuid;
        this.color = color;
    }

    Element writeField(Document placement) {
        Element fieldElement = placement.createElement("field");
        fieldElement.setAttribute("name", displayName);
        fieldElement.setAttribute("class", type);
        Element fieldDescription = placement.createElement("fieldDescription");
        fieldDescription.appendChild(ReportUtil.formatTextExpression("", placement));
        fieldElement.appendChild(fieldDescription);
        return fieldElement;
    }

    Element writeColumn(Document placement) {
        Element columnElement = placement.createElement("jr:column");
        columnElement.setAttribute("width", width.toString());
        columnElement.setAttribute("uuid", uuid);
        Element columnHeader = createHeader(placement);
        columnElement.appendChild(columnHeader);
        Element detailCell = createDetailCell(placement);
        columnElement.appendChild(detailCell);
        return columnElement;
    }

    private Element createDetailCell(Document placement) {
        Element detailCell = placement.createElement("jr:detailCell");
        detailCell.setAttribute("style", "table_TD");
        detailCell.setAttribute("height", "20");
        Element textField = placement.createElement("textField");
        textField.setAttribute("isStretchWithOverflow", "true");
        textField.setAttribute("isBlankWhenNull","true");

        Element reportElement = createReportElement(placement, 20, color);
        Element textElement = createTextElement(placement);
        Element textFieldExpression = placement.createElement("textFieldExpression");
        textFieldExpression.appendChild(ReportUtil.formatTextExpression(valueFunction, placement));
        textField.appendChild(reportElement);
        textField.appendChild(textElement);
        textField.appendChild(textFieldExpression);
        detailCell.appendChild(textField);
        return detailCell;
    }

    private Element createHeader(Document placement) {
        Element columnHeader = placement.createElement("jr:columnHeader");
        columnHeader.setAttribute("style", "table_CH");
        columnHeader.setAttribute("height", "30");
        Element staticText = placement.createElement("staticText");
        Element reportElement = createReportElement(placement,30, null);
        Element textElement = createTextElement(placement);
        Element text = placement.createElement("text");
        text.appendChild(ReportUtil.formatTextExpression(displayName, placement));
        staticText.appendChild(reportElement);
        staticText.appendChild(textElement);
        staticText.appendChild(text);
        columnHeader.appendChild(staticText);
        return columnHeader;
    }

    private Element createTextElement(Document placement) {
        Element textElement = placement.createElement("textElement");
        textElement.setAttribute("textAlignment", "Center");
        textElement.setAttribute("verticalAlignment", "Middle");
        return textElement;
    }

    private Element createReportElement(Document placement, Integer height, String color) {
        Element reportElement = placement.createElement("reportElement");
        reportElement.setAttribute("x","0");
        reportElement.setAttribute("y", "0");
        reportElement.setAttribute("width", width.toString());
        reportElement.setAttribute("height", height.toString());
        if (color != null && !color.isEmpty()) {
            reportElement.setAttribute("backcolor", color);
        }
        return reportElement;
    }

    @Override
    public String toString() {
        return "ru.nivanov.jrbuilder.report.Column{" +
                "displayName='" + displayName + '\'' +
                ", valueFunction='" + valueFunction + '\'' +
                ", type='" + type + '\'' +
                ", width=" + width +
                '}';
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getValueFunction() {
        return valueFunction;
    }

    public String getType() {
        return type;
    }

    public Integer getWidth() {
        return width;
    }

    public String getUuid() {
        return uuid;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setValueFunction(String valueFunction) {
        this.valueFunction = valueFunction;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
