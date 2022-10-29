package pl.bratosz.smartlockers.service;

import com.fasterxml.jackson.annotation.JsonView;
import pl.bratosz.smartlockers.model.Views;
import java.util.LinkedList;
import java.util.List;

public class LockersLoadReport {
    @JsonView(Views.Public.class)
    private int plantNumber;
    @JsonView(Views.Public.class)
    private List<BoxInfo> duplicatedBoxes;
    @JsonView(Views.Public.class)
    private int loadedBoxesAmount;

    public LockersLoadReport() {
    }

    public LockersLoadReport(int plantNumber) {
        this.plantNumber = plantNumber;
        duplicatedBoxes = new LinkedList<>();
    }

    public int getLoadedBoxesAmount() {
        return loadedBoxesAmount;
    }

    public void setLoadedBoxesAmount(int loadedBoxesAmount) {
        this.loadedBoxesAmount = loadedBoxesAmount;
    }

    public int getPlantNumber() {
        return plantNumber;
    }

    public void setPlantNumber(int plantNumber) {
        this.plantNumber = plantNumber;
    }

    public List<BoxInfo> getDuplicatedBoxes() {
        return duplicatedBoxes;
    }

    public void setDuplicatedBoxes(List<BoxInfo> duplicatedBoxes) {
        this.duplicatedBoxes = duplicatedBoxes;
    }
}
