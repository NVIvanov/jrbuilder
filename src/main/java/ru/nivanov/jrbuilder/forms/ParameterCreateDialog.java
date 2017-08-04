package ru.nivanov.jrbuilder.forms;

import ru.nivanov.jrbuilder.report.Parameter;
import ru.nivanov.jrbuilder.report.Report;

import javax.swing.*;
import java.awt.event.*;

public class ParameterCreateDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField titleField;
    private JTextField typeField;
    private JTextField defaultExpressionField;
    private Report report;

    ParameterCreateDialog(Report report) {
        this.report = report;
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setTitle("Добавление параметра");
        setSize(400, 300);
        setLocationRelativeTo(null);
        buttonOK.addActionListener(e -> onOK());
        buttonCancel.addActionListener(e -> onCancel());
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });
        contentPane.registerKeyboardAction(e -> onCancel(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        report.addParameter(new Parameter(titleField.getText().trim(), typeField.getText().trim(),
                defaultExpressionField.getText().trim()));
        dispose();
    }

    private void onCancel() {
        dispose();
    }
}
