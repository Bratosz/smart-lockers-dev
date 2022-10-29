package pl.bratosz.smartlockers.response;

import com.fasterxml.jackson.annotation.JsonView;
import pl.bratosz.smartlockers.model.Views;

import java.util.Collection;
import java.util.List;

public class CreateResponse <T> {

    @JsonView(Views.Public.class)
    private T entity;

    private Collection<T> collection;

    @JsonView(Views.Public.class)
    private String message;

    @JsonView(Views.Public.class)
    private boolean succeed;

    public CreateResponse() {
    }

    public CreateResponse(Collection<T> collection, String message) {
        this.collection = collection;
        this.message = message;
        this.succeed = true;
    }

    public CreateResponse(T entity, String message) {
        this.entity = entity;
        this.message = message;
        this.succeed = true;
    }

    public CreateResponse(String message) {
        this.message = message;
        this.succeed = false;
        this.entity = null;
    }


    public T getEntity() {
        return entity;
    }

    public void setEntity(T entity) {
        this.entity = entity;
    }

    public Collection<T> getCollection() {
        return collection;
    }

    public void setCollection(Collection<T> collection) {
        this.collection = collection;
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
