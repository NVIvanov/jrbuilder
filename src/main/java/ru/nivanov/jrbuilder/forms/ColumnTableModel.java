package ru.nivanov.jrbuilder.forms;

import ru.nivanov.jrbuilder.report.Column;
import ru.nivanov.jrbuilder.report.Report;

import java.awt.*;

/**
 * @author nivanov
 *         on 03.08.17.
 */
public class ColumnTableModel extends ReportTableModel {
    private String[] columnNames = {"Имя", "Тип", "Ширина", "Функция отображения", "Цвет"};

    ColumnTableModel(Report report) {
        super(report);
    }

    @Override
    public int getRowCount() {
        return report.getColumns().size();
    }

    @Override
    public int getColumnCount() {
        return 5;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Column column = report.getColumns().get(rowIndex);
        switch (columnIndex) {
            case 1:
                return column.getType();
            case 2:
                return column.getWidth();
            case 3:
                return column.getValueFunction();
            case 4:
                return column.getColor();
            default:
                return column.getDisplayName();
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Column column = report.getColumns().get(rowIndex);
        switch (columnIndex) {
            case 1:
                column.setType(String.valueOf(aValue));
                break;
            case 2:
                try {
                    column.setWidth(Integer.valueOf(String.valueOf(aValue)));
                } catch (Exception ignored) {}
                break;
            case 3:
                column.setValueFunction(String.valueOf(aValue));
                break;
            case 4:
                Color color = (Color) aValue;
                column.setColor(rgbString(color.getRed(), color.getGreen(), color.getBlue()));
                break;
            default:
                column.setDisplayName(String.valueOf(aValue));
        }
    }

    private String rgbString(int r, int g, int b) {
        return ("#" + getRGBCodeElement(r) + getRGBCodeElement(g) + getRGBCodeElement(b)).toUpperCase();
    }

    private String getRGBCodeElement(int digit) {
        String code = Integer.toHexString(digit);
        if (code.length() == 1)
            return "0" + code;
        return code;
    }
}
