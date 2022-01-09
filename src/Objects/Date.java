
package Objects;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import Utils.Tools;
import Utils.Types;

public class Date {
    String timestring;

    public Date(String timestring) {
	this.timestring = timestring;
    }

    public Date() {
    }

    public java.util.Date getDateObject() throws ParseException {
	String format = (timestring.contains(":")) ? Tools.TIME_FORMAT : Tools.DATE_FORMAT;
	return new SimpleDateFormat(format).parse(Types.STRING.cast(timestring));
    }

    @Override
    public String toString() {
	return timestring;
    }

}
