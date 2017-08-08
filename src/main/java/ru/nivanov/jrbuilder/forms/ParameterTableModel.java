package ru.nivanov.jrbuilder.forms;

import ru.nivanov.jrbuilder.report.Parameter;
import ru.nivanov.jrbuilder.report.Report;

/**
 * @author nivanov
 *         on 03.08.17.
 */
public class ParameterTableModel extends ReportTableModel {
    private String[] columnNames = {"Имя", "Тип", "Значение по умолчанию"};

    ParameterTableModel(Report report) {
        super(report);
    }

    @Override
    public int getRowCount() {
        return report.getParameters().size();
    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Parameter parameter = report.getParameters().get(rowIndex);
        switch (columnIndex) {
            case 1:
                return parameter.getType();
            case 2:
                return parameter.getDefaultExpression();
            default:
                return parameter.getName();
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Parameter parameter = report.getParameters().get(rowIndex);
        switch (columnIndex) {
            case 1:
                parameter.setType(String.valueOf(aValue));
                break;
            case 2:
                parameter.setDefaultExpression(String.valueOf(aValue));
                break;
            default:
                parameter.setName(String.valueOf(aValue));
        }
        fireTableDataChanged();
    }
}
