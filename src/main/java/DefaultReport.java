import org.w3c.dom.*;
import org.xml.sax.SAXException;

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
    private volatile boolean queryUpdated = true;
    private volatile boolean columnsUpdated = true;
    private volatile boolean paramsUpdated = true;
    private List<Column> cachedColumns;
    private List<Parameter> cachedParameters;
    private String cachedQuery;

    public DefaultReport(File source) {
        this.source = source;
    }

    @Override
    public void init() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.parse(source);
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new RuntimeException("init exception", e);
        }
    }

    @Override
    public void setQuery(String query) {
        queryUpdated = true;
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
        queryString.appendChild(document.createTextNode(ReportUtil.formatTextExpression(query).trim()));
    }

    @Override
    public void setName(String name) {
        document.getDocumentElement().setAttribute("name", name.trim());
    }

    @Override
    public void addParameter(Parameter parameter) {
        paramsUpdated = true;
        removeExistingParameter(parameter);
        Element parameterElement = parameter.getXML(document);
        document.getDocumentElement().appendChild(parameterElement);
        Element dataset = getDatasetElement();
        dataset.appendChild(parameterElement);
        Element datasetParameter = parameter.getDatasetParameterXML(document);
        Node datasetRun = document.getDocumentElement().getElementsByTagName("datasetRun").item(0);
        datasetRun.insertBefore(datasetParameter, datasetRun.getFirstChild());
    }

    @Override
    public void addColumn(Column column) {
        columnsUpdated = true;
        Element columnElement = column.writeColumn(document);
        Element fieldElement = column.writeField(document);
        Element table = (Element) document.getDocumentElement().getElementsByTagName("jr:table").item(0);
        table.appendChild(columnElement);
        Element subDataset = getDatasetElement();
        subDataset.appendChild(fieldElement);
    }

    @Override
    public void removeParameter(String parameterName) {
        paramsUpdated = true;
        Element dataset = getDatasetElement();
        removeParameterFromNode(dataset, parameterName);
        removeParameterFromNode(document.getDocumentElement(), parameterName);
        Element datasetRun = (Element) document.getDocumentElement().getElementsByTagName("datasetRun").item(0);
        removeParameterFromNode(datasetRun, parameterName);
    }

    @Override
    public void removeColumn(String columnName) {
        columnsUpdated = true;
        NodeList fieldList = document.getDocumentElement().getElementsByTagName("field");
        removeElementByNameFromNode(fieldList, "name", columnName);
        Element table = (Element) document.getDocumentElement().getElementsByTagName("jr:table").item(0);
        NodeList columnList = table.getElementsByTagName("jr:column");
        removeElementByNameFromNode(columnList, "uuid", columnName);
    }

    @Override
    public void clearColumns() {
        columnsUpdated = true;
        getColumns().forEach(column -> removeColumn(column.getDisplayName()));
    }

    @Override
    public String getName() {
        return document.getDocumentElement().getAttribute("name").trim();
    }

    @Override
    public String getQuery() {
        if (queryUpdated) {
            queryUpdated = false;
            cachedQuery = ((Element) document.getDocumentElement().getElementsByTagName("subDataset").item(0))
                    .getElementsByTagName("queryString").item(0).getTextContent().trim();
        }
        return cachedQuery;
    }

    @Override
    public List<Parameter> getParameters() {
        if (paramsUpdated) {
            paramsUpdated = false;
            List<Parameter> parameters = new ArrayList<>();
            NodeList params = document.getDocumentElement().getElementsByTagName("parameter");
            for (int i = 0; i < params.getLength(); i++) {
                Element param = (Element) params.item(i);
                Node paramDefaultValue = param.getElementsByTagName("defaultValueExpression").item(0);
                parameters.add(new Parameter(param.getAttribute("name"), param.getAttribute("class"),
                        ReportUtil.parseTextExpression(paramDefaultValue.getTextContent())));
            }
            cachedParameters = parameters;
        }
        return cachedParameters;
    }

    @Override
    public List<Column> getColumns() {
        if (columnsUpdated) {
            columnsUpdated = false;
            List<Column> columns = new ArrayList<>();
            NodeList fields = ((Element)document.getDocumentElement().getElementsByTagName("subDataset").item(0))
                    .getElementsByTagName("field");
            NodeList columnNodes = document.getDocumentElement().getElementsByTagName("jr:column");
            for (int i = 0; i < fields.getLength(); i++) {
                Element column = (Element) fields.item(i);
                String columnName = column.getAttribute("name");
                Element columnInTable = null;
                for (int j = 0; j < columnNodes.getLength(); j++) {
                    columnInTable = (Element) columnNodes.item(j);
                    if (columnInTable.getAttribute("uuid").equals(columnName)) {
                        break;
                    }
                }
                if (columnInTable == null) {
                    throw new IllegalStateException("column can not be null!");
                }
                Element textFieldExpression = (Element) columnInTable.getElementsByTagName("textFieldExpression").item(0);
                columns.add(new Column(column.getAttribute("name"),
                        ReportUtil.parseTextExpression(textFieldExpression.getTextContent()),
                        Integer.valueOf(columnInTable.getAttribute("width")),
                        column.getAttribute("class")));
            }
            cachedColumns = columns;
        }
        return cachedColumns;
    }

    @Override
    public void update() {
        try {
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

    private Element getDatasetElement() {
        NodeList datasetList = document.getDocumentElement().getElementsByTagName("subDataset");
        if (datasetList.getLength() == 0) {
            throw new IllegalStateException("template has not any dataset");
        }
        return (Element) datasetList.item(0);
    }

    private void removeElementByNameFromNode(NodeList candidates, String attributeName, String value) {
        for (int i = 0; i < candidates.getLength(); i++) {
            Node node = candidates.item(i);
            NamedNodeMap attributes = node.getAttributes();
            if (attributes != null && attributes.getNamedItem(attributeName) != null &&
                    attributes.getNamedItem(attributeName).getTextContent().equals(value)) {
                node.getParentNode().removeChild(node);
            }
        }
    }

    private void removeExistingParameter(Parameter parameter) {
        removeParameter(parameter.getName());
    }

    private void removeParameterFromNode(Node node, String name) {
        NodeList parameterList = node.getChildNodes();
        removeElementByNameFromNode(parameterList, "name", name);
    }

    private void checkDocument(){
        if (document == null)
            throw new IllegalStateException("Document must not be null");
    }
}
