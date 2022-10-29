package pl.bratosz.smartlockers.model.orders;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ActionType {
    ACCEPT("Zaakceptuj"),
    CANCEL("Anuluj"),
    EDIT("Edytuj");

    private final String name;

    ActionType(String name) {
        this.name = name;
    }

    @JsonValue
    public String getName() {
        return name;
    }
}
