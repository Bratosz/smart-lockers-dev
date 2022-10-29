package pl.bratosz.smartlockers.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum MeasurementListType {
    TO_MEASURE("Do pomierzenia"),
    TO_RELEASE("Do wydania");

    private final String name;

    MeasurementListType(String name) {
        this.name = name;
    }

    @JsonValue
    public String getName() {
        return name;
    }
}
