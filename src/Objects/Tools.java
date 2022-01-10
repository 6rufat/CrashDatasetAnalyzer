package Objects;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;

/*
 * c. Using the Reflection API (package java.lang.reflect) will not only result in
	shorter and better codes but also + 20 points.
 */
import java.lang.reflect.Type;

public class Tools {
    /** Formatting of the dates */
    public static String DATE_FORMAT = "dd/mm/yy";
    /** Formatting of the time */
    public static String TIME_FORMAT = "hh:mm";

    /** Array of letters in alphabet */
    public static String[] ALPHABET = new String[] { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m",
	    "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z" };

    public static interface Types {
	public static Class<? extends Double> NUMBER = new Double(0).getClass();
	public static Class<? extends Date> DATE = new Objects.Date().getClass(); // format: mm-dd-yy
	public static Class<? extends String> STRING = new String("").getClass();

    }

    /**
     * Method that takes Type and order of sorting(asc or dsc) as input and returns
     * Comparator based on Type and order
     */
    public static Comparator<Object> compare(Class<?> cType, boolean order) {
	return new Comparator<Object>() {

	    @Override
	    public int compare(Object o1, Object o2) {

		if (order) {
		    Object t = o1;
		    o1 = o2;
		    o2 = t;
		}

		if (cType.equals(Types.NUMBER)) {
		    return (int) (Double.parseDouble(o1.toString()) * 100 - Double.parseDouble(o2.toString()) * 100);
		}
		if (cType.equals(Types.DATE)) {
		    o1 = Tools.safeCast(o1, cType).getObject();
		    o2 = Tools.safeCast(o2, cType).getObject();

		    Date d1 = (Tools.isReallyNull(o1)) ? new Date("Null") : ((Date) o1);
		    Date d2 = (Tools.isReallyNull(o2)) ? new Date("Null") : ((Date) o2);

		    return d1.compareTo(d2);
		}
		if (cType.equals(Types.STRING)) {
		    return o1.toString().compareTo(o2.toString());
		}
		return 0;
	    }

	};
    }

    /**
     * Method that identifies what type of object is the array. This method is
     * crucial since a column of type 'String' may start with Numeric-looking value.
     * Basically, sorts out Date and Numeric types and the rest that do not fit into
     * the Class description based on given Class type are going to be String type.
     */
    public static FlaggedObject safeCast(Object o, Class<?> type) {
	FlaggedObject pair = null;

	if (type == null)
	    return new FlaggedObject(o, false);

	if (type.isInstance(1)) {
	    Object newVal = Double.parseDouble(Types.STRING.cast(o));
	    return new FlaggedObject(newVal, false);

	} else if (type.isInstance(new Objects.Date())) {
	    Object newVal = new Date(o + "");
	    return new FlaggedObject(newVal, false);

	} else if (o.toString().contains("Null")) {
	    return new FlaggedObject("Null", false);
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

    /**
     * Multiplexer method that returns Predicate based on the provided operation
     * code.
     */
    public static Predicate<? super Object> decideFilter(String keyword, int mode) {
	Predicate<? super Object> comp = null;
	if (mode == DataFrame.STRING_NULL_IN)
	    comp = s -> (Tools.isReallyNull(s));

	if (mode == DataFrame.STRING_NULL_OUT)
	    comp = s -> (!Tools.isReallyNull(s));

	if (mode == DataFrame.STRING_CONTAINS)
	    comp = s -> (s.toString().contains(keyword));

	if (mode == DataFrame.STRING_ENDS)
	    comp = s -> (s.toString().endsWith(keyword));

	if (mode == DataFrame.STRING_STARTS)
	    comp = s -> (s.toString().startsWith(keyword));

	if (mode == DataFrame.NUMERIC_EQUALITY)
	    comp = s -> (Double.parseDouble(s.toString()) == Double.parseDouble(keyword));

	if (mode == DataFrame.NUMERIC_GE)
	    comp = s -> (Double.parseDouble(s.toString()) >= Double.parseDouble(keyword));

	if (mode == DataFrame.NUMERIC_GT)
	    comp = s -> (Double.parseDouble(s.toString()) > Double.parseDouble(keyword));

	if (mode == DataFrame.NUMERIC_LE)
	    comp = s -> (Double.parseDouble(s.toString()) <= Double.parseDouble(keyword));

	if (mode == DataFrame.NUMERIC_LT)
	    comp = s -> (Double.parseDouble(s.toString()) < Double.parseDouble(keyword));

	if (mode == DataFrame.NUMERIC_BT) {
	    String[] n = keyword.split(" ");
	    double a = Double.parseDouble(n[0]);
	    double b = Double.parseDouble(n[1]);
	    comp = s -> (Double.parseDouble(s.toString()) >= a && Double.parseDouble(s.toString()) <= b);
	}

	if (mode == DataFrame.DATE_DAY) {
	    comp = s -> (new Date(s.toString()).getDay() == new Date(keyword + "/0/0").getDay());
	}
	if (mode == DataFrame.DATE_MONTH) {
	    comp = s -> (new Date(s.toString()).getMonth() == new Date("0/" + keyword + "/0").getMonth());
	}
	if (mode == DataFrame.DATE_YEAR) {
	    comp = s -> (new Date(s.toString()).getYear() == new Date("0/0/" + keyword).getYear());
	}
	return comp;
    }

    @SuppressWarnings("unused")
    /**
     * Identifies the given object's type
     */
    public static Type getClass(Object object) {
	if (isReallyNull(object))
	    return Types.STRING;
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

    ////////////// HELPER METHODS ----------->
    public static boolean isReallyNull(Object o) {
	return o.toString().contains("Null");
    }

    public static String camouflageNull(Object str, String with) {
	return (isReallyNull(str)) ? with : str.toString();
    }

    public static HashMap<String, ArrayList<Object>> getInitializedMap(List<String> headers) {
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

    public static String getHeadersOfType(DataFrame df, Class<?> type) {
	StringBuilder sb = new StringBuilder();
	for (String h : df.HEADERS) {
	    if (df.TYPES.get(h).equals(type)) {
		sb.append(h + " ");
	    }
	}
	return sb.toString();
    }

    public static boolean containsAny(String o, String... keys) {
	boolean flag = false;
	for (String key : keys) {
	    if (o.contains(key))
		flag |= true;
	}
	return flag;
    }

    public static void swap(Object[] arr, int i, int j) {
	Object t = arr[i];
	arr[i] = arr[j];
	arr[j] = t;
    }

    public static void swap(ArrayList<Object> arr, int i, int j) {
	Object t = arr.get(i);
	arr.set(i, arr.get(j));
	arr.set(j, t);
    }
    ////////////// <-----------HELPER METHODS

}
