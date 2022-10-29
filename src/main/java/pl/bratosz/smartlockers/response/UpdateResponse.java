package pl.bratosz.smartlockers.response;

import com.fasterxml.jackson.annotation.JsonView;
import pl.bratosz.smartlockers.model.Views;

import java.util.LinkedList;
import java.util.List;

public class UpdateResponse <T> {
    @JsonView(Views.Public.class)
    private String message;
    @JsonView(Views.Public.class)
    private List<T> list;
    @JsonView(Views.Public.class)
    private boolean succeed;

    public UpdateResponse(String message, List<T> list) {
        this.message = message;
        this.list = list;
        this.succeed = true;
    }

    public UpdateResponse(String message) {
        this.message = message;
        this.list = new LinkedList<>();
        this.succeed = false;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public boolean isSucceed() {
        return succeed;
    }

    public void setSucceed(boolean succeed) {
        this.succeed = succeed;
    }
}
