package pl.bratosz.smartlockers.date;

import pl.bratosz.smartlockers.exception.WrongDateFormatException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class LocalDateConverter {

    public static String getDate(Date date){
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        return dateFormat.format(date);
    }

    public static String getDate(LocalDate date) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return dateTimeFormatter.format(date);
    }

    /**
     *
     * @param date pattern is yyyy-MM-dd
     *
     */
    public static Date getDate(String date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            if(date.equals(null) || date.length() < 10) {
                return new Date(0);
            } else {
                return dateFormat.parse(date);
            }
        } catch (ParseException e) {
            e.getMessage();
            e.printStackTrace();
            throw new WrongDateFormatException("Proper date format is " +
                    "yyyy-MM-dd. Passed date was: " + date);
        }
    }

    /**
     *
     * @param date pattern is yyyy-MM-dd
     *
     */
    public static LocalDate getDateFrom(String date) {
            if(date.equals(null) || date.length() < 10) {
                return convert(new Date(0));
            } else {
                return LocalDate.parse(date);
            }

    }

    public static LocalDate getDefaultDate() {
        return LocalDate.of(1990,1,1);
    }

    public static LocalDate convert(Date date) {
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    public static LocalDate getActualDate() {
        return LocalDate.now();
    }
}
