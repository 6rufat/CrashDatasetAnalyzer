
package Objects;

import java.util.Date;

public class Tools {
    public static String DATE_FORMAT = "dd/MM/yy";
    public static String TIME_FORMAT = "hh:mm";

}

interface Types {
    public static Class<? extends Double> NUMBER = new Double(0).getClass();
    public static Class<? extends Date> DATE = new Date().getClass(); // format: mm-dd-yy
    public static Class<? extends String> STRING = new String("").getClass();

}
