/**
 * @author nivanov
 *         on 31.07.17.
 */
public interface Report {
    void init();
    void setQuery(String query);
    void addParameter(Parameter parameter);
    void addColumn(Column column);
    void update();
}
