
package Objects;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import Utils.Tools;
import Utils.Types;

public class DataFrame {

    private HashMap<String, ArrayList<Object>> map;
    private String[] HEADERS;
    private Type[] TYPES; // for debug only

    public static int ENDS = 0x1;
    public static int STARTS = 0x2;
    public static int NULL_IN = 0x3;
    public static int NULL_OUT = 0x4;
    public static int CONTAINS = 0x5;
    //
    // public static void main(String[] args) {
    // Predicate<Object> p = decideFilter("a", NULLS);
    //
    // System.out.println(p.test(null));
    // }

    @SuppressWarnings("rawtypes")
    public DataFrame(String[] keys, ArrayList<ArrayList<String>> values, int record_count) throws ParseException {
	HEADERS = keys;
	TYPES = (Type[]) Array.newInstance(Type.class, HEADERS.length);

	map = new HashMap<>();
	for (int i = 0; i < values.size(); i++) {
	    ArrayList<String> column = values.get(i);
	    String header = keys[i];
	    Class cType = (Class<?>) Tools.getClass(column.get(0));
	    List<Object> colArr = new ArrayList<>();

	    for (int j = 0; j < column.size(); j++) {
		Object o = column.get(j);
		// if is a number
		FlaggedObject fo = Tools.safeCast(o, cType);
		colArr.add(fo.getObject());
		if (fo.getFlag()) {
		    cType = Types.STRING;
		    colArr = colArr.stream().map(a -> Object.class.cast(a + "")).collect(Collectors.toList());
		}
	    }
	    map.put(header, new ArrayList<>(colArr));
	    TYPES[i] = cType;
	}
    }

    public void filter(String keyword, int mode, String[] fields) {
	Predicate<? super Object> comp = decideFilter(keyword, mode);
	HashMap<String, ArrayList<Object>> newMap = Tools.getInitializedMap(HEADERS);

	for (int i = 0; i < size(); i++) {
	    boolean found = false;
	    for (String header : fields) {
		ArrayList<Object> column = map.get(header);
		Class cType = (Class<?>) Tools.getClass(column.get(0));
		FlaggedObject fo = Tools.safeCast(column.get(i), cType);

		// System.out.println(fo + " == " + (fo == null));
		if (fo.getObject() == null) {
		    continue;
		}
		if (comp.test(fo.getObject().toString())) {
		    found = true;
		    break;
		}
	    }

	    if (!found)
		continue;

	    // fill in new map, if something found
	    for (int z = 0; z < HEADERS.length; z++) {
		String header = HEADERS[z];
		Class cType = (Class) TYPES[z];
		Object o = map.get(header).get(i);
		o = (o == null) ? null : cType.cast(o);
		newMap.get(header).add(o);
	    }

	}

	map = newMap; // replace old map
    }

    public int size() {
	return map.get(HEADERS[0]).size();
    }

    public static Predicate<? super Object> decideFilter(String keyword, int mode) {
	Predicate<? super Object> comp = null;
	if (mode == NULL_IN)
	    comp = s -> (Tools.isReallyNull(s));

	if (mode == NULL_OUT)
	    comp = s -> (!Tools.isReallyNull(s));

	if (mode == CONTAINS)
	    comp = s -> (s.toString().contains(keyword));

	if (mode == ENDS)
	    comp = s -> (s.toString().endsWith(keyword));

	if (mode == STARTS)
	    comp = s -> (s.toString().startsWith(keyword));
	return comp;
    }

    @Override
    public String toString() {
	StringBuilder s = new StringBuilder();
	s.append(HEADERS[0]);
	// s.append(":" + TYPES[0].getTypeName());
	for (int i = 1; i < HEADERS.length; i++) {
	    s.append(", " + HEADERS[i]);
	    // s.append(":" + TYPES[i].getTypeName());
	}
	s.append("\n");

	for (int i = 0; i < size(); i++) {
	    s.append(map.get(HEADERS[0]).get(i));
	    for (int j = 1; j < HEADERS.length; j++) {
		s.append(", " + map.get(HEADERS[j]).get(i));

	    }
	    s.append("\n");
	}
	s.append(size() + " entries.");

	return s.toString();
    }

}
