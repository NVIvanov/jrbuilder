package ru.nivanov.jrbuilder.forms;

import ru.nivanov.jrbuilder.report.Column;
import ru.nivanov.jrbuilder.report.Report;
import ru.nivanov.jrbuilder.utils.QueryProcessor;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;

import static ru.nivanov.jrbuilder.utils.ReportUtil.getProperty;

/**
 * @author nivanov
 *         on 02.08.17.
 */
public class ReportForm {
    private JPanel reportForm;
    private JLabel reportNameLabel;
    private JTable columnsTable;
    private JTable parametersTable;
    private JTextArea queryArea;
    private JButton updateQueryButton;
    private JButton addParameterButton;
    private JButton saveButton;

    ReportForm(Report report) {
        QueryProcessor processor = new QueryProcessor(getProperty("default.datasource"), "com.mysql.jdbc.Driver",
                getProperty("datasource.username"), getProperty("datasource.password"));
        setUpStaticData(report);
        setUpColumnsTable(report);
        setUpParametersTable(report);
        setUpActions(report, processor);
    }

    private void setUpStaticData(Report report) {
        JFrame frame = new JFrame("JRBuilder");
        frame.setContentPane(reportForm);
        frame.setSize(900, 700);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        queryArea.setText(report.getQuery());
        reportNameLabel.setText(report.getName());
    }

    private void setUpActions(Report report, QueryProcessor processor) {
        updateQueryButton.addActionListener(e -> new Thread(() -> {
            try {
                List<Column> newColumns = processor.getColumns(queryArea.getText());
                report.clearColumns();
                newColumns.forEach(report::addColumn);
                report.setQuery(queryArea.getText().trim());
                SwingUtilities.invokeLater(() -> ((ColumnTableModel) columnsTable.getModel()).fireTableDataChanged());
            } catch (SQLException e1) {
                try {
                    SwingUtilities.invokeAndWait(() ->
                            JOptionPane.showMessageDialog(reportForm, "Произошла ошибка при обработке запроса"));
                } catch (InterruptedException | InvocationTargetException e2) {
                    e2.printStackTrace();
                }
            }
        }).start());

        addParameterButton.addActionListener(e -> {
            ParameterCreateDialog dialog = new ParameterCreateDialog(report);
            dialog.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    super.windowClosed(e);
                    parametersTable.updateUI();
                }
            });
            dialog.setVisible(true);
        });

        saveButton.addActionListener(e -> report.update());
    }

    private void setUpColumnsTable(Report report) {
        columnsTable.setModel(new ColumnTableModel(report));
        columnsTable.getColumnModel().getColumn(4).setCellEditor(new ColorChooserEditor());
        columnsTable.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(new TypeComboBox()));
    }

    private void setUpParametersTable(Report report) {
        parametersTable.setModel(new ParameterTableModel(report));
        parametersTable.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(
                new TypeComboBox()));
        JPopupMenu popup = new JPopupMenu();
        JButton button = new JButton("Удалить");
        button.addActionListener(e1 -> {
            int rowIndex = parametersTable.getSelectedRow();
            String parameterName =
                    (String) parametersTable.getModel().getValueAt(rowIndex, 0);
            report.removeParameter(parameterName);
            ((ParameterTableModel)parametersTable.getModel()).fireTableDataChanged();
        });
        popup.add(button);
        parametersTable.setComponentPopupMenu(popup);
    }
}
