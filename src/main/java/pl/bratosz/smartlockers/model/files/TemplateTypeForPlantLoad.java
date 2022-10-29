package pl.bratosz.smartlockers.model.files;

import org.springframework.http.server.ServerHttpAsyncRequestControl;
import pl.bratosz.smartlockers.service.exels.plant.template.data.SheetTypeForPlantLoad;


import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static pl.bratosz.smartlockers.service.exels.plant.template.data.SheetTypeForPlantLoad.EMPLOYEES_AND_SIZES;

public enum TemplateTypeForPlantLoad {
    TEMPLATE_WITH_LOCATIONS(
            "Szablon do wczytania klienta - lokalizacje",
            "src/main/resources/static/xlsx-templates/load-plant/with-employees-locations.xlsx"),
    TEMPLATE_WITH_LOCATIONS_AND_SIZES(
            "Szablon do wczytania klienta - rozmiary, lokalizacje",
            "src/main/resources/static/xlsx-templates/load-plant/with-employees-sizes-and-locations.xlsx",
            Arrays.asList(EMPLOYEES_AND_SIZES)),
    TEMPLATE_WITH_BOXES(
            "Szablon do wczytania klienta - boxy",
            "src/main/resources/static/xlsx-templates/load-plant/with-employees-boxes.xlsx"),
    TEMPLATE_WITH_BOXES_AND_SIZES(
            "Szablon do wczytania klienta - rozmiary, boxy",
            "src/main/resources/static/xlsx-templates/load-plant/with-employees-sizes-and-boxes.xlsx",
            Arrays.asList(EMPLOYEES_AND_SIZES));

    private String name;
    private String path;
    private Set<SheetTypeForPlantLoad> sheetTypes;

    TemplateTypeForPlantLoad(String name, String path, List<SheetTypeForPlantLoad> additionalSheetTypes) {
        this.name = name;
        this.path = path;
        this.sheetTypes = new HashSet<>(Arrays.asList(SheetTypeForPlantLoad.values())
                .stream()
                .filter(e -> !e.isAdditional())
                .collect(Collectors.toList()));
        additionalSheetTypes.forEach(e -> this.sheetTypes.add(e));
    }

    TemplateTypeForPlantLoad(String name, String path) {
        this.name = name;
        this.path = path;
        this.sheetTypes = new HashSet<>(Arrays.asList(SheetTypeForPlantLoad.values()));
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    public Set<SheetTypeForPlantLoad> getSheetTypes() {
        return sheetTypes;
    }
}
