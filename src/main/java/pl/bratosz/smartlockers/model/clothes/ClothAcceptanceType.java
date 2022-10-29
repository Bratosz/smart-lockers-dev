package pl.bratosz.smartlockers.model.clothes;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ClothAcceptanceType {
    CHECK_STATUS("sprawdzenie statusu"),
    EXCHANGE("wymiana"),
    REPAIR("naprawa"),
    WASHING("pranie");

    ClothAcceptanceType(String name) {
        this.name = name;
    }

    private final String name;

    @JsonValue
    public String getName() {
        return name;
    }
}
