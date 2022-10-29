package pl.bratosz.smartlockers.model.clothes;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import pl.bratosz.smartlockers.serializers.ClothActualStatusSerializer;

import static pl.bratosz.smartlockers.model.clothes.LifeCycleStatus.*;

@JsonSerialize(using = ClothActualStatusSerializer.class)
public enum ClothActualStatus {
    ORDERED("Zamówione", BEFORE_RELEASE),
    ASSIGNED("Przypisane", BEFORE_RELEASE),
    PENDING_FOR_SUPPLY("Oczekuje na dostawę",BEFORE_RELEASE),
    IN_PREPARATION("W przygotowaniu", BEFORE_RELEASE),
    RELEASED("Wydane", IN_ROTATION),
    ACCEPTED_FOR_EXCHANGE("Przyjęte do wymiany", ACCEPTED),
    EXCHANGED("Wymienione", WITHDRAWN),
    ACCEPTED_AND_WITHDRAWN("Wycofane", WITHDRAWN),
    UNKNOWN("Nieznany", LifeCycleStatus.UNKNOWN);

    private final String name;
    private final LifeCycleStatus lifeCycleStatus;

    ClothActualStatus(String name, LifeCycleStatus lifeCycleStatus) {
        this.name = name;
        this.lifeCycleStatus = lifeCycleStatus;
    }

    public String getName() {
        return name;
    }

    public LifeCycleStatus getLifeCycleStatus() {
        return lifeCycleStatus;
    }
}
