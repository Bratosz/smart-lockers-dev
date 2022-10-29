package pl.bratosz.smartlockers.model.orders;

import pl.bratosz.smartlockers.model.ClientArticle;
import pl.bratosz.smartlockers.model.clothes.Article;
import pl.bratosz.smartlockers.model.clothes.ClothSize;

import java.util.List;

public interface BasicOrder {

    OrderType getOrderType();

    long getId();

    ClientArticle getClientArticle();

    ClothSize getSize();

    String getNote();

    void setNote(String note);

    List<OrderStatus> getOrderStatusHistory();

    OrderStatus getOrderStatus();

    void setOrderStatus(OrderStatus orderStatus);

    void setOrderType(OrderType orderType);

    boolean isActive();

    void setActive(boolean active);
}
