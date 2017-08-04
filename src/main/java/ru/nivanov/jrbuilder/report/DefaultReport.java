package ru.nivanov.jrbuilder.report;

import org.w3c.dom.*;
import org.xml.sax.SAXException;
import ru.nivanov.jrbuilder.utils.ReportUtil;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author nivanov
 *         on 31.07.17.
 */
public class DefaultReport implements Report {
    private File source;
    private Document document;
    private List<Column> cachedColumns;
    private List<Parameter> cachedParameters;

    DefaultReport(File source) {
        this.source = source;
    }

    @Override
    public void init() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.parse(source);
            parseParameters();
            parseColumns();
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new RuntimeException("init exception", e);
        }
    }

    private void parseColumns() {
        List<Column> columns = new ArrayList<>();
        NodeList columnNodes = document.getDocumentElement().getElementsByTagName("jr:column");
        NodeList fields = document.getDocumentElement().getElementsByTagName("field");
        for (int i = 0; i < columnNodes.getLength(); i++) {
            Element columnNode = (Element) columnNodes.item(i);
            Integer width = Integer.valueOf(columnNode.getAttribute("width"));
            String uuid = columnNode.getAttribute("uuid");
            String displayName = ReportUtil.parseTextExpression(columnNode.getElementsByTagName("text").item(0)
                    .getFirstChild());
            String function = ReportUtil.parseTextExpression(columnNode.getElementsByTagName("textFieldExpression")
                    .item(0).getFirstChild());
            String type = null;
            for (int j = 0; j < fields.getLength(); j++) {
                Element field = (Element) fields.item(j);
                if (field.getAttribute("name").equals(displayName)) {
                    type = field.getAttribute("class");
                }
            }
            columns.add(new Column(displayName, function, width, type, uuid));
        }
        cachedColumns = columns;
    }

    private void parseParameters() {
        List<Parameter> parameters = new ArrayList<>();
        NodeList params = ((Element) document.getDocumentElement().getElementsByTagName("subDataset").item(0))
                .getElementsByTagName("parameter");
        for (int i = 0; i < params.getLength(); i++) {
            Element param = (Element) params.item(i);
            Node paramDefaultValue = param.getElementsByTagName("defaultValueExpression").item(0);
            parameters.add(new Parameter(param.getAttribute("name"), param.getAttribute("class"),
                    ReportUtil.parseTextExpression(paramDefaultValue.getFirstChild())));
        }
        cachedParameters = parameters;
    }

    @Override
    public void setQuery(String query) {
        checkDocument();
        Element subDataset = getDatasetElement();
        NodeList queryList = subDataset.getElementsByTagName("queryString");
        Element queryString;
        if (queryList.getLength() == 0) {
            queryString = document.createElement("queryString");
            subDataset.appendChild(queryString);
        } else {
            queryString = (Element) queryList.item(0);
        }
        queryString.removeChild(queryString.getFirstChild());
        queryString.appendChild(ReportUtil.formatTextExpression(query.trim(), document));
    }

    @Override
    public void setName(String name) {
        document.getDocumentElement().setAttribute("name", name.trim());
    }

    @Override
    public void addParameter(Parameter parameter) {
        cachedParameters.add(parameter);
    }

    private void writeParameter(Parameter parameter) {
        removeExistingParameter(parameter);
        Element parameterElement = parameter.getXML(document);
        document.getDocumentElement().appendChild(parameterElement);
        Element dataset = getDatasetElement();
        dataset.insertBefore(parameterElement, dataset.getFirstChild());
        document.getDocumentElement().insertBefore(parameterElement.cloneNode(true), dataset.getNextSibling());
        Element datasetParameter = parameter.getDatasetParameterXML(document);
        Node datasetRun = document.getDocumentElement().getElementsByTagName("datasetRun").item(0);
        datasetRun.insertBefore(datasetParameter, datasetRun.getFirstChild());
        cachedParameters.add(parameter);
    }

    @Override
    public void addColumn(Column column) {
        cachedColumns.add(column);
    }

    private void writeColumn(Column column) {
        Element columnElement = column.writeColumn(document);
        Element fieldElement = column.writeField(document);
        Element table = (Element) document.getDocumentElement().getElementsByTagName("jr:table").item(0);
        table.appendChild(columnElement);
        Element subDataset = getDatasetElement();
        subDataset.appendChild(fieldElement);
    }

    @Override
    public void removeParameter(String parameterName) {
        cachedParameters.removeIf(parameter -> parameter.getName().equals(parameterName));
    }

    @Override
    public void removeColumn(String columnName) {
        cachedColumns.removeIf(column -> column.getDisplayName().equals(columnName));
    }

    private void removeColumnsFromDocument(){
        Element dataset = getDatasetElement();
        NodeList fields = dataset.getElementsByTagName("field");
        removeAllFromDocument(fields);
        NodeList columns = document.getDocumentElement().getElementsByTagName("jr:column");
        removeAllFromDocument(columns);
    }

    private void removeParametersFromDocument(){
        NodeList parameters = document.getDocumentElement().getElementsByTagName("parameter");
        removeAllFromDocument(parameters);
        NodeList datasetParameters = document.getDocumentElement().getElementsByTagName("datasetParameter");
        removeAllFromDocument(datasetParameters);
    }

    private void removeAllFromDocument(NodeList nodes) {
        for (int i = 0; i < nodes.getLength(); i++) {
            nodes.item(i).getParentNode().removeChild(nodes.item(i));
        }
    }

    @Override
    public void clearColumns() {
        getColumns().forEach(column -> removeColumn(column.getDisplayName()));
    }

    @Override
    public String getName() {
        return document.getDocumentElement().getAttribute("name").trim();
    }

    @Override
    public String getQuery() {
        return ReportUtil.parseTextExpression(((Element) document.getDocumentElement()
                .getElementsByTagName("subDataset").item(0))
                .getElementsByTagName("queryString").item(0).getFirstChild()).trim();
    }

    @Override
    public List<Parameter> getParameters() {
        return cachedParameters;
    }

    @Override
    public List<Column> getColumns() {
        return cachedColumns;
    }

    @Override
    public void update() {
        try {
            updateDocument();
            TransformerFactory transformerFactory =
                    TransformerFactory.newInstance();
            Transformer transformer =
                    transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            DOMSource source = new DOMSource(document);
            StreamResult result =
                    new StreamResult(this.source);
            transformer.transform(source, result);
        } catch (TransformerException e) {
            throw new RuntimeException("save exception", e);
        }
    }

    private void updateDocument(){
        removeColumnsFromDocument();
        removeParametersFromDocument();
        cachedColumns.forEach(this::writeColumn);
        cachedParameters.forEach(this::writeParameter);
    }

    private Element getDatasetElement() {
        NodeList datasetList = document.getDocumentElement().getElementsByTagName("subDataset");
        if (datasetList.getLength() == 0) {
            throw new IllegalStateException("template has not any dataset");
        }
        return (Element) datasetList.item(0);
    }

    private void removeExistingParameter(Parameter parameter) {
        removeParameter(parameter.getName());
    }

    private void checkDocument(){
        if (document == null)
            throw new IllegalStateException("Document must not be null");
    }
}
