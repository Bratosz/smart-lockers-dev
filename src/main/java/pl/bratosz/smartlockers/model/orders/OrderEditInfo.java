package pl.bratosz.smartlockers.model.orders;

import pl.bratosz.smartlockers.model.clothes.ClothSize;
import pl.bratosz.smartlockers.model.clothes.LengthModification;

public class OrderEditInfo {
    private long orderId;
    private ClothSize clothSize;
    private LengthModification lengthModification;
    private long clientArticleId;
    private MainOrder mainOrder;

    public OrderEditInfo() {
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public ClothSize getClothSize() {
        return clothSize;
    }

    public void setClothSize(ClothSize clothSize) {
        this.clothSize = clothSize;
    }

    public LengthModification getLengthModification() {
        return lengthModification;
    }

    public void setLengthModification(LengthModification lengthModification) {
        this.lengthModification = lengthModification;
    }

    public MainOrder getMainOrder() {
        return mainOrder;
    }

    public void setMainOrder(MainOrder mainOrder) {
        this.mainOrder = mainOrder;
    }

    public long getClientArticleId() {
        return clientArticleId;
    }

    public void setClientArticleId(long clientArticleId) {
        this.clientArticleId = clientArticleId;
    }
}
