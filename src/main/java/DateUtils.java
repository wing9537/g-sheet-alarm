import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

    public static final String DATE_FORMAT = "yyyy-MM-dd";

    public static Date getToday() {
        return stringToDate(dateToString(new Date(), DATE_FORMAT), DATE_FORMAT);
    }

    public static Date stringToDate(String date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            if (date != null && !date.isEmpty()) return sdf.parse(date);
        } catch (Exception e) {
            System.out.println("cannot convert " + date + " to date with format " + format);
        }
        return null;
    }

    public static String dateToString(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    public static long daysBetween(Date date1, Date date2) {
        long diff = date2.getTime() - date1.getTime();
        return diff / (24 * 60 * 60 * 1000);
    }

}
