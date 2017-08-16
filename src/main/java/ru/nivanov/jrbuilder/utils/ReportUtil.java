package ru.nivanov.jrbuilder.utils;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;

/**
 * @author nivanov
 *         on 31.07.17.
 */

@SuppressWarnings("unchecked")
public final class ReportUtil {
    private static final char DEFAULT_LIST_DELIMITER = ',';
    private static final String dataSourcesStorage;
    private static PropertiesConfiguration properties;
    private ReportUtil(){}

    static {
        try {
            properties = new PropertiesConfiguration("application.properties");
            properties.setListDelimiter(DEFAULT_LIST_DELIMITER);
        } catch (ConfigurationException e) {
            e.printStackTrace();
            properties = null;
        } finally {
            dataSourcesStorage = getProperty("dataSources.storage");
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

    public static void storeDataSources(List<DataSource> dataSourceList) {
        Path path = Paths.get(dataSourcesStorage);
        if (!Files.isWritable(path)) {
            throw new IllegalStateException("data sources path must be writable");
        }
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path.toAbsolutePath().toString()))) {
            for (DataSource dataSource : dataSourceList) {
                oos.writeObject(dataSource);
            }
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<DataSource> getDataSourceList() {
        Path path = Paths.get(dataSourcesStorage);
        if (!Files.isReadable(path)) {
            throw new IllegalStateException("data sources path must be readable");
        }
        List<DataSource> dataSourceList = new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toAbsolutePath().toString()))) {
            while (true) {
                dataSourceList.add((DataSource) ois.readObject());
            }
        } catch (EOFException exp) {
            return dataSourceList;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return emptyList();
    }

    public static String getProperty(String key) {
        validatePropertySource();
        return properties.getString(key);
    }

    public static void putProperty(String key, String value) {
        validatePropertySource();
        properties.setProperty(key, value);
        try {
            properties.save();
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
    }

    private static void validatePropertySource() {
        if (properties == null) {
            throw new IllegalStateException("properties not initialized");
        }
    }
}
