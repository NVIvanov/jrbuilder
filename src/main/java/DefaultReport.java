import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;

/**
 * @author nivanov
 *         on 31.07.17.
 */
public class DefaultReport implements Report {
    private File source;
    private Document document;

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
            //TODO сделать возврат результата иницализации без отлова исключения извне
            e.printStackTrace();
        }
    }

    @Override
    public void setQuery(String query) {
        checkDocument();
        //TODO сделать проверку на отсутствие датасорса
        Element subDataset = (Element) document.getElementsByTagName("subDataset").item(0);
        //TODO сделать проверку на отсутствие запроса в датасорсе
        Element queryString = (Element) subDataset.getElementsByTagName("queryString").item(0);
        queryString.appendChild(document.createTextNode(ReportUtil.formatTextExpression(query)));
    }

    @Override
    public void addParameter(Parameter parameter) {
        removeExistingParameter(parameter);
        Element parameterElement = parameter.getXML(document);
        document.appendChild(parameterElement);
    }

    private void removeExistingParameter(Parameter parameter) {
        NodeList parameterList = document.getElementsByTagName("parameter");
        for (int i = 0; i < parameterList.getLength(); i++) {
            Node parameterNode = parameterList.item(i);
            NamedNodeMap attributes = parameterNode.getAttributes();
            if (attributes.getNamedItem("name").getTextContent().equals(parameter.getName())) {
                document.removeChild(parameterNode);
            }
        }
    }

    @Override
    public void addColumn(Column column) {
        Element columnElement = column.write(document);
        document.appendChild(columnElement);
    }


    @Override
    public void update() {
        try {
            TransformerFactory transformerFactory =
                    TransformerFactory.newInstance();
            Transformer transformer =
                    transformerFactory.newTransformer();
            DOMSource source = new DOMSource(document);
            StreamResult result =
                    new StreamResult(this.source);
            transformer.transform(source, result);
        } catch (TransformerException e) {
            //TODO сделать возврат результата обновления без отлова исключения извне
            e.printStackTrace();
        }
    }

    private void checkDocument(){
        if (document == null)
            throw new IllegalStateException("Document must not be null");
    }
}
