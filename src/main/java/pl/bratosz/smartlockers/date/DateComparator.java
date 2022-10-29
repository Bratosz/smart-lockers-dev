package pl.bratosz.smartlockers.date;

import java.time.LocalDate;

public class DateComparator {

    public static boolean isDateOlderThanPeriodOfMonths(
            LocalDate dateToCompare,
            int periodInMonths) {
        LocalDate currentDate = LocalDate.now();
        LocalDate currentDateMinusPeriod = currentDate.minusMonths(periodInMonths);
        return dateToCompare.isBefore(currentDateMinusPeriod);
    }
}
