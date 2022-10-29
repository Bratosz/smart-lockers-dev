package pl.bratosz.smartlockers.response;

import com.fasterxml.jackson.annotation.JsonView;
import pl.bratosz.smartlockers.model.Views;
import pl.bratosz.smartlockers.model.clothes.Cloth;
import pl.bratosz.smartlockers.model.orders.OrderType;

import java.util.List;

public class ResponseOrdersCreated {
    @JsonView(Views.Public.class)
    private boolean created;
    @JsonView(Views.Public.class)
    private OrderType orderType;
    @JsonView(Views.Public.class)
    private String message;
    @JsonView(Views.Public.class)
    private int amount;
    @JsonView(Views.Public.class)
    private List<Cloth> clothesWithActiveOrders;


    public ResponseOrdersCreated(OrderType orderType, String message, int amount) {
        this.orderType = orderType;
        this.message = message;
        this.amount = amount;
    }

    public ResponseOrdersCreated(OrderType orderType, String message) {
        this.orderType = orderType;
        this.message = message;
    }

    public ResponseOrdersCreated(OrderType orderType, String message, List<Cloth> clothesWithActiveOrders) {
        this.orderType = orderType;
        this.message = message;
        this.clothesWithActiveOrders = clothesWithActiveOrders;
    }

    public static ResponseOrdersCreated createForOrdersNotCreatedBecauseActiveOrdersPresent(
            OrderType orderType,
            List<Cloth> clothesWithActiveOrders) {
        String message = "Nie udało się utworzyć zamówień. Dla wskazanych sztuk odzieży są inne aktywne zamówienia.";
        ResponseOrdersCreated response = new ResponseOrdersCreated(
                orderType,
                message,
                clothesWithActiveOrders);
        return response;
    }

    public static ResponseOrdersCreated createOrdersNotCreated(
            OrderType orderType,
            String cause) {
        String message = "Nie udało się utworzyć zamówienia z powodu: " + cause;
        ResponseOrdersCreated response = new ResponseOrdersCreated(orderType, message);
        response.setCreated(false);
        return response;
    }

    public static ResponseOrdersCreated createForOrdersCreated(
            OrderType orderType,
            int amount) {
        String message = "Utworzono zamówienie typu: " + orderType.getName();
        ResponseOrdersCreated response = new ResponseOrdersCreated(orderType, message, amount);
        response.setCreated(true);
        return response;
    }


    public boolean isCreated() {
        return created;
    }

    public void setCreated(boolean created) {
        this.created = created;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Cloth> getClothesWithActiveOrders() {
        return clothesWithActiveOrders;
    }

    public int getAmount() {
        return amount;
    }
}
