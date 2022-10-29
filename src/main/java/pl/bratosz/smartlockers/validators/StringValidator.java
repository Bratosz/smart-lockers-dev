package pl.bratosz.smartlockers.validators;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringValidator {

    public static boolean isStringContainsNumber(String s) {
        Pattern p = Pattern.compile("[0-9]");
        Matcher m = p.matcher(s);

        return m.find();
    }

    public static boolean isStringContainLettersAndSpacesOnly(String s) {
        Pattern p = Pattern.compile("[\\p{IsAlphabetic}\\d]{2,}(\\s[\\p{IsAlphabetic}\\d]{2,})*");
        Matcher m = p.matcher(s);

        return m.matches();
    }

    public static boolean isStringContainLettersDashesAndSpacesOnly(String s) {
        Pattern p = Pattern.compile("[\\p{IsAlphabetic}\\d]{2,}(\\s-[\\p{IsAlphabetic}\\d]{2,})*");
        Matcher m = p.matcher(s);

        return m.matches();
    }
}
