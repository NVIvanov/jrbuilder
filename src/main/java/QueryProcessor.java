import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
        query = query.replaceAll("\\$\\{.*}", "NULL");
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
            result.add(new Column(metaData.getColumnName(i), "${" + metaData.getColumnName(i) + "}",
                    90, metaData.getColumnClassName(i)));
        }
        resultSet.close();
        return result;
    }
}
