package pl.bratosz.smartlockers.model.orders;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonView;
import pl.bratosz.smartlockers.model.Views;
import pl.bratosz.smartlockers.model.users.User;

import javax.persistence.*;
import java.util.Date;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class OrderStatus {
    @JsonView(Views.Public.class)
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private long id;

    @JsonView(Views.InternalForClothOrders.class)
    @ManyToOne
    private User user;

    @JsonView(Views.Public.class)
    private OrderStage orderStage;

    @JsonView(Views.Public.class)
    private ActionType actionPerformed;

    @JsonView(Views.Public.class)
    @Column(columnDefinition = "dateTime")
    private Date dateOfUpdate;

    @ManyToOne
    private MainOrder mainOrder;

    @ManyToOne
    private ClothOrder clothOrder;

    public OrderStatus(){
    }

    public OrderStatus(OrderStage initialStage,
                       User user,
                       Date date) {
        this.orderStage = initialStage;
        this.dateOfUpdate = date;
        this.user = user;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public OrderStage getOrderStage() {
        return orderStage;
    }

    public void setOrderStage(OrderStage orderStage) {
        this.orderStage = orderStage;
    }

    public Date getDateOfUpdate() {
        return dateOfUpdate;
    }

    public void setDateOfUpdate(Date dateOfUpdate) {
        this.dateOfUpdate = dateOfUpdate;
    }

    public MainOrder getMainOrder() {
        return mainOrder;
    }

    public void setMainOrder(MainOrder mainOrder) {
        this.mainOrder = mainOrder;
    }

    public ClothOrder getClothOrder() {
        return clothOrder;
    }

    public void setClothOrder(ClothOrder clothOrder) {
        this.clothOrder = clothOrder;
    }

    public ActionType getActionPerformed() {
        return actionPerformed;
    }

    public void setActionPerformed(ActionType actionPerformed) {
        this.actionPerformed = actionPerformed;
    }

    public enum OrderStage {
        REQUESTED_AND_PENDING_FOR_CONFIRMATION_BY_SUPERVISOR("oczekuje na potwierdzenie przez klienta", false, true),
        CONFIRMED_BY_CLIENT_AND_PENDING_FOR_ACCEPTANCE("oczekuje na akceptację", true, true),
        DECLINED_BY_CLIENT("odrzucone przez kilenta",false, false),

        PENDING_FOR_ASSIGNMENT("oczekuje na przypisanie", true, true),
        IN_REALIZATION("w realizacji",true, false),

        READY_FOR_REALIZATION("gotowe do wykonania", true, false),
        READY_BUT_PENDING_FOR_CLOTH_RETURN("oczekuje na zwrot odzieży", true, false),

        RELEASED_AND_PENDING_FOR_OLD_CLOTH_RETURN("wydane, stara odzież nie zwrócona", true, false),

        DEFFERED("odroczone", true, false),
        PREPARED("przygotowane", true, false),
        PACKED("spakowane", true, false),
        FINALIZED("sfinalizowane", false, false),
        CANCELLED("usunięte", false, false),
        EMPTY("brak zamówienia", false, false),
        READY_BUT_PENDING_FOR_ASSIGNMENT("odzież zwrócona, oczekuje na przypisanie", true, true);

        private final String name;
        private final boolean active;
        private final boolean modificationAllowed;

        OrderStage(String name, boolean active, boolean modificationAllowed) {
            this.name = name;
            this.active = active;
            this.modificationAllowed = modificationAllowed;
        }

        @JsonValue
        public String getName() {
            return name;
        }

        public boolean isActive() {return active;}

        public boolean isModificationAllowed() {
            return modificationAllowed;
        }
    }


}
