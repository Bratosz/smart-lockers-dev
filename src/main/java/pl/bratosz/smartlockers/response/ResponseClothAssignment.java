package pl.bratosz.smartlockers.response;

import com.fasterxml.jackson.annotation.JsonView;
import pl.bratosz.smartlockers.model.Views;

public class ResponseClothAssignment {
    @JsonView(Views.Public.class)
    private String message;
    @JsonView(Views.Public.class)
    private boolean assigned;


    public ResponseClothAssignment(String message, boolean assigned) {
        this.message = message;
        this.assigned = assigned;
    }

    public static ResponseClothAssignment createForSucceed() {
        return new ResponseClothAssignment("Odzież przypisano do pracownika",true);
    }

    public static ResponseClothAssignment createForFailure(String message) {
        return new ResponseClothAssignment(message, false);
    }
}
