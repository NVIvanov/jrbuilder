/**
 * @author nivanov
 *         on 03.08.17.
 */
public class ColumnTableModel extends ReportTableModel {

    public ColumnTableModel(Report report) {
        super(report);
    }

    @Override
    public int getRowCount() {
        return report.getColumns().size();
    }

    @Override
    public int getColumnCount() {
        return 4;
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 1:
                return "Тип";
            case 2:
                return "Ширина";
            case 3:
                return "Функция отображения";
            default:
                return "Отображаемое имя";
        }
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
            default:
                return column.getDisplayName();
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex != 0;
    }
}
