import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.IOException;

/**
 * @author nivanov
 *         on 02.08.17.
 */
public class LoadForm {
    private JButton loadReportButton;
    private JButton createReportButton;
    private JPanel loadForm;

    public LoadForm() {
        ReportLoader reportLoader = new ReportLoader();
        JFileChooser fc = new JFileChooser(System.getProperty("user.home"));
        createReportButton.addActionListener(e -> {
            FileNameExtensionFilter filter = new FileNameExtensionFilter("JasperReport files", "jrxml");
            fc.setFileFilter(filter);
            int retVal = fc.showSaveDialog(createReportButton);
            if (retVal == JFileChooser.APPROVE_OPTION) {
                try {
                    String filePath = fc.getSelectedFile().getPath();
                    if (!filePath.endsWith(".jrxml"))
                        filePath += ".jrxml";
                    reportLoader.createNewReport(filePath);
                    Report report = reportLoader.loadReport(filePath);
                    new ReportForm(report);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        loadReportButton.addActionListener(e -> {
            int retVal = fc.showOpenDialog(createReportButton);
            if (retVal == JFileChooser.APPROVE_OPTION) {
                Report report = reportLoader.loadReport(fc.getSelectedFile().getPath());
                new ReportForm(report);
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("LoadForm");
        frame.setContentPane(new LoadForm().loadForm);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
