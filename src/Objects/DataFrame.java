
package Objects;

import java.util.ArrayList;
import java.util.Iterator;

public class DataFrame {
    Row headers;
    ArrayList<Row> rows;

    public DataFrame(Object[] objects) {
	rows = new ArrayList<>();
	headers = new Row(objects);
    }

    public void add(Row row) {
	rows.add(row);
    }

    public void add(Object[] entries) {
	rows.add(new Row(headers.values(), entries));
    }

    public Row remove(int i) {
	return rows.remove(i);
    }

    public boolean remove(Row row) {
	return rows.remove(row);
    }

    private static String format(Object str) {
	return (str == null || str.toString().length() < 2) ? (str.toString() + "\t") : str.toString();
    }

    @Override
    public String toString() {
	String retval = "| ID " + headers + "\n";
	int l = retval.length();
	for (int i = 0; i < l - 2; i++)
	    retval += "_";
	retval += '\n';

	for (Row row : rows) {
	    Iterator<Object> h_iter = headers.iterator();
	    retval += "| " + row.id + " | ";
	    while (h_iter.hasNext()) {
		retval += format(row.get(h_iter.next())) + " | ";
	    }
	    retval += '\n';
	}

	return retval;
    }

}
