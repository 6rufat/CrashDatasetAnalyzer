package Utils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

import Objects.Date;
import Objects.FlaggedObject;

public class Tools {
    public static String DATE_FORMAT = "dd/mm/yy";
    public static String TIME_FORMAT = "hh:mm";
    public static String[] ALPHABET = new String[] { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m",
	    "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z" };

    public static boolean isReallyNull(Object o) {
	return o == null || o.toString().equals("Null");
    }

    public static HashMap<String, ArrayList<Object>> getInitializedMap(String[] headers) {
	HashMap<String, ArrayList<Object>> map = new HashMap<>();
	for (String header : headers) {
	    map.put(header, new ArrayList<>());
	}
	return map;
    }

    public static boolean containsAll(String o, String... keys) {
	for (String key : keys) {
	    if (!o.contains(key))
		return false;
	}
	return true;
    }

    public static FlaggedObject safeCast(Object o, Class type) {
	FlaggedObject pair = null;

	if (type.isInstance(1)) {
	    Object newVal = Double.parseDouble(Types.STRING.cast(o));
	    return new FlaggedObject(newVal, false);

	} else if (type.isInstance(new Objects.Date())) {
	    Object newVal = new Date(o + "");
	    return new FlaggedObject(newVal, false);

	} else if (o.toString().contains("Null")) {
	    return new FlaggedObject(null, false);
	}

	try {
	    Object newVal = type.cast(Types.STRING.cast(o).trim());
	    return new FlaggedObject(newVal, false);
	} catch (ClassCastException e) {
	    try {
		if (type.equals(Types.NUMBER)) {
		    Object newVal = Double.parseDouble(o.toString());
		    return new FlaggedObject(newVal, false);
		}
	    } catch (NumberFormatException e2) {
		return new FlaggedObject(o.toString(), true);
	    }

	}
	return pair;
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
	if (object == null)
	    return null;
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
	if (Tools.containsAny(strObj, ":", "/", "-") && strObj.length() <= 8
		&& !Tools.containsAny(strObj.toLowerCase(), Tools.ALPHABET)) {
	    return Types.DATE;
	}

	return Types.STRING;
    }
}
