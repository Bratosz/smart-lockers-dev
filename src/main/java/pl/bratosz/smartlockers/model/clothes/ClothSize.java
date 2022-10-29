package pl.bratosz.smartlockers.model.clothes;

import com.fasterxml.jackson.annotation.JsonValue;
import pl.bratosz.smartlockers.converters.ConvertableEnum;

import java.util.HashMap;
import java.util.Map;

public enum ClothSize implements Comparable<ClothSize>, ConvertableEnum {
    SIZE_SAME("SIZE_SAME"),
    SIZE_0("0"),
    SIZE_NS("NS"),
    SIZE_NTP("NTP"),
    SIZE_34("34"),
    SIZE_36("36"),
    SIZE_38("38"),
    SIZE_40("40"),
    SIZE_42("42"),
    SIZE_44("44"),
    SIZE_46("46"),
    SIZE_48("48"),
    SIZE_50("50"),
    SIZE_52("52"),
    SIZE_54("54"),
    SIZE_56("56"),
    SIZE_58("58"),
    SIZE_60("60"),
    SIZE_62("62"),
    SIZE_64("64"),
    SIZE_66("66"),
    SIZE_68("68"),
    SIZE_XS("XS"),
    SIZE_S("S"),
    SIZE_M("M"),
    SIZE_L("L"),
    SIZE_XL("XL"),
    SIZE_XXL("XXL"),
    SIZE_2XL("2XL"),
    SIZE_XXXL("XXXL"),
    SIZE_3XL("3XL"),
    SIZE_XXXXL("XXXXL"),
    SIZE_4XL("4XL"),
    SIZE_XXXXXL("XXXXXL"),
    SIZE_5XL("5XL"),
    SIZE_XXXXXXL("XXXXXXL"),
    SIZE_6XL("6XL"),
    SIZE_UNKNOWN("??"),
    SIZE_2XS("2XS"),
    SIZE_3XS("3XS"),
    SIZE_EMPTY("EMPTY"),
    SIZE_32("32");

    private final String name;
    private static final Map<String, ClothSize>
            sizesByNames = new HashMap<>();

    static {
        for(ClothSize size : ClothSize.values()) {
            sizesByNames.put(size.name, size);
        }
    }

    ClothSize(String name) {
        this.name = name;
    }

    public static ClothSize getSizeByName(String name) {
        if(sizesByNames.containsKey(name)) {
            return sizesByNames.get(name);
        } else {
            return SIZE_0;
        }
    }

    public static ClothSize getExactSizeByName(String name) {
        if(sizesByNames.containsKey(name)) {
            return sizesByNames.get(name);
        } else {
            return ClothSize.SIZE_UNKNOWN;
        }
    }

    @JsonValue
    @Override
    public String getName() {
        return name;
    }

}
