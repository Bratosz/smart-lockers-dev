package pl.bratosz.smartlockers.response;

import com.fasterxml.jackson.annotation.JsonView;
import pl.bratosz.smartlockers.model.Employee;
import pl.bratosz.smartlockers.model.Position;
import pl.bratosz.smartlockers.model.Views;

import java.util.stream.Stream;

public class StandardResponse <T> {
    @JsonView(Views.Public.class)
    private String message;
    @JsonView(Views.Public.class)
    private T entity;
    @JsonView(Views.Public.class)
    private boolean succeed;

    public StandardResponse(String message) {
        this.entity = null;
        this.message = message;
        this.succeed = false;
    }

    public static <T> StandardResponse createForSucceed(String message, T entity) {
        return new StandardResponse(message, true, entity);
    }


    public static <T> StandardResponse createForSucceed() {
        return new StandardResponse("", true, "");
    }

    public static <T> StandardResponse createForFailure(String message, T entity) {
        return new StandardResponse(message, false, entity);
    }

    public static <T> StandardResponse createForFailure(String message) {
        return new StandardResponse(message, false, false);
    }

    public static <T> StandardResponse createForSucceed(String message) {
        return new StandardResponse(message, true, true);
    }

    public static <T> StandardResponse createForSucceed(T entity) {
        return new StandardResponse("", true, entity);
    }


    private StandardResponse(String message, boolean succeed, T entity) {
        this.message = message;
        this.succeed = succeed;
        this.entity = entity;
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

    public T getEntity() {
        return entity;
    }

    public void setEntity(T entity) {
        this.entity = entity;
    }
}
