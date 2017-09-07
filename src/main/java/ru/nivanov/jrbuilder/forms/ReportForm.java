package ru.nivanov.jrbuilder.forms;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
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
import java.util.ArrayList;
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

    void update() {
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

        queryArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                report.setQuery(queryArea.getText());
            }
        });
    }

    private void setUpActions() {
        createUpdateButtonPopup();
        updateQueryButton.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == (getProperty("last.datasource") == null ? 1 : 3)) {
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
        try {
            List<DataSource> dataSourceList = getDataSourceList();
            dataSourceList.forEach(ds -> {
                JMenuItem item = new JMenuItem(new UpdateAction(ds));
                updateButtonPopup.add(item);
            });
        } catch (Exception e) {
            JOptionPane.showMessageDialog(reportForm, "Ошибка при считывании файла с источниками данных");
        }
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

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        reportForm = new JPanel();
        reportForm.setLayout(new GridBagLayout());
        reportNameLabel = new JLabel();
        reportNameLabel.setFont(new Font(reportNameLabel.getFont().getName(), reportNameLabel.getFont().getStyle(), 16));
        reportNameLabel.setText("Label");
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 5.0;
        reportForm.add(reportNameLabel, gbc);
        queryArea = new JTextArea();
        queryArea.setFont(new Font("Courier New", queryArea.getFont().getStyle(), 14));
        queryArea.setLineWrap(true);
        queryArea.setMargin(new Insets(10, 10, 10, 10));
        queryArea.setToolTipText("Введите SQL-запрос");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 4;
        gbc.weightx = 1.0;
        gbc.weighty = 0.2;
        gbc.fill = GridBagConstraints.BOTH;
        reportForm.add(queryArea, gbc);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        reportForm.add(panel1, gbc);
        addParameterButton = new JButton();
        addParameterButton.setText("Добавить параметр");
        panel1.add(addParameterButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JSplitPane splitPane1 = new JSplitPane();
        splitPane1.setDividerLocation(300);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 4;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        reportForm.add(splitPane1, gbc);
        final JScrollPane scrollPane1 = new JScrollPane();
        scrollPane1.setToolTipText("Пример использования параметра в запросе: $P{имя_параметра}");
        splitPane1.setLeftComponent(scrollPane1);
        scrollPane1.setBorder(BorderFactory.createTitledBorder("Список параметров"));
        parametersTable = new JTable();
        parametersTable.setDoubleBuffered(false);
        scrollPane1.setViewportView(parametersTable);
        final JScrollPane scrollPane2 = new JScrollPane();
        scrollPane2.setToolTipText("Колонки отображаются в таблице отчета. Если список колонок пуст, то введите запрос в поле выше и нажмите \"Обновить колонки\"");
        splitPane1.setRightComponent(scrollPane2);
        scrollPane2.setBorder(BorderFactory.createTitledBorder("Список колонок"));
        columnsTable = new JTable();
        scrollPane2.setViewportView(columnsTable);
        saveButton = new JButton();
        saveButton.setText("Сохранить");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.EAST;
        reportForm.add(saveButton, gbc);
        updateQueryButton = new JButton();
        updateQueryButton.setText("Обновить колонки");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.EAST;
        reportForm.add(updateQueryButton, gbc);
        dataSourcesButton = new JButton();
        dataSourcesButton.setFocusTraversalPolicyProvider(false);
        dataSourcesButton.setText("Источники данных");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.EAST;
        reportForm.add(dataSourcesButton, gbc);
        reportNameLabel.setLabelFor(scrollPane1);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return reportForm;
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
                    updateColumns(newColumns);
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

        private void updateColumns(List<Column> newColumns) {
            List<Column> currentColumns = report.getColumns();
            newColumns = new ArrayList<>(newColumns);
            newColumns.replaceAll(column -> {
                if (currentColumns.contains(column)) {
                    return currentColumns.get(currentColumns.indexOf(column));
                }
                return column;
            });
            report.clearColumns();
            newColumns.forEach(report::addColumn);
            report.setQuery(queryArea.getText().trim());
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
