
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Iterator;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import Objects.DataFrame;
import Objects.Row;

public class Main {
    public static String[] HEADERS = new String[] { "Date", "Time", "Location", "Operator", "Flight..", "Route", "Type",
	    "Registration", "cn.In", "Aboard", "Fatalities", "Ground", "Survivors", "SurvivalRate", "Summary",
	    "ClustID" };

    public static DataFrame DF;
    public static String data_dir = "./data/crash.csv";

    public static void main(String[] args) throws Exception {
	DF = readCSV(data_dir);
	Array arr = Array.newInstance(String, 5);
    }

    public static DataFrame readCSV(String dir) throws IOException {
	FileReader in = new FileReader(new File(dir));
	@SuppressWarnings("deprecation")
	Iterator<CSVRecord> records = CSVFormat.EXCEL.withHeader(HEADERS).withFirstRecordAsHeader().parse(in)
		.iterator();
	records.next();
	DataFrame DF = new DataFrame(HEADERS);

	while (records.hasNext()) {
	    CSVRecord rec = records.next();
	    Row r = new Row(HEADERS, HEADERS);
	    for (Object o : HEADERS) {
		r.set(o, rec.get(o.toString()));
	    }
	    DF.add(r);

	}

	return DF;
    }

    public static Object[] record2array(CSVRecord record) {
	return record.toList().toArray(new Object[0]);
    }
}
