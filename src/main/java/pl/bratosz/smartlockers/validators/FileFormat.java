package pl.bratosz.smartlockers.validators;

public enum FileFormat {
    XLSX(".xlsx"),
    XLS(".xls");

    private final String name;

    FileFormat(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
