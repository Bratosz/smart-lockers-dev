package pl.bratosz.smartlockers.service.exels.plant.template.data;

public enum SheetTypeForPlantLoad {
    POSITIONS_AND_ARTICLES("STANOWISKA"),
    POSITIONS_AND_DEPARTMENTS("STANOWISKA I ODDZIAŁY"),
    EMPLOYEES("PRACOWNICY"),
    EMPLOYEES_AND_SIZES("PRACOWNICY I ROZMIARY", true),
    LOCKERS("SZAFY"),
    LOCATIONS("LOKALIZACJE"),
    DEPARTMENTS("ODDZIAŁY");

    private final String name;
    private boolean additional;

    SheetTypeForPlantLoad(String name) {
        this.name = name;
    }

    SheetTypeForPlantLoad(String name, boolean additional) {
        this.name = name;
        this.additional = additional;
    }

    public String getName() {
        return name;
    }

    public boolean isAdditional() {
        return additional;
    }
}
