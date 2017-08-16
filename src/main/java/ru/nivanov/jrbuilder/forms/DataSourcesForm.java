package ru.nivanov.jrbuilder.forms;

import javax.swing.*;
import java.awt.event.*;

/**
 * @author nivanov
 *         on 14.08.17.
 */
public class DataSourcesForm extends JDialog{
    private JPanel dataSourcesForm;
    private JButton buttonSave;
    private JTextField titleField;
    private JTextField jdbcField;
    private JTable dataSourceTable;
    private JPanel contentPane;
    private ReportForm parent;

    DataSourcesForm(ReportForm parent) {
        this.parent = parent;
        setContentPane(dataSourcesForm);
        setTitle("Data sources");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);
        dataSourceTable.setModel(new DatasourceTableModel());

        ActionListener listener = e -> {
            if (titleField.getText().isEmpty() || jdbcField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(dataSourcesForm, "Название или URL источника не должны быть пустыми");
                return;
            }
            DatasourceTableModel model = (DatasourceTableModel) dataSourceTable.getModel();
            if (model.containsDatasource(titleField.getText())) {
                JOptionPane.showMessageDialog(dataSourcesForm, "Источник данных с таким названием уже существует");
                return;
            }
            ((DatasourceTableModel) dataSourceTable.getModel()).addDataSource(titleField.getText(), jdbcField.getText());
            dataSourceTable.updateUI();
        };
        buttonSave.addActionListener(listener);
        jdbcField.addActionListener(listener);
        titleField.addActionListener(e -> {
            jdbcField.grabFocus();
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                ((DatasourceTableModel) dataSourceTable.getModel()).updateDatasourceList();
                super.windowClosing(e);
                parent.update();
            }
        });

        JPopupMenu popup = new JPopupMenu();
        JButton button = new JButton("Удалить");
        button.addActionListener(e1 -> {
            int rowIndex = dataSourceTable.getSelectedRow();
            String dataSourceName =
                    (String) dataSourceTable.getModel().getValueAt(rowIndex, 0);
            ((DatasourceTableModel)dataSourceTable.getModel()).removeDataSource(dataSourceName);
            dataSourceTable.updateUI();
            popup.setVisible(false);
        });
        popup.add(button);
        dataSourceTable.setComponentPopupMenu(popup);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });
        contentPane.registerKeyboardAction(e -> onCancel(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onCancel() {
        ((DatasourceTableModel) dataSourceTable.getModel()).updateDatasourceList();
        parent.update();
        dispose();
    }

}
