package pl.bratosz.smartlockers.calculator;

import java.util.Date;

public class CalculateClothesValue {
    public static int calculateValueForCloth(String articleNumber, Date releaseDate) {
        Date today = new Date();
        long diff = today.getTime() - releaseDate.getTime();
        Float months = (diff / (3600 * 24 * 1000 * 30.42f));

        Float clothValue;
        if (articleNumber.equals("1052") || articleNumber.equals("1051") || articleNumber.equals("1351")
                || articleNumber.equals("5595")) {
            clothValue = 68f;
        } else if (articleNumber.equals("5071") || articleNumber.equals("5072") || articleNumber.equals("5073")) {
            clothValue = 49f;
        } else if (articleNumber.equals("1344") || articleNumber.equals("1350")
                || articleNumber.equals("3526") || articleNumber.equals("3595")) {
            clothValue = 49f;
        } else {
            clothValue = 38f;
        }

        clothValue = (clothValue - (months * (clothValue / 42)));
        if (clothValue < 0) clothValue = 0f;

        return Math.round(clothValue);

    }
}
