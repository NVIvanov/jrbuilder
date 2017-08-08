package ru.nivanov.jrbuilder.utils;

import ru.nivanov.jrbuilder.report.Column;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author nivanov
 *         on 02.08.17.
 */
public class QueryProcessor {
    private final String dataSourceUrl;
    private final String username;
    private final String password;

    public QueryProcessor(String dataSourceUrl, String connector, String username, String password) {
        this.dataSourceUrl = dataSourceUrl;
        this.username = username;
        this.password = password;
        try {
            Class.forName(connector);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public List<Column> getColumns(String query) throws SQLException {
        Connection connection;
        Statement statement;
        query = query.replaceAll("\\$P\\{[\\w]*}", "NULL");
        connection = DriverManager.getConnection(dataSourceUrl, username, password);
        statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);
        List<Column> columns = getColumns(resultSet);
        statement.close();
        connection.close();
        return columns;
    }

    private List<Column> getColumns(ResultSet resultSet) throws SQLException {
        List<Column> result = new ArrayList<>();
        ResultSetMetaData metaData = resultSet.getMetaData();
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            result.add(new Column(metaData.getColumnName(i), "$F{" + metaData.getColumnName(i) + "}",
                    90, prepareClassName(metaData.getColumnClassName(i)),
                    UUID.randomUUID().toString(), "#000000"));
        }
        resultSet.close();
        return result;
    }

    private String prepareClassName(String className) {
        if (className.equals("java.sql.Date") || className.equals("java.sql.Timestamp")) {
            return "java.lang.String";
        }
        return className;
    }
}
