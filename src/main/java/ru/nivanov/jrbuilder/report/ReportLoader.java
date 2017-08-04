package ru.nivanov.jrbuilder.report;

import ru.nivanov.jrbuilder.utils.ReportUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * @author nivanov
 *         on 01.08.17.
 */
public class ReportLoader {
    private Report report;

    public Report loadReport(String filename) {
        if (report != null) {
            clearCurrentReport();
        }
        File reportFile = new File(filename);
        report = new DefaultReport(reportFile);
        report.init();
        return report;
    }

    private void clearCurrentReport(){
        report.update();
        report = null;
    }

    public void createNewReport(String path) throws IOException {
        String defaultTemplatePath = ReportUtil.getProperty("template.path");
        Files.copy(Paths.get(defaultTemplatePath), Paths.get(path), StandardCopyOption.REPLACE_EXISTING);
        report = loadReport(path);
        report.setName(getFileName(path));
        clearCurrentReport();
    }

    private String getFileName(String fullPath) {
        String[] paths = fullPath.split(File.separator);
        return paths[paths.length - 1].split("\\.")[0];
    }
}
