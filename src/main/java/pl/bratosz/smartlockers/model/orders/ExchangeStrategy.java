package pl.bratosz.smartlockers.model.orders;

public enum ExchangeStrategy {
    PIECE_FOR_PIECE("Sztuka za sztukę"),
    RELEASE_BEFORE_RETURN("Najpierw wydanie, potem zwrot"),
    NONE("Nie dotyczy");

    private String name;

    ExchangeStrategy(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
