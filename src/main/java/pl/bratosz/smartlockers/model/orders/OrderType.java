package pl.bratosz.smartlockers.model.orders;

import com.fasterxml.jackson.annotation.JsonValue;
import pl.bratosz.smartlockers.converters.ConvertableEnum;

public enum OrderType implements ConvertableEnum {
    AUTO_EXCHANGE("Wymiana automatyczna"),
    EXCHANGE_FOR_NEW_ONE("Wymiana na nowe"),
    CHANGE_SIZE("Zmiana rozmiaru"),
    CHANGE_ARTICLE("Zmiana artykułu"),
    NEW_ARTICLE("Nowy artykuł"),
    RELEASE("Do wydania"),
    EMPTY("Brak aktywnego zamówienia"),
    ALL("Wszystkie"),
    REPAIR("Naprawa"),
    LENGTH_MODIFICATION("Modyfikacja długości");

    private final String name;

    OrderType(String name) {
        this.name = name;
    }

    @JsonValue
    @Override
    public String getName() {
        return name;
    }

}