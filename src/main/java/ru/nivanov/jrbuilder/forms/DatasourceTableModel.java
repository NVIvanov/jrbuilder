package ru.nivanov.jrbuilder.forms;

import ru.nivanov.jrbuilder.utils.DataSource;
import ru.nivanov.jrbuilder.utils.ReportUtil;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;


/**
 * @author nivanov
 *         on 14.08.17.
 */
public class DatasourceTableModel extends AbstractTableModel {
    private String[] columnNames = {"Название", "URL"};
    private final List<DataSource> dataSources;

    DatasourceTableModel() {
        List<DataSource> sources = ReportUtil.getDataSourceList();
        if (sources == null) {
            dataSources = new ArrayList<>();
        } else {
            dataSources = sources;
        }
    }

    @Override
    public int getRowCount() {
        return dataSources.size();
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            return dataSources.get(rowIndex).getName();
        } else {
            return dataSources.get(rowIndex).getUrl();
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            dataSources.get(rowIndex).setName(aValue.toString());
        } else {
            dataSources.get(rowIndex).setUrl(aValue.toString());
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    void addDataSource(String name, String url) {
        if (name.isEmpty() || url.isEmpty()) {
            throw new IllegalArgumentException("name and url must not be empty");
        }
        dataSources.add(new DataSource(name, url));
    }

    void removeDataSource(String name) {
        if (name.isEmpty()) {
            throw new IllegalArgumentException("name must not be empty");
        }
        dataSources.removeIf(ds -> ds.getName().equals(name));
    }

    boolean containsDatasource(String name) {
        return dataSources
                .stream()
                .anyMatch(ds -> ds.getName().equals(name));
    }

    void updateDatasourceList() {
        ReportUtil.storeDataSources(dataSources);
    }
}
