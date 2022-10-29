package pl.bratosz.smartlockers.model.orders;

import com.fasterxml.jackson.annotation.JsonView;
import pl.bratosz.smartlockers.model.ClientArticle;
import pl.bratosz.smartlockers.model.clothes.Cloth;
import pl.bratosz.smartlockers.model.clothes.ClothSize;
import pl.bratosz.smartlockers.model.Views;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;

@Entity
public class ClothOrder implements
        OrderForRelease, OrderForExchangeAndRelease {

    @Id
    @JsonView(Views.Public.class)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JsonView(Views.Public.class)
    @OneToOne
    private Cloth clothToExchange;

    @JsonView(Views.Public.class)
    @OneToOne(cascade = CascadeType.ALL)
    private Cloth clothToRelease;

    @JsonView(Views.Public.class)
    private OrderType orderType;

    @JsonView(Views.Public.class)
    private String note;

    @JsonView(Views.Public.class)
    private boolean active;

    @ManyToOne
    private MainOrder mainOrder;

    @JsonView(Views.Public.class)
    private boolean reported;

    @JsonView(Views.Public.class)
    private ExchangeStrategy exchangeStrategy;

    @JsonView(Views.Public.class)
    @OneToMany(mappedBy = "clothOrder")
    private List<OrderStatus> orderStatusHistory;

    public ClothOrder() {
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public ClientArticle getClientArticle() {
        return clothToRelease.getClientArticle();
    }

    @Override
    public ClothSize getSize() {
        return clothToRelease.getSize();
    }

    public Cloth getClothToRelease() {
        return clothToRelease;
    }

    public void setClothToRelease(Cloth clothToRelease) {
        this.clothToRelease = clothToRelease;
    }

    public Cloth getClothToExchange() {
        return clothToExchange;
    }

    public void setClothToExchange(Cloth clothToExchange) {
        this.clothToExchange = clothToExchange;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public List<OrderStatus> getOrderStatusHistory() {
        if(orderStatusHistory == null) {
            return new LinkedList<>();
        } else {
            return orderStatusHistory;
        }
    }

    public void setOrderStatusHistory(List<OrderStatus> orderStatusHistory) {
        this.orderStatusHistory = orderStatusHistory;
    }

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }


    public MainOrder getMainOrder() {
        return mainOrder;
    }

    public void setMainOrder(MainOrder mainOrder) {
        this.mainOrder = mainOrder;
    }

    public ExchangeStrategy getExchangeStrategy() {
        return exchangeStrategy;
    }

    public void setExchangeStrategy(ExchangeStrategy exchangeStrategy) {
        this.exchangeStrategy = exchangeStrategy;
    }

    public boolean isReported() {
        return reported;
    }

    public void setReported(boolean reported) {
        this.reported = reported;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        if(orderStatusHistory == null) {
            orderStatusHistory = new LinkedList<>();
        }
        orderStatus.setClothOrder(this);
        orderStatusHistory.add(orderStatus);
    }

    public OrderStatus getOrderStatus() {
        int last = orderStatusHistory.size() - 1;
        return orderStatusHistory.get(last);
    }
}