package pl.bratosz.smartlockers.calculator;

import pl.bratosz.smartlockers.model.ClientArticle;
import pl.bratosz.smartlockers.model.clothes.Cloth;
import pl.bratosz.smartlockers.utils.Utils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.Date;

public class CalcCloth {


    public static Double calculateRedemptionPrice(Cloth cloth) {
        ClientArticle clientArticle = cloth.getClientArticle();
        LocalDate releaseDate = cloth.getReleaseDate();
        int depreciationPeriod = clientArticle.getDepreciationPeriod();
        int monthsBetween = getMonthsBetween(releaseDate, LocalDateTime.now());
        float percentPerMonth = getPercentRedemptionPerMonth(
                clientArticle.getDepreciationPercentageCap(),
                depreciationPeriod);
        return calculatePrice(
                monthsBetween,
                depreciationPeriod,
                percentPerMonth,
                clientArticle.getRedemptionPrice());
    }

    private static double calculatePrice(
            int monthsBetween,
            int depreciationPeriod,
            float percentPerMonth,
            double redemptionPrice) {
        if(monthsBetween > depreciationPeriod) monthsBetween = depreciationPeriod;
        double priceForMonth = redemptionPrice * (percentPerMonth / 100f);
        redemptionPrice = redemptionPrice - (priceForMonth * monthsBetween);
        return Math.round(redemptionPrice * 100d) / 100d;
    }

    private static int getMonthsBetween(LocalDate releaseDate, LocalDateTime now) {
        long between = ChronoUnit.MONTHS.between(releaseDate, now);
        if(between < 0) {
            return 0;
        } else {
            return (int) between;
        }
    }

    private static float getPercentRedemptionPerMonth(
            int percentageCap,
            int period) {
        return percentageCap / (period * 1.0f);
    }

}
