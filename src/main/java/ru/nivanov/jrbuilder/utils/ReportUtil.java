package ru.nivanov.jrbuilder.utils;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

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

    public static Node formatTextExpression(String text, Document document){
        return document.createCDATASection(text.trim());
    }

    public static String parseTextExpression(Node textElement) {
        if (textElement instanceof CharacterData) {
            return ((CharacterData) textElement).getData().trim();
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
