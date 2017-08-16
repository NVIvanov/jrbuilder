package ru.nivanov.jrbuilder.forms;

import ru.nivanov.jrbuilder.report.Column;
import ru.nivanov.jrbuilder.report.Report;
import ru.nivanov.jrbuilder.utils.DataSource;
import ru.nivanov.jrbuilder.utils.QueryProcessor;
import ru.nivanov.jrbuilder.utils.ReportUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static ru.nivanov.jrbuilder.utils.ReportUtil.*;

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
    private JButton dataSourcesButton;
    private JPopupMenu updateButtonPopup;
    private Report report;

    ReportForm(Report report) {
        this.report = report;
        setUpStaticData();
        setUpColumnsTable();
        setUpParametersTable();
        setUpActions();
    }

    void update(){
        reportForm.updateUI();
        createUpdateButtonPopup();
    }

    private void setUpStaticData() {
        JFrame frame = new JFrame("JRBuilder");
        frame.setContentPane(reportForm);
        frame.setSize(900, 700);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        queryArea.setText(report.getQuery());
        reportNameLabel.setText(report.getName());
    }

    private void setUpActions() {
        createUpdateButtonPopup();
        updateQueryButton.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e){
                if (e.getButton() == (getProperty("last.datasource") == null ? 1:3)){
                    chooseDataSource(e);
                } else if (e.getButton() == 1) {
                    Optional<DataSource> dataSourceOptional = ReportUtil.getDataSourceList()
                            .stream()
                            .filter(ds -> ds.getName().equals(getProperty("last.datasource")))
                            .findFirst();
                    if (!dataSourceOptional.isPresent()) {
                        chooseDataSource(e);
                        return;
                    }
                    new UpdateAction(dataSourceOptional.get()).actionPerformed(null);
                }
            }

            private void chooseDataSource(MouseEvent e) {
                if (ReportUtil.getDataSourceList().isEmpty()) {
                    new DataSourcesForm(ReportForm.this);
                } else {
                    updateButtonPopup.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });

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
        dataSourcesButton.addActionListener(e -> new DataSourcesForm(this));
        String lastDataSource = getProperty("last.datasource");
        updateButtonTitle(lastDataSource);
    }

    private void createUpdateButtonPopup() {
        updateButtonPopup = new JPopupMenu();
        List<DataSource> dataSourceList = getDataSourceList();
        dataSourceList.forEach(ds -> {
            JMenuItem item = new JMenuItem(new UpdateAction(ds));
            updateButtonPopup.add(item);
        });
    }

    private void updateButtonTitle(String lastDataSource) {
        updateQueryButton.setText("Обновить колонки" + (lastDataSource != null ? " (" + lastDataSource + ")" : ""));
    }

    private void setUpColumnsTable() {
        columnsTable.setModel(new ColumnTableModel(report));
        columnsTable.getColumnModel().getColumn(4).setCellEditor(new ColorChooserEditor());
        columnsTable.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(new TypeComboBox()));
    }

    private void setUpParametersTable() {
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
            parametersTable.updateUI();
            popup.setVisible(false);
        });
        popup.add(button);
        parametersTable.setComponentPopupMenu(popup);
    }

    private class UpdateAction extends AbstractAction {
        private final DataSource dataSource;

        UpdateAction(DataSource dataSource) {
            this.dataSource = dataSource;
            this.putValue(Action.NAME, dataSource.getName());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            new Thread(() -> {
                try {
                    SwingUtilities.invokeLater(this::lockUI);
                    List<Column> newColumns = new QueryProcessor(dataSource.getUrl(), "com.mysql.jdbc.Driver",
                            getProperty("datasource.username"), getProperty("datasource.password"))
                            .getColumns(queryArea.getText());
                    report.clearColumns();
                    newColumns.forEach(report::addColumn);
                    report.setQuery(queryArea.getText().trim());
                    SwingUtilities.invokeLater(this::unlockUI);
                } catch (SQLException e1) {
                    try {
                        SwingUtilities.invokeAndWait(() -> {
                            JOptionPane.showMessageDialog(reportForm, "Произошла ошибка при обработке запроса");
                            unlockUI();
                        });
                    } catch (InterruptedException | InvocationTargetException e2) {
                        e2.printStackTrace();
                    }
                }
            }).start();
        }

        private void unlockUI() {
            columnsTable.setEnabled(true);
            updateQueryButton.setEnabled(true);
            ((ColumnTableModel) columnsTable.getModel()).fireTableDataChanged();
            putProperty("last.datasource", dataSource.getName());
            updateButtonTitle(dataSource.getName());
        }

        private void lockUI() {
            columnsTable.setEnabled(false);
            updateQueryButton.setEnabled(false);
        }
    }
}
