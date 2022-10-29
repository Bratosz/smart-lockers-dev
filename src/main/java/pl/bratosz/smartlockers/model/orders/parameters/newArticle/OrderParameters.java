package pl.bratosz.smartlockers.model.orders.parameters.newArticle;

import pl.bratosz.smartlockers.model.clothes.ClothSize;
import pl.bratosz.smartlockers.model.clothes.LengthModification;
import pl.bratosz.smartlockers.model.orders.OrderType;

public class OrderParameters {
    private long articlesWithQuantityId;
    private long clientArticleId;
    private ClothSize size;
    private String lengthModification;
    private int quantity;
    private long employeeId;
    private String orderType;
    private long [] barcodes;
    private String exchangeDescription;

    public void setLengthModification(String lengthModification) {
        this.lengthModification = lengthModification;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getExchangeDescription() {
        return exchangeDescription;
    }

    public void setExchangeDescription(String exchangeDescription) {
        this.exchangeDescription = exchangeDescription;
    }

    public OrderParameters() {
    }

    public long getClientArticleId() {
        return clientArticleId;
    }

    public void setClientArticleId(long clientArticleId) {

        this.clientArticleId = clientArticleId;
    }

    public ClothSize getSize() {
        return size;
    }

    public void setSize(ClothSize size) {
        this.size = size;
    }

    public LengthModification getLengthModification() {
        return LengthModification.valueOf(lengthModification);
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public long getArticlesWithQuantityId() {
        return articlesWithQuantityId;
    }

    public void setArticlesWithQuantityId(long articlesWithQuantityId) {
        this.articlesWithQuantityId = articlesWithQuantityId;
    }

    public OrderType getOrderType() {
        return OrderType.valueOf(orderType);
    }

    public long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(long employeeId) {
        this.employeeId = employeeId;
    }

    public long[] getBarcodes() {
        if(barcodes == null) {
            return new long[0];
        }
        return barcodes;
    }

    public void setBarcodes(long[] barcodes) {
        this.barcodes = barcodes;
    }
}
