package pl.bratosz.smartlockers.service;

import com.fasterxml.jackson.annotation.JsonView;
import pl.bratosz.smartlockers.model.Views;

public class BoxInfo {
    @JsonView(Views.Public.class)
    private int lockerNumber;
    @JsonView(Views.Public.class)
    private int boxNumber;

    public BoxInfo(int lockerNumber, int boxNumber) {
        this.lockerNumber = lockerNumber;
        this.boxNumber = boxNumber;
    }

    public int getLockerNumber() {
        return lockerNumber;
    }

    public void setLockerNumber(int lockerNumber) {
        this.lockerNumber = lockerNumber;
    }

    public int getBoxNumber() {
        return boxNumber;
    }

    public void setBoxNumber(int boxNumber) {
        this.boxNumber = boxNumber;
    }
}
