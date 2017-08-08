package ru.nivanov.jrbuilder.forms;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * @author nivanov
 *         on 08.08.17.
 */
class ColorChooserEditor extends AbstractCellEditor implements TableCellEditor {

    private JButton delegate = new JButton();
    private Color savedColor;

    ColorChooserEditor() {
        ActionListener actionListener = actionEvent -> {
            Color color = JColorChooser.showDialog(delegate, "Color Chooser", savedColor);
            ColorChooserEditor.this.changeColor(color);
        };
        delegate.addActionListener(actionListener);
    }

    public Object getCellEditorValue() {
        return savedColor;
    }

    private void changeColor(Color color) {
        if (color != null) {
            savedColor = color;
            delegate.setBackground(color);
        }
    }

    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
                                                 int row, int column) {
        changeColor(new Color(
                Integer.valueOf( ((String)value).substring( 1, 3 ), 16 ),
                Integer.valueOf( ((String)value).substring( 3, 5 ), 16 ),
                Integer.valueOf( ((String)value).substring( 5, 7 ), 16 ) ));
        return delegate;
    }
}
