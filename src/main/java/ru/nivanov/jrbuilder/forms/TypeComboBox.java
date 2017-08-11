package ru.nivanov.jrbuilder.forms;

import javax.swing.*;

/**
 * @author nivanov
 *         on 08.08.17.
 */
class TypeComboBox extends JComboBox<String> {
    private static String[] types = new String[]{
        "java.lang.Integer", "java.lang.Double", "java.lang.Byte", "java.lang.Short", "java.lang.Float",
            "java.lang.String", "java.util.Date", "java.sql.Timestamp", "java.math.BigDecimal",
            "java.math.BigInteger"
    };

    TypeComboBox() {
        super(types);
    }
}
