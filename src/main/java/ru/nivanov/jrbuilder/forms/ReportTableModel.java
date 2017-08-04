package ru.nivanov.jrbuilder.forms;

import ru.nivanov.jrbuilder.report.Report;

import javax.swing.table.AbstractTableModel;

/**
 * @author nivanov
 *         on 03.08.17.
 */
public abstract class ReportTableModel extends AbstractTableModel {
    protected Report report;

    ReportTableModel(Report report) {
        this.report = report;
    }
}
