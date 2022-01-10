
package Objects;

public class Date {
    String timestring;

    public Date(String timestring) {
	this.timestring = timestring;
    }

    public Date() {
    }

    public String[] getTime() {

	return timestring.replaceAll("/", ":").split(":");
    }

    public String[] getDate() {
	return timestring.split("/");

    }

    public int getHour() {
	return Integer.parseInt(getTime()[0]);
    }

    public int getMinute() {
	return Integer.parseInt(getTime()[0]);
    }

    public int getDay() {
	return Integer.parseInt(getDate()[0]);
    }

    public int getMonth() {
	return Integer.parseInt(getDate()[1]);
    }

    public int getYear() {
	int y = Integer.parseInt(getDate()[2]);
	if (y < 33)
	    return y + 2000;
	else
	    return y + 1900;

    }

    public int compareTo(Date date) {

	// Check nulls, they always concede real values
	if ((timestring + date.timestring).contains("Null")) {
	    if (timestring.equals(date.timestring))
		return 0;

	    if (date.timestring.contains("Null")) {
		return 1;
	    }
	    if (timestring.contains("Null")) {
		return -1;
	    }
	}

	// if no nulls detected
	int res = 0;
	// check if it is time
	if (timestring.contains(":")) {
	    if (getHour() == date.getHour()) {
		res = getMinute() - date.getMinute();
	    } else
		res = getHour() - date.getHour();
	} else { // check if it is date
	    if (getYear() == date.getYear()) {
		if (getMonth() == date.getMonth()) {
		    res = getDay() - date.getDay();
		} else {
		    res = getMonth() - date.getMonth();
		}
	    } else {
		res = getYear() - date.getYear();
	    }
	}
	if (res == 0)
	    return 0;

	return res / Math.abs(res); // -1, 0, 1
    }

    @Override
    public String toString() {

	return timestring;
    }

}
