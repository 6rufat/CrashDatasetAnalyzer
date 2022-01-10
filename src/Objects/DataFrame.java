
package Objects;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class DataFrame {

    private HashMap<String, ArrayList<Object>> map;
    public ArrayList<String> HEADERS;
    public HashMap<String, Class<?>> TYPES; // for debug only

    public static int STRING_ENDS = 0x1;
    public static int STRING_STARTS = 0x2;
    public static int STRING_NULL_IN = 0x3;
    public static int STRING_NULL_OUT = 0x4;
    public static int STRING_CONTAINS = 0x5;

    public static int NUMERIC_EQUALITY = 0x6;
    public static int NUMERIC_GT = 0x7;
    public static int NUMERIC_LT = 0x8;
    public static int NUMERIC_GE = 0x9;
    public static int NUMERIC_LE = 0xA;
    public static int NUMERIC_BT = 0xB;

    public static int DATE_YEAR = 0xC;
    public static int DATE_MONTH = 0xD;
    public static int DATE_DAY = 0xE;

    public static int BOOLEAN = 0xF;

    public static int RANGE = 0x11;
    public static int FIELDS = 0x12;

    @SuppressWarnings("unchecked")
    public DataFrame(DataFrame df) {
	this.map = (HashMap<String, ArrayList<Object>>) df.map.clone();
	this.HEADERS = (ArrayList<String>) df.HEADERS.clone();
	this.TYPES = (HashMap<String, Class<?>>) df.TYPES.clone();
    }

    public DataFrame(String[] keys, ArrayList<ArrayList<String>> values, int record_count) throws ParseException {
	HEADERS = new ArrayList<>(Arrays.asList(keys));
	TYPES = new HashMap<>();

	map = new HashMap<>();
	for (int i = 0; i < values.size(); i++) {
	    ArrayList<String> column = values.get(i);
	    String header = keys[i];
	    Class<?> cType = (Class<?>) Tools.getClass(column.get(0));
	    List<Object> colArr = new ArrayList<>();

	    for (int j = 0; j < column.size(); j++) {
		Object o = column.get(j);

		// fix date formats
		if (cType == Tools.Types.DATE)
		    o = o.toString().replaceAll("-", "/");

		// if is a number
		FlaggedObject fo = Tools.safeCast(o, cType);
		colArr.add(fo.getObject());
		if (fo.getFlag()) {
		    cType = Tools.Types.STRING;
		    colArr = colArr.stream().map(a -> Object.class.cast(a + "")).collect(Collectors.toList());
		}
	    }
	    map.put(header, new ArrayList<>(colArr));
	    TYPES.put(header, cType);
	}
    }

    // SEARCH---------------------------->
    public void search(String field, String value) {
	int mode = (TYPES.get(field).equals(Tools.Types.NUMBER)) ? NUMERIC_EQUALITY : STRING_CONTAINS;
	filter(value, mode, new String[] { field });
    }

    // <---------------------------SEARCH

    // SORT---------------------------->
    private static Comparator<Object> comparator;
    private int sort_index;

    public void sort(String field) {
	sort(field, true);
    }

    public void sort(String field, boolean asc) {
	// order: true -> ascend, false -> descend

	// find index of header
	for (int i = 0; i < HEADERS.size(); i++) {
	    if (field.equals(HEADERS.get(i))) {
		sort_index = i;
		break;
	    }
	}

	// Create Comparator
	Class<?> cType = TYPES.get(field);
	comparator = Tools.compare(cType, asc);

	sort(0, size() - 1);

    }

    private int partition(int low, int high) {
	ArrayList<Object> arr = map.get(HEADERS.get(sort_index));

	int i = (low - 1); // index of smaller element
	for (int j = low; j < high; j++) {

	    if (comparator.compare(arr.get(j), arr.get(high)) >= 0) {
		i++;
		for (String header : HEADERS)
		    Tools.swap(map.get(header), i, j);
	    }
	}

	for (String header : HEADERS)
	    Tools.swap(map.get(header), i + 1, high);

	return i + 1;
    }

    private void sort(int low, int high) {
	if (low < high) {
	    int pi = partition(low, high);

	    // Recursively sort elements before
	    // partition and after partition
	    sort(low, pi - 1);
	    sort(pi + 1, high);
	}
    }

    // <----------------------------SORT

    // ---------------------------->FILTER
    public void filter(String keyword, int mode, String[] fields) {
	Predicate<? super Object> comp = Tools.decideFilter(keyword, mode);
	HashMap<String, ArrayList<Object>> newMap = Tools.getInitializedMap(HEADERS);

	for (int i = 0; i < size(); i++) {
	    boolean found = false;
	    for (String header : fields) {
		ArrayList<Object> column = map.get(header);
		Class<?> cType = TYPES.get(header);
		FlaggedObject fo = Tools.safeCast(column.get(i), cType);

		if (Tools.isReallyNull(fo.getObject()) && mode == STRING_NULL_OUT) {
		    found = false;
		    continue;
		}

		if (comp.test(fo.getObject().toString())) {
		    found = true;

		    if (mode != STRING_NULL_OUT)
			break;
		}
	    }

	    if (!found)
		continue;

	    // fill in new map, if something found
	    for (int z = 0; z < HEADERS.size(); z++) {
		String header = HEADERS.get(z);
		Class<?> cType = TYPES.get(header);
		Object o = map.get(header).get(i);
		o = (Tools.isReallyNull(o)) ? "Null" : cType.cast(o);
		newMap.get(header).add(o);
	    }

	}

	map = newMap; // replace old map
    }

    // <----------------------------FILTER

    // SELECT---------------------------->
    @SuppressWarnings("unchecked")
    public void select(String[] fields, boolean selectOut) {
	/* Modify HEADERS */
	ArrayList<String> HEADERS_COMPLEMENT = (ArrayList<String>) HEADERS.clone();

	if (!selectOut) {
	    for (String str : fields)
		HEADERS_COMPLEMENT.remove(str);

	    HEADERS = new ArrayList<>(Arrays.asList(fields));

	} else {
	    HEADERS_COMPLEMENT = new ArrayList<>(Arrays.asList(fields));

	    for (String str : fields)
		HEADERS.remove(str);
	}

	/* Modify DATA */
	for (String str : HEADERS_COMPLEMENT)
	    map.remove(str);

    }

    public void select(String[] fields) {
	select(fields, false);
    }

    public void select(int left, int right) {
	if (left >= right) {
	    left = 0;
	    right = 0;
	    // delete all
	}

	/*
	 * Note: Removing from the end is the key, since the size of list decreases as
	 * we remove
	 */
	for (int i = size() - 1; i >= right; i--)
	    for (String str : HEADERS)
		map.get(str).remove(i);

	for (int i = left - 1; i >= 0; i--)
	    for (String str : HEADERS)
		map.get(str).remove(i);

    }

    // <----------------------------SELECT

    public int size() {
	return map.get(HEADERS.get(0)).size();
    }

    public void collect() {
	System.out.println(toString());
    }

    @Override
    public String toString() {
	StringBuilder s = new StringBuilder();
	s.append("-: " + HEADERS.get(0));
	// s.append(":" + TYPES.get(HEADERS.get(0)).getTypeName());
	for (int i = 1; i < HEADERS.size(); i++) {
	    s.append(", " + HEADERS.get(i));
	    // s.append(":" + TYPES.get(HEADERS.get(i)).getTypeName());
	}
	s.append("\n");

	for (int i = 0; i < size(); i++) {
	    String o = Tools.camouflageNull(map.get(HEADERS.get(0)).get(i).toString(), "------");
	    s.append(i + ": " + o);
	    for (int j = 1; j < HEADERS.size(); j++) {
		o = Tools.camouflageNull(map.get(HEADERS.get(j)).get(i).toString(), "------");
		s.append(", " + o);

	    }
	    s.append("\n");
	}
	s.append("________________\n");
	s.append("Size of Frame: " + size() + "\n");

	return s.toString();
    }

    /**
     * 
     * Needed for this: <br>
     * a. Giving an extra option to the user to EXPORT the result in the form of a
     * report to a .csv file AFTER LISTING IT + 15 points.
     * 
     */
    public String toCSV() {
	StringBuilder s = new StringBuilder();
	s.append(HEADERS.get(0));
	for (int i = 1; i < HEADERS.size(); i++) {
	    s.append("," + HEADERS.get(i));
	}
	s.append("\n");

	for (int i = 0; i < size(); i++) {

	    String o = Tools.camouflageNull(map.get(HEADERS.get(0)).get(i).toString(), "");

	    s.append(o);
	    for (int j = 1; j < HEADERS.size(); j++) {
		o = Tools.camouflageNull(map.get(HEADERS.get(j)).get(i).toString(), "");
		s.append("," + o);

	    }
	    s.append("\n");
	}

	return s.toString();
    }
}
