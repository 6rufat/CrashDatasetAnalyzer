
package Objects;

import java.io.ObjectInputStream.GetField;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

public class DataFrame {

    private HashMap<String, Object[]> map;
    private String[] HEADERS;
    public int size;

    public DataFrame(String[] keys, ArrayList<ArrayList<String>> values, int record_count) throws ParseException {
	HEADERS = keys;
	size = record_count;
	map = new HashMap<>();
	for (int i = 0; i < values.size(); i++) {
	    ArrayList<String> column = values.get(i);
	    String header = keys[i];
	    Class cType = (Class<?>) getClass(column.get(0));
	    Object[] colArr = (Object[]) Array.newInstance(cType, column.size());

	    for (int j = 0; j < column.size(); j++) {
		Object o = column.get(j);
		// if is a number
		if (o.toString().contains("Null")) {
		    o = null;
		}
		if (cType.isInstance(1)) {
		    o = Double.parseDouble(Types.STRING.cast(o));
		} else if (cType.isInstance(new Date())) {
		    if (o != null) {
			String format = (o.toString().contains(":")) ? Tools.TIME_FORMAT : Tools.DATE_FORMAT;
			o = new SimpleDateFormat(format).parse(Types.STRING.cast(o));
		    } else {
			o = new Date() {
			    @Override
			    public String toString() {
				return null;
			    }
			};
		    }
		}

		try {
		    colArr[j] = cType.cast(o);
		} catch (ArrayStoreException | ClassCastException e) {
		    cType = Types.STRING;
		    Object[] newCopy = (Object[]) Array.newInstance(cType, colArr.length);
		    for (int z = 0; z < colArr.length; z++) {
			newCopy[z] = cType.cast(colArr[z]);
		    }
		    colArr = newCopy;

		}

	    }
	    map.put(header, colArr);
	}
    }

    public static Type getClass(Object object) {
	String strObj = object.toString();

	// check if it is possible to convert to number
	try {
	    Double d = Double.parseDouble(strObj);
	    return Types.NUMBER;
	} catch (Exception e) {
	}

	if (strObj.length() <= 8 && (strObj.contains("/") || strObj.contains(":"))) {
	    return Types.DATE;
	}

	return Types.STRING;
    }

    public void add(String key, Object[] value) {
	map.put(key, value);
    }

    @Override
    public String toString() {
	StringBuilder s = new StringBuilder();
	s.append(HEADERS[0]);
	for (int i = 1; i < HEADERS.length; i++) {
	    s.append(", " + HEADERS[i]);
	}
	s.append("\n");

	for (int i = 0; i < size; i++) {
	    s.append(map.get(HEADERS[0])[i]);
	    for (int j = 1; j < HEADERS.length; j++) {
		s.append(", " + map.get(HEADERS[j])[i]);
	    }
	    s.append("\n");
	}

	return s.toString();
    }

}
