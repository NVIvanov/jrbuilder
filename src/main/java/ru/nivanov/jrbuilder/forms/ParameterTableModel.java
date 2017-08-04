package ru.nivanov.jrbuilder.forms;

import ru.nivanov.jrbuilder.report.Parameter;
import ru.nivanov.jrbuilder.report.Report;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.util.ArrayList;
import java.util.List;

/**
 * @author nivanov
 *         on 03.08.17.
 */
public class ParameterTableModel extends ReportTableModel {

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
        switch (column) {
            case 1:
                return "Тип";
            case 2:
                return "Значение по умолчанию";
            default:
                return "Наименование";
        }
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
}
