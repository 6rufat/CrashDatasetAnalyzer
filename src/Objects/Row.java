package Objects;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class Row {

    public int id;
    private static int AUTOINCREMENT = 0;
    private HashMap<Object, Object> entries;

    public Row(Object[] headers, Object[] values) {
	this.id = AUTOINCREMENT++;
	entries = new HashMap<>();

	for (int i = 0; i < headers.length; i++) {
	    entries.put(headers[i], values[i]);
	}

    }

    public Row(Object[] values) {
	this.id = AUTOINCREMENT++;
	entries = new HashMap<>();

	for (int i = 0; i < values.length; i++) {
	    entries.put(new Integer(i), values[i]);
	}

    }

    public Row() {
	this.id = AUTOINCREMENT++;
	entries = new HashMap<>();
    }

    public boolean hasNull() {
	boolean retval = false;
	for (Object key : entries.keySet()) {
	    boolean isNull = entries.get(key) == null || ((String) entries.get(key)).equals("");
	    retval |= isNull;
	}
	return retval;
    }

    public Object get(Object header) {
	return entries.get(header);
    }

    public Object remove(Object header) {
	return entries.remove(header);
    }

    public void set(Object header, Object entry) {
	entries.put(header, entry);
    }

    public Iterator<Object> iterator() {
	return entries.values().iterator();
    }

    public Object[] values() {
	return entries.values().toArray();
    }

    @Override
    public String toString() {
	Iterator<Object> o = entries.keySet().iterator();
	String retval = "| " + entries.get(o.next()) + " | ";
	while (o.hasNext())
	    retval += entries.get(o.next()) + " | ";

	return retval;

    }

}
