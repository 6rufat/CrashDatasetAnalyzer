import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Scanner;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import Objects.DataFrame;
import Objects.Tools;

public class MainGUI {

    /* Main container of the data */
    public static DataFrame DF;
    /* Temporary container of the data */
    public static DataFrame T;
    /* Repository where the date is stored */
    public static String data_dir = "./data/";
    /* Scaner Object */
    public static Scanner sc;

    public static void main(String[] args) throws IOException, ParseException {
	// init the program
	sc = new Scanner(System.in);
	readCSV(data_dir + "crash.csv");
	DF.collect();
	new MainGUI().start();
    }

    public void start() throws FileNotFoundException {
	/* User is prompted whether to write from temporary storage to main */
	if (T != null) {
	    System.out.println("Save? (1/0):");
	    if (sc.nextInt() == 1)
		DF = T;
	    T = null; // release
	}

	StringBuilder ui = new StringBuilder();
	ui.append("Operations:\n");
	ui.append("-------------\n");
	ui.append("1 | List\n");
	ui.append("2 | Sort\n");
	ui.append("3 | Search\n");
	ui.append("4 | Columns\n");
	ui.append("5 | Filter\n");
	ui.append("6 | Export\n");
	ui.append("7 | Exit\n");
	ui.append("-------------\n");
	ui.append("Enter Number: ");
	System.out.println(ui);
	int num = sc.nextInt();
	sc.nextLine();
	switch (num) {
	case 1:
	    // List all the entities (20 points)
	    list();
	    break;
	case 2:
	    // Sort the entities (30 points)
	    T = new DataFrame(DF);
	    System.out.println("Possible Fields: " + DF.HEADERS);

	    // a. Based on any field
	    System.out.println("Enter a Field (e.g. Time):");
	    String field = sc.nextLine();

	    // b. In any order (i.e., ASC, DESC)
	    System.out.println("Ascending? (1/0):");
	    int asc = sc.nextInt();

	    T.sort(field, asc == 1);
	    T.collect();

	    break;
	case 3:
	    // 3. Search entity(ies) based on any given field and value (15 points)
	    search();
	    break;
	case 4:
	    // 4. List column names (5 points)
	    System.out.println(DF.HEADERS);
	    break;
	case 5:
	    // 5. Filter entities (30 points)
	    filter();
	    break;
	case 6:
	    /*
	     * a. Giving an extra option to the user to EXPORT the result in the form of a
	     * report to a .csv file AFTER LISTING IT + 15 points.
	     */
	    System.out.println("Enter name for file:");
	    String name = sc.nextLine();
	    PrintWriter pw = new PrintWriter(data_dir + name + ".csv");
	    pw.write(DF.toCSV());
	    pw.close();
	case 7:
	    sc.close();
	    System.exit(0);
	}
	start();
    }

    public void filter() {
	StringBuilder ui = new StringBuilder();
	ui.append("1 | Strings\n");
	ui.append("2 | Numeric\n");
	ui.append("3 | Date\n");
	ui.append("4 | clustID\n");
	ui.append("-------------\n");
	ui.append("Enter Number: ");
	System.out.println(ui);

	// a. Based on any given field or set of fields and according to some rules:

	int num = sc.nextInt();
	sc.nextLine();
	switch (num) {
	case 1:
	    // b. string fields
	    filterString();
	    break;
	case 2:
	    // c. date, time, and numeric fields
	    filterNumeric();
	    break;
	case 3:
	    // d. date and time
	    filterDate();
	    break;
	case 4:
	    // e. [IF you take clustId as a Boolean value (i.e., is high fatality]
	    System.out.println("High Fatality? (1/0):");
	    int n = sc.nextInt();
	    sc.nextLine();
	    T = new DataFrame(DF);
	    // i. Equal (eq) [true or false]
	    String key = (n == 1) ? "High" : "Low";

	    T.filter(key, DataFrame.STRING_CONTAINS, new String[] { "ClustID" });
	    break;
	}
	T.collect();
    }

    /**
     * d. date and time
     */
    public void filterDate() {
	StringBuilder ui = new StringBuilder();
	ui.append("1 | Specify Year\n");
	ui.append("2 | Specify Month\n");
	ui.append("3 | Specify Day\n");

	ui.append("__________\n");
	ui.append("Enter Number: ");
	System.out.println(ui);
	int num = sc.nextInt();
	sc.nextLine();

	T = new DataFrame(DF);
	System.out.println("Enter Value:");
	int dmy = sc.nextInt();
	sc.nextLine();

	int sw = 0;
	switch (num) {
	case 1:
	    // i. in a specific year (y)
	    sw = DataFrame.DATE_YEAR;
	    break;
	case 2:
	    // ii. in a specific month (m)
	    sw = DataFrame.DATE_MONTH;
	    break;
	case 3:
	    // iii. in a specific day (d)
	    sw = DataFrame.DATE_DAY;
	    break;
	}
	T.filter(dmy + "", sw, new String[] { "Date" });
    }

    /**
     * c. date, time, and numeric fields
     */
    public void filterNumeric() {
	StringBuilder ui = new StringBuilder();
	ui.append("1 | Equals\n");
	ui.append("2 | Greater Than\n");
	ui.append("3 | Less Than\n");
	ui.append("4 | Greater and Equal to\n");
	ui.append("5 | Less and Equal to\n");
	ui.append("6 | Between\n");
	ui.append("__________\n");
	ui.append("Enter Number: ");
	System.out.println(ui);
	int num = sc.nextInt();
	sc.nextLine();

	T = new DataFrame(DF);
	System.out.println("Possible Fields: " + Tools.getHeadersOfType(T, Tools.Types.NUMBER));
	System.out.println("Enter Fields & Values (e.g. SurvivalRate 0.3):");
	ArrayList<String> in = new ArrayList<String>(Arrays.asList(sc.nextLine().split(" ")));

	switch (num) {
	case 1:
	    // i. equal (eq)
	    T.filter(in.remove(in.size() - 1), DataFrame.NUMERIC_EQUALITY, in.toArray(new String[0]));
	    break;
	case 2:
	    // ii. greater than (gt)
	    T.filter(in.remove(in.size() - 1), DataFrame.NUMERIC_GT, in.toArray(new String[0]));

	    break;
	case 3:
	    // iii. less than (lt)
	    T.filter(in.remove(in.size() - 1), DataFrame.NUMERIC_LT, in.toArray(new String[0]));

	    break;
	case 4:
	    // iv. greater and equal to (ge)
	    T.filter(in.remove(in.size() - 1), DataFrame.NUMERIC_GE, in.toArray(new String[0]));
	    break;
	case 5:
	    // v. less and equal to (le)
	    T.filter(in.remove(in.size() - 1), DataFrame.NUMERIC_LE, in.toArray(new String[0]));
	    break;
	case 6:
	    // vi. between (bt)
	    T.filter(in.remove(in.size() - 1) + " " + in.remove(in.size() - 1), DataFrame.NUMERIC_BT,
		    in.toArray(new String[0]));
	    break;
	// vii: null can be filtered otherwise, no need for reimplementation. Use String
	}

    }

    /**
     * b. string fields
     */
    public void filterString() {
	StringBuilder ui = new StringBuilder();
	ui.append("1 | Starts With\n");
	ui.append("2 | Ends With\n");
	ui.append("3 | Contains\n");
	ui.append("4 | Has Null\n");
	ui.append("5 | No Null\n");
	ui.append("__________\n");
	ui.append("Enter Number: ");
	System.out.println(ui);
	int num = sc.nextInt();
	sc.nextLine();

	T = new DataFrame(DF);
	System.out.println("Possible Fields: " + Tools.getHeadersOfType(T, Tools.Types.STRING));

	ArrayList<String> in = null;
	;
	switch (num) {
	case 1:
	    // i. starts with
	    System.out.println("Enter Fields & Keyword(e.g. Location Lo):");
	    in = new ArrayList<String>(Arrays.asList(sc.nextLine().split(" ")));
	    T.filter(in.remove(in.size() - 1), DataFrame.STRING_STARTS, in.toArray(new String[0]));
	    break;
	case 2:
	    // ii. ends with
	    System.out.println("Enter Fields & Keyword(e.g. Location Lo):");
	    in = new ArrayList<String>(Arrays.asList(sc.nextLine().split(" ")));
	    T.filter(in.remove(in.size() - 1), DataFrame.STRING_ENDS, in.toArray(new String[0]));
	    break;
	case 3:
	    // iii. contains
	    System.out.println("Enter Fields & Keyword(e.g. Location Lo):");
	    in = new ArrayList<String>(Arrays.asList(sc.nextLine().split(" ")));
	    T.filter(in.remove(in.size() - 1), DataFrame.STRING_CONTAINS, in.toArray(new String[0]));
	    break;
	case 4:
	    // iv. Filter in entries with null values
	    System.out.println("Enter Field (e.g. Time):");
	    in = new ArrayList<String>(Arrays.asList(sc.nextLine().split(" ")));
	    T.filter(null, DataFrame.STRING_NULL_IN, in.toArray(new String[0]));
	    break;
	case 5:
	    // iv. Filter out entries with null values
	    System.out.println("Enter Field (e.g. Time):");
	    in = new ArrayList<String>(Arrays.asList(sc.nextLine().split(" ")));
	    T.filter(null, DataFrame.STRING_NULL_OUT, in.toArray(new String[0]));
	}
    }

    public void search() {
	StringBuilder ui = new StringBuilder();
	ui.append("1 | Key\n");
	ui.append("2 | Number\n");
	ui.append("3 | Date\n");
	ui.append("__________\n");
	ui.append("Enter Number: ");
	System.out.println(ui);
	int num = sc.nextInt();
	sc.nextLine();
	String[] in = null;
	switch (num) {
	case 1:
	    /*
	     * a. string fields and values must be checked based on contains not exact
	     * equality.
	     */
	    T = new DataFrame(DF);
	    System.out.println("Possible Names: " + DF.HEADERS);
	    System.out.println("Enter Field and Value (e.g. Location London):");
	    in = sc.nextLine().split(" ");
	    T.search(in[0], in[1]);
	    T.collect();
	    break;
	case 2:
	    /*
	     * b. non-string fields and values must be checked based on exact equality.
	     */
	    T = new DataFrame(DF);
	    System.out.println("Possible Fields: " + Tools.getHeadersOfType(T, Tools.Types.NUMBER));
	    System.out.println("Enter Field and Value (e.g. Survivors 0):");
	    in = sc.nextLine().split(" ");

	    T.search(in[0], in[1]);
	    T.collect();
	    break;
	case 3:
	    /*
	     * b. non-string fields and values must be checked based on exact equality.
	     */
	    T = new DataFrame(DF);
	    System.out.println("Possible Fields: " + Tools.getHeadersOfType(T, Tools.Types.DATE));
	    System.out.println("Enter Field and Date(e.g. 'Date dd/mm/yy' or 'Time hh:mm'):");
	    in = sc.nextLine().split(" ");
	    T.search(in[0], in[1]);
	    T.collect();
	    break;
	}
    }

    public void list() throws FileNotFoundException {
	StringBuilder ui = new StringBuilder();
	ui.append("1 | All\n");
	ui.append("2 | Select Fields\n");
	ui.append("3 | Select Range\n");
	ui.append("__________\n");
	ui.append("Enter Number: ");
	System.out.println(ui);
	int num = sc.nextInt();
	sc.nextLine();
	switch (num) {
	case 1:
	    /*
	     * a. List all the fields of each entity
	     */
	    DF.collect();
	    break;
	case 2:
	    /*
	     * b. List only the selected fields of each entity
	     */
	    T = new DataFrame(DF);
	    System.out.println("Possible Names: " + DF.HEADERS);
	    System.out.println("Enter Fields (e.g. A B C):");
	    String[] fields = sc.nextLine().split(" ");
	    T.select(fields);
	    T.collect();
	    break;
	case 3:
	    /*
	     * c. List entities based on the range of rows (e.g., range is given, 5 100)
	     */
	    T = new DataFrame(DF);
	    System.out.println("Size = " + DF.size() + "\nEnter Range (e.g. 5 500): ");
	    String[] range = sc.nextLine().split(" ");
	    int left = Integer.parseInt(range[0]);
	    int right = Integer.parseInt(range[1]);
	    T.select(left, right);
	    T.collect();
	    break;
	}
    }

    /**
     * Method needed to read the CSV file.
     */
    public static DataFrame readCSV(String dir) throws IOException, ParseException {
	Scanner sc = new Scanner(new File(dir));
	String[] HEADERS = sc.nextLine().split(",");

	sc.close();
	FileReader in = new FileReader(new File(dir));
	@SuppressWarnings("deprecation")
	Iterator<CSVRecord> records = CSVFormat.EXCEL.withHeader(HEADERS).withFirstRecordAsHeader().parse(in)
		.iterator();

	ArrayList<ArrayList<String>> columns = new ArrayList<>();

	for (int i = 0; i < HEADERS.length; i++) {
	    columns.add(new ArrayList<>());
	}

	int record_count = 0;
	while (records.hasNext()) {
	    record_count++;
	    CSVRecord rec = records.next();

	    for (int i = 0; i < HEADERS.length; i++) {
		String header = HEADERS[i];
		String entry = rec.get(header);
		if (entry.equals("")) {
		    entry = "Null";
		}
		columns.get(i).add(entry);
	    }

	}

	DF = new DataFrame(HEADERS, columns, record_count);
	//
	return DF;
    }
}
