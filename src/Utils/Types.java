
package Utils;

import Objects.Date;

public interface Types {
    public static Class<? extends Double> NUMBER = new Double(0).getClass();
    public static Class<? extends Date> DATE = new Objects.Date().getClass(); // format: mm-dd-yy
    public static Class<? extends String> STRING = new String("").getClass();

}
