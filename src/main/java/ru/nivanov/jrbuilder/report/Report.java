package ru.nivanov.jrbuilder.report;

import java.util.List;

/**
 * @author nivanov
 *         on 31.07.17.
 */
public interface Report {
    void init();
    void setQuery(String query);
    void setName(String name);
    void addParameter(Parameter parameter);
    void addColumn(Column column);
    void removeParameter(String parameterName);
    void removeColumn(String columnName);
    void clearColumns();
    String getName();
    String getQuery();
    List<Parameter> getParameters();
    List<Column> getColumns();
    void update();
}
