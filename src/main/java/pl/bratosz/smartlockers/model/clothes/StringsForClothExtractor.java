package pl.bratosz.smartlockers.model.clothes;

import pl.bratosz.smartlockers.utils.string.MyString;

import static pl.bratosz.smartlockers.model.clothes.ClothSize.*;

public class StringsForClothExtractor {

    public static LengthModification getModificationFromString(
            String stringSize) {
        String modification;
        if (stringSize.contains("N")
                && !stringSize.contains("NS")
                && !stringSize.contains("NTP")) {
            modification = MyString
                    .create(stringSize.substring(stringSize.indexOf("N"))).get();
            modification = validate(modification); }
        else if(stringSize.contains("SKR-DŁ")) {
            modification = MyString.create("SKR_DŁ").get();
        } else if (stringSize.contains("R")) {
            modification = MyString
                    .create(stringSize.substring(stringSize.indexOf("R"))).get();
            modification = validate(modification);
        } else {
            modification = MyString.create("NONE").get();
        }
        return LengthModification.valueOf(modification);
    }

    public static ClothSize getSizeFromString(String stringSize) {
        if (stringSize.contains("N")
                && !stringSize.contains("NS")
                && !stringSize.contains("NTP")) {
            stringSize = MyString.create(stringSize.substring(0, stringSize.indexOf("N"))).get();
        } else if (stringSize.contains("R")) {
            stringSize = MyString.create(stringSize.substring(0, stringSize.indexOf("R"))).get();
        }
        return normalize(getSizeByName(stringSize));
    }

    private static String validate(String modification) {
        String numberOnly = modification.substring(1);
        Integer lengthModificationValue = Integer.valueOf(numberOnly);
        if (lengthModificationValue < 70 ||
                lengthModificationValue > 130) {
            return "NTP";
        } else {
            return modification;
        }
    }

    private static ClothSize normalize(ClothSize size) {
        switch (size) {
            case SIZE_XXL:
                return SIZE_2XL;
            case SIZE_XXXL:
                return SIZE_3XL;
            case SIZE_XXXXL:
                return SIZE_4XL;
            case SIZE_XXXXXL:
                return SIZE_5XL;
            case SIZE_XXXXXXL:
                return SIZE_6XL;
            case SIZE_NS:
                return SIZE_NTP;
            default:
                return size;
        }
    }
}
