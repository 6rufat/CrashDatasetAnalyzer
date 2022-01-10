
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import Objects.DataFrame;

public class Main {
    public static String[] HEADERS = new String[] { "Date", "Time", "Location", "Operator", "Flight..", "Route", "Type",
	    "Registration", "cn.In", "Aboard", "Fatalities", "Ground", "Survivors", "SurvivalRate", "Summary",
	    "ClustID" };

    public static DataFrame DF;
    public static String data_dir = "./data/crash.csv";

    public static int COLUMN_CNT;

    public static void main(String[] args) throws Exception {

	// DF = readCSV(data_dir);
	// // Array arr = Array.newInstance(String, 5);
	// // System.out.println("3/12/50".matches());
	readCSV(data_dir);
	DF.filter(null, DF.NULL_OUT, new String[] { "Time" });
	System.out.println(DF);
	// String a = "/";

    }

    private static void defineHeaders(String dir) throws FileNotFoundException {
	Scanner sc = new Scanner(new File(dir));
	HEADERS = sc.nextLine().split(",");
	COLUMN_CNT = HEADERS.length;
	sc.close();
    }

    public static DataFrame readCSV(String dir) throws IOException, ParseException {
	defineHeaders(dir);
	FileReader in = new FileReader(new File(dir));
	@SuppressWarnings("deprecation")
	Iterator<CSVRecord> records = CSVFormat.EXCEL.withHeader(HEADERS).withFirstRecordAsHeader().parse(in)
		.iterator();
	records.next(); // skip headers

	ArrayList<ArrayList<String>> columns = new ArrayList<>();

	for (int i = 0; i < COLUMN_CNT; i++)
	    columns.add(new ArrayList<>());

	int record_count = 0;
	while (records.hasNext()) {
	    record_count++;
	    CSVRecord rec = records.next();

	    for (int i = 0; i < COLUMN_CNT; i++) {
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

    public static Object[] record2array(CSVRecord record) {
	return record.toList().toArray(new Object[0]);
    }
}
