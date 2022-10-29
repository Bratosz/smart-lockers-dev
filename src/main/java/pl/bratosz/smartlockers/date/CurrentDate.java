package pl.bratosz.smartlockers.date;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CurrentDate {
    private String date;
    private DateTimeFormatter dateTimeFormatter;

    /**
     *
     * @param pattern should be like dd.MM.yyyy or dd-MM-yyyy e.t.c
     */
    public static CurrentDate createFor(String pattern) {
        return new CurrentDate(pattern);
    }

    private CurrentDate() {}

    private CurrentDate(String pattern) {
        dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        LocalDateTime now = LocalDateTime.now();
        date = dateTimeFormatter.format(now);
    }

    public String get() {
        return date;
    }
}
