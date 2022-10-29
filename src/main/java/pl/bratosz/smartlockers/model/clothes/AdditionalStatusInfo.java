package pl.bratosz.smartlockers.model.clothes;

import com.fasterxml.jackson.annotation.JsonValue;

public enum AdditionalStatusInfo {
    RETURNED_IN_BAG("Oddane w worku"),
    RETURNED_IN_LAUNDRY("Oddane w praniu"),
    RETURNED_TO_SERVICE_MAN("Oddane do serwisanta");

    private final String name;

    AdditionalStatusInfo(String name) {
        this.name = name;
    }

    @JsonValue
    public String getName() {
        return name;
    }
}
