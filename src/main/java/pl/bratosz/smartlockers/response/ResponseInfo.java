package pl.bratosz.smartlockers.response;

import com.fasterxml.jackson.annotation.JsonView;
import pl.bratosz.smartlockers.model.Views;

public class ResponseInfo {
    @JsonView(Views.Public.class)
    private String message;
    @JsonView(Views.Public.class)
    private boolean succeed;

    public ResponseInfo() {
    }

    public ResponseInfo(String message, boolean succeed) {
        this.message = message;
        this.succeed = succeed;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSucceed() {
        return succeed;
    }

    public void setSucceed(boolean succeed) {
        this.succeed = succeed;
    }
}
