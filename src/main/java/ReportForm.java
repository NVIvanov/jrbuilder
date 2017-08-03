import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;

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
    private Report report;

    public ReportForm(Report report) {
        QueryProcessor processor = new QueryProcessor("jdbc:mysql://localhost/hybrisdb", "com.mysql.jdbc.Driver",
                "hybrisuser", "hybrispassword");
        this.report = report;
        JFrame frame = new JFrame("ReportForm");
        frame.setContentPane(reportForm);
        frame.setSize(900, 700);
        frame.setVisible(true);

        queryArea.setText(report.getQuery());
        reportNameLabel.setText(report.getName());
        columnsTable.setModel(new ColumnTableModel(report));
        parametersTable.setModel(new ParameterTableModel(report));

        updateQueryButton.addActionListener(e -> new Thread(() -> {
            try {
                List<Column> newColumns = processor.getColumns(queryArea.getText());
                report.clearColumns();
                newColumns.forEach(report::addColumn);
                SwingUtilities.invokeAndWait(() -> columnsTable.updateUI());
            } catch (SQLException e1) {
                try {
                    SwingUtilities.invokeAndWait(() ->
                            JOptionPane.showMessageDialog(reportForm, "Произошла ошибка при обработке запроса"));
                } catch (InterruptedException | InvocationTargetException e2) {
                    e2.printStackTrace();
                }
            } catch (InterruptedException | InvocationTargetException e1) {
                e1.printStackTrace();
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
    }
}
