package pl.bratosz.smartlockers.response;

import pl.bratosz.smartlockers.model.Locker;

import java.util.List;

public class DataLoadedResponse {
    String message;
    int numberOfLockers;

    private DataLoadedResponse(String message, int numberOfLockers) {
        this.message = message;
        this.numberOfLockers = numberOfLockers;
    }

    private DataLoadedResponse(String message){
        this.message = message;
    }

    public static DataLoadedResponse createLockersBoxesAndEmployeesLoadedSuccesfully(
            List<Locker> lockers) {
        DataLoadedResponse response = new DataLoadedResponse("Data loaded succesfully. Number of lockers is: " +
                lockers.size(), lockers.size());
        return response;
    }

    public static DataLoadedResponse createFailLoadDataFromFile(String message) {
        DataLoadedResponse response = new DataLoadedResponse("Cant extract data from file.\n" +
                message);
        return response;
    }

    public int getNumberOfLockers() {
        return numberOfLockers;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setNumberOfLockers(int numberOfLockers) {
        this.numberOfLockers = numberOfLockers;
    }
}
