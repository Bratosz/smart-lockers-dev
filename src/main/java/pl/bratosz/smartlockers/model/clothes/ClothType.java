package pl.bratosz.smartlockers.model.clothes;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ClothType {
    SHIRT("Koszulka"),
    TROUSERS("Spodnie"),
    SWEATSHIRT("Bluza"),
    APRON("Fartuch"),
    JACKET("Kurtka"),
    NOT_DEFINED("Nieokreślony"),
    EXCEPTIONAL("EXCEPTIONAL!");

    private final String name;

    ClothType(String name) {
        this.name = name;
    }

    @JsonValue
    public String getName() {
        return name;
    }
}
