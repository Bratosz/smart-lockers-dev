package pl.bratosz.smartlockers.model.orders;

import com.fasterxml.jackson.annotation.JsonView;
import pl.bratosz.smartlockers.model.ClientArticle;
import pl.bratosz.smartlockers.model.Employee;
import pl.bratosz.smartlockers.model.MyEntity;
import pl.bratosz.smartlockers.model.Views;
import pl.bratosz.smartlockers.model.clothes.ClothSize;
import pl.bratosz.smartlockers.model.clothes.LengthModification;


import javax.persistence.*;
import java.util.*;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class MainOrder implements Comparable<MainOrder>, MyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonView(Views.Public.class)
    private long id;

    @JsonView(Views.Public.class)
    private OrderType orderType;

    @JsonView(Views.OrderBasicInfo.class)
    @ManyToOne
    private Employee employee;

    @JsonView({Views.EmployeeCompleteInfo.class, Views.OrderBasicInfo.class})
    @OneToMany(mappedBy = "mainOrder")
    private Set<ClothOrder> clothOrders;

    @JsonView({Views.EmployeeCompleteInfo.class, Views.OrderBasicInfo.class})
    @OneToMany(mappedBy = "mainOrder")
    private List<OrderStatus> orderStatusHistory;

    @JsonView({Views.EmployeeCompleteInfo.class, Views.OrderBasicInfo.class})
    @ManyToOne
    @JoinColumn(name = "previous_client_article_id")
    private ClientArticle previousClientArticle;

    @JsonView({Views.EmployeeCompleteInfo.class, Views.OrderBasicInfo.class})
    @ManyToOne
    @JoinColumn(name = "desired_client_article_id")
    private ClientArticle desiredClientArticle;

    @JsonView({Views.EmployeeCompleteInfo.class, Views.OrderBasicInfo.class})
    private ClothSize previousSize;

    @JsonView({Views.EmployeeCompleteInfo.class, Views.OrderBasicInfo.class})
    private ClothSize desiredSize;

    @JsonView({Views.EmployeeCompleteInfo.class, Views.OrderBasicInfo.class})
    private Date finalizationDate;

    @JsonView({Views.EmployeeCompleteInfo.class, Views.OrderBasicInfo.class})
    private boolean finalized;

    @JsonView({Views.EmployeeCompleteInfo.class, Views.OrderBasicInfo.class})
    private Date created;

    @JsonView({Views.EmployeeCompleteInfo.class, Views.OrderBasicInfo.class})
    private boolean reported;

    @JsonView({Views.Public.class, Views.OrderBasicInfo.class})
    private boolean active;

    @JsonView(Views.Public.class)
    private ExchangeStrategy exchangeStrategy;

    @JsonView(Views.Public.class)
    private LengthModification lengthModification;

    @JsonView(Views.Public.class)
    private String description;

    private boolean orderEmpty;

    public MainOrder() {
    }

    public static MainOrder createDefault() {
        MainOrder mainOrder = new MainOrder();
        mainOrder.setActive(true);
        mainOrder.setCreated(new Date());
        return mainOrder;
    }

    public static MainOrder createEmpty() {
        MainOrder m = new MainOrder();
        m.setOrderEmpty(true);
        return m;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        employee.getMainOrders().add(this);
        this.employee = employee;
    }

    public Set<ClothOrder> getClothOrders() {
        return clothOrders;
    }

    public void setClothOrders(Set<ClothOrder> clothOrders) {
        this.clothOrders = clothOrders;
    }

    public List<OrderStatus> getOrderStatusHistory() {
        return orderStatusHistory;
    }

    public void setOrderStatusHistory(List<OrderStatus> orderStatusHistory) {
        this.orderStatusHistory = orderStatusHistory;
    }

    public ClientArticle getPreviousClientArticle() {
        return previousClientArticle;
    }

    public void setPreviousClientArticle(ClientArticle previousClientArticle) {
        this.previousClientArticle = previousClientArticle;
    }

    public ClientArticle getDesiredClientArticle() {
        return desiredClientArticle;
    }

    public void setDesiredClientArticle(ClientArticle actualClientArticle) {
        this.desiredClientArticle = actualClientArticle;
    }

    public ClothSize getPreviousSize() {
        return previousSize;
    }

    public void setPreviousSize(ClothSize previousSize) {
        this.previousSize = previousSize;
    }

    public ClothSize getDesiredSize() {
        return desiredSize;
    }

    public void setDesiredSize(ClothSize actualSize) {
        this.desiredSize = actualSize;
    }

    public Date getFinalizationDate() {
        return finalizationDate;
    }

    public void setFinalizationDate(Date finalizationDate) {
        this.finalizationDate = finalizationDate;
    }

    public boolean isFinalized() {
        return finalized;
    }

    public void setFinalized(boolean finalized) {
        this.finalized = finalized;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void addClothOrder(ClothOrder clothOrder) {
        if(clothOrders == null) {
            clothOrders = new HashSet<>();
        }
        clothOrder.setMainOrder(this);
        clothOrders.add(clothOrder);
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        if(orderStatusHistory == null) {
            orderStatusHistory = new LinkedList<>();
        }
        orderStatus.setMainOrder(this);
        orderStatusHistory.add(orderStatus);
    }

    public OrderStatus getOrderStatus() {
        int last = orderStatusHistory.size() - 1;
        return orderStatusHistory.get(last);
    }

    public LengthModification getLengthModification() {
        if(lengthModification == null) {
            return LengthModification.NONE;
        } else {
            return lengthModification;
        }
    }

    public void setLengthModification(LengthModification lengthModification) {
        this.lengthModification = lengthModification;
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

    public boolean isOrderEmpty() {
        return orderEmpty;
    }

    public void setOrderEmpty(boolean orderEmpty) {
        this.orderEmpty = orderEmpty;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int compareTo(MainOrder o) {
        return Comparator.comparing(MainOrder::getDesiredClientArticle)
                .thenComparing(MainOrder::getDesiredSize)
                .thenComparing(MainOrder::getId)
                .compare(this, o);
    }
}
