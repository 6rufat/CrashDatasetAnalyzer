
package Objects;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import Utils.Types;

public class DataFrame {

    private HashMap<String, Object[]> map;
    private String[] HEADERS;
    private Type[] TYPES; // for debug only
    public int size;

    

    @SuppressWarnings("rawtypes")
    public DataFrame(String[] keys, ArrayList<ArrayList<String>> values, int record_count) throws ParseException {
	HEADERS = keys;
	TYPES = (Type[]) Array.newInstance(Type.class, HEADERS.length);
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
		    colArr[j] = null;
		    continue;
		}
		if (cType.isInstance(1)) {
		    colArr[j] = Double.parseDouble(Types.STRING.cast(o));
		    continue;
		} else if (cType.isInstance(new Objects.Date())) {
		    colArr[j] = new Date(o + "");
		    continue;
		}

		try {
		    colArr[j] = cType.cast(Types.STRING.cast(o).trim());
		} catch (ClassCastException e) {
		    try {
			if (cType.equals(Types.NUMBER)) {
			    colArr[j] = Double.parseDouble(o.toString());
			}
		    } catch (NumberFormatException e2) {
			cType = Types.STRING;
			Object[] newCopy = (Object[]) Array.newInstance(cType, colArr.length);
			for (int z = 0; z < colArr.length; z++) {
			    newCopy[z] = colArr[z] + "";
			}
			colArr = newCopy;
		    }

		}

	    }
	    map.put(header, colArr);
	    TYPES[i] = cType;
	}
    }

    public static boolean containsAll(String o, String... keys) {
	for (String key : keys) {
	    if (!o.contains(key))
		return false;
	}
	return true;
    }

    public static boolean containsAny(String o, String... keys) {
	boolean flag = false;
	for (String key : keys) {
	    if (o.contains(key))
		flag |= true;
	}
	return flag;
    }

    @SuppressWarnings("unused")
    public static Type getClass(Object object) {
	String strObj = object.toString();

	// check if it is possible to convert to number
	try {
	    Double d = Double.parseDouble(strObj);
	    return Types.NUMBER;
	} catch (Exception e) {
	}

	/*
	 * Date and Time pattern matcher: should have {:, /, -}, less than 8 chars and
	 * no letters
	 */
	if (containsAny(strObj, ":", "/", "-") && strObj.length() <= 8
		&& !containsAny(strObj.toLowerCase(), new String[] { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
			"k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z" })) {
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
	// s.append(":" + TYPES[0].getTypeName());
	for (int i = 1; i < HEADERS.length; i++) {
	    s.append(", " + HEADERS[i]);
	    // s.append(":" + TYPES[0].getTypeName());
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
