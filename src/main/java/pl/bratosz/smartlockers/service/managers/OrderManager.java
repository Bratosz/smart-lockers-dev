package pl.bratosz.smartlockers.service.managers;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.model.ClientArticle;
import pl.bratosz.smartlockers.model.clothes.Cloth;
import pl.bratosz.smartlockers.model.clothes.ClothSize;
import pl.bratosz.smartlockers.model.clothes.LengthModification;
import pl.bratosz.smartlockers.model.orders.*;
import pl.bratosz.smartlockers.model.orders.OrderStatus.OrderStage;
import pl.bratosz.smartlockers.model.orders.parameters.complete.CompleteOrderParameters;
import pl.bratosz.smartlockers.model.orders.parameters.complete.CompleteForExchangeAndRelease;
import pl.bratosz.smartlockers.model.orders.parameters.complete.CompleteForRelease;
import pl.bratosz.smartlockers.model.orders.parameters.newArticle.OrderParameters;
import pl.bratosz.smartlockers.model.users.User;
import pl.bratosz.smartlockers.repository.ClientArticlesRepository;
import pl.bratosz.smartlockers.repository.ClothOrdersRepository;
import pl.bratosz.smartlockers.repository.MainOrdersRepository;
import pl.bratosz.smartlockers.response.UpdateResponse;
import pl.bratosz.smartlockers.service.ClothService;
import pl.bratosz.smartlockers.service.OrderStatusService;
import pl.bratosz.smartlockers.service.managers.creators.OrderCreator;
import sun.applet.Main;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static pl.bratosz.smartlockers.model.orders.OrderStatus.OrderStage.*;
import static pl.bratosz.smartlockers.model.orders.OrderType.*;

@Service
public class OrderManager {
    private OrderStatusService orderStatusService;
    private ClothOrdersRepository clothOrdersRepository;
    private MainOrdersRepository mainOrdersRepository;
    private ClothService clothService;
    private ClientArticlesRepository clientArticlesRepository;

    public OrderManager(
            OrderStatusService orderStatusService,
            ClothOrdersRepository clothOrdersRepository,
            MainOrdersRepository mainOrdersRepository,
            @Lazy ClothService clothService, ClientArticlesRepository clientArticlesRepository) {
        this.orderStatusService = orderStatusService;
        this.clothOrdersRepository = clothOrdersRepository;
        this.mainOrdersRepository = mainOrdersRepository;
        this.clothService = clothService;
        this.clientArticlesRepository = clientArticlesRepository;
    }

    public ClothOrder createOne(CompleteForRelease parameters, User user) {
        return OrderCreator.create(parameters, user);
    }

    public MainOrder createEmpty() {
        return MainOrder.createEmpty();
    }


    public ClothOrder createOne(CompleteForExchangeAndRelease parameters, User user) {
        return OrderCreator.createWithExchange(parameters, user);
    }

    public ClothOrder createForExistingCloth(
            OrderType orderType,
            Cloth cloth,
            User user) {
        OrderStatus orderStatus = orderStatusService.create(orderType, user);
        CompleteForRelease parameters = CompleteOrderParameters.createOrderForExistingCloth(
                cloth,
                cloth.getEmployee(),
                orderType,
                orderStatus,
                user);
        return create(parameters);
    }

    private ClothOrder create(
            CompleteForRelease parameters
    ) {
        ClothOrder order = new ClothOrder();
        order.setOrderType(parameters.getOrderType());
        order.setClothToRelease(parameters.getClothToRelease());
        order.setOrderStatus(parameters.getOrderStatus());
        return clothOrdersRepository.save(order);
    }

    public ClothOrder createForAdditionalCloth(
            Cloth clothForRelease,
            OrderStatus clothOrderStatus,
            MainOrder mainOrder) {
        ClothOrder order = new ClothOrder();
        order.setActive(true);
        order.setOrderStatus(clothOrderStatus);
        order.setMainOrder(mainOrder);
        order.setExchangeStrategy(mainOrder.getExchangeStrategy());
        order.setClothToExchange(clothForRelease);
        order.setClothToRelease(clothForRelease);
        order.setOrderType(mainOrder.getOrderType());
        return order;
    }

    public ClothOrder updateClothOrder(OrderStage orderStage, ClothOrder order, User user) {
        OrderStatus status = orderStatusService.create(orderStage, user);
        order.setOrderStatus(status);
        switch (orderStage) {
            case CANCELLED:
            case DECLINED_BY_CLIENT:
                return cancelClothOrder(order);
            case READY_FOR_REALIZATION:
            case READY_BUT_PENDING_FOR_ASSIGNMENT:
                order.setReported(false);
                return order;
            default:
                order.setActive(true);
                return order;
        }
    }


    public MainOrder updateMainOrder(OrderStage orderStage, MainOrder order, User user) {
        OrderStatus status = orderStatusService.create(orderStage, user);
        order.setOrderStatus(status);
        switch (orderStage) {
            case CANCELLED:
            case DECLINED_BY_CLIENT:
                updateClothOrders(orderStage, order.getClothOrders(), user);
                MainOrder mainOrder = cancelMainOrder(order);
                return mainOrder;
            default:
                order.setActive(true);
                return order;
        }
    }

    private void updateClothOrders(OrderStage orderStage, Set<ClothOrder> clothOrders, User user) {
        for (ClothOrder o : clothOrders) {
            updateClothOrder(orderStage, o, user);
        }
    }

    private MainOrder cancelMainOrder(MainOrder mainOrder) {
        mainOrder.setActive(false);
        return mainOrdersRepository.save(mainOrder);
    }

    private ClothOrder cancelClothOrder(ClothOrder clothOrder) {
        OrderType orderType = clothOrder.getOrderType();
        if (!orderType.equals(NEW_ARTICLE)) {
            Cloth clothToExchange = clothOrder.getClothToExchange();
            clothService.returnToEmployee(clothToExchange);
        }
        Cloth clothToRelease = clothOrder.getClothToRelease();
        save(removeClothesFrom(clothOrder));
        clothService.hardDelete(clothToRelease);
        clothOrder.setActive(false);
        return save(clothOrder);
    }

    private ClothOrder save(ClothOrder order) {
        return clothOrdersRepository.save(order);
    }

    private ClothOrder removeClothesFrom(ClothOrder order) {
        order.setClothToExchange(null);
        order.setClothToRelease(null);
        return order;
    }


    public UpdateResponse perform(ActionType actionType, List<OrderEditInfo> orderEditInfos, User user) {
        Set<MainOrder> orders;
        switch (actionType) {
            case CANCEL:
                orders = orderEditInfos.stream()
                        .map(oei -> oei.getMainOrder())
                        .collect(Collectors.toSet());
                Set<MainOrder> updatedOrders = update(CANCELLED, orders, user);
                return new UpdateResponse(
                        "Usunięto zamówień: " + updatedOrders.size() + ".",
                        Collections.singletonList(updatedOrders));
            case EDIT:
                orders = editOrders(orderEditInfos, actionType, user);
                return new UpdateResponse("Edytowano zamówień: " + orders.size() + ".",
                        Collections.singletonList(orders));
            default:
                return new UpdateResponse("Coś poszło nie tak.");
        }
    }

    private Set<MainOrder> editOrders(List<OrderEditInfo> orderEditInfos, ActionType actionType, User user) {
        Set<MainOrder> mainOrders = new HashSet<>();
        orderEditInfos.stream()
                .forEach(oei -> mainOrders.add(editOrder(oei, actionType, user)));
        return mainOrders;
    }

    public void edit(MainOrder order, OrderParameters params, User user) {
        ActionType actionType = ActionType.EDIT;
        changeArticle(order, params.getClientArticleId());
        editSize(order, params.getSize());
        editLengthModification(order, params.getLengthModification());
        changeQuantity(order, params.getQuantity(), user);
        update(order, actionType, user);
        mainOrdersRepository.save(order);
    }

    private MainOrder editOrder(OrderEditInfo oei, ActionType actionType, User user) {
        MainOrder mainOrder = oei.getMainOrder();
        editSize(mainOrder, oei.getClothSize());
        editLengthModification(mainOrder, oei.getLengthModification());
        editClientArticle(mainOrder, oei.getClientArticleId());
        update(mainOrder, actionType, user);
        return mainOrdersRepository.save(mainOrder);
    }

    private void update(MainOrder mainOrder, ActionType actionType, User user) {
        OrderStatus.OrderStage orderStage = mainOrder.getOrderStatus().getOrderStage();
        OrderStatus orderStatus = orderStatusService
                .create(orderStage, actionType, user);
        mainOrder.setOrderStatus(orderStatus);
        mainOrder.getClothOrders().stream()
                .forEach(clothOrder -> {
                    OrderStatus orderStatusForClothOrder = orderStatusService.createCopy(orderStatus);
                    clothOrder.setOrderStatus(orderStatusForClothOrder);
                });
    }

    private void editLengthModification(MainOrder mainOrder, LengthModification lengthModification) {
        LengthModification previousLengthModification = mainOrder.getLengthModification();
        if (!(previousLengthModification.equals(lengthModification)
                || lengthModification.equals(LengthModification.NONE))) {
            mainOrder.setLengthModification(lengthModification);
            mainOrder.getClothOrders().stream()
                    .forEach(clothOrder -> clothOrder
                            .getClothToRelease()
                            .setLengthModification(lengthModification));
        }
    }

    private void changeQuantity(MainOrder order, int desiredQuantity, User user) {
        int actualQuantity = (int) order.getClothOrders().stream()
                .filter(c -> c.isActive()).count();
        if (desiredQuantity < actualQuantity) {
            decreaseClothesQuantity(order, desiredQuantity);
        } else if (desiredQuantity > actualQuantity) {
            increaseClothesQuantity(order, desiredQuantity, user);
        }
    }

    private void increaseClothesQuantity(MainOrder order, int desiredQuantity, User user) {
        int actualQuantity = (int) order.getClothOrders().stream()
                .filter(c -> c.isActive()).count();
        for(int ordinalNumber = actualQuantity + 1; ordinalNumber <= desiredQuantity; ordinalNumber++) {
            addCloth(order, ordinalNumber, user);
        }
    }

    private void addCloth(MainOrder order, int ordinalNumber, User user) {
        ClothOrder clothOrder = createCopy(order, ordinalNumber, user);
        order.addClothOrder(clothOrder);
    }

    private ClothOrder createCopy(MainOrder order, int ordinalNumber, User user) {
        ClothOrder clothOrder = order.getClothOrders().stream()
                .filter(c -> c.isActive()).findFirst().get();
        ClothOrder copy = new ClothOrder();
        copy.setActive(true);
        copy.setOrderType(clothOrder.getOrderType());
        copy.setExchangeStrategy(clothOrder.getExchangeStrategy());
        copy.setReported(false);

        Cloth cloth = clothService.createAdditional( order, ordinalNumber, user);
        OrderStatus orderStatus = orderStatusService.createCopy(clothOrder.getOrderStatus());

        copy.setClothToRelease(cloth);
        copy.setClothToExchange(cloth);
        copy.setOrderStatus(orderStatus);
        return clothOrdersRepository.save(copy);
    }

    private void decreaseClothesQuantity(MainOrder order, int desiredQuantity) {
        order.getClothOrders().forEach(o -> {
            if (o.getClothToRelease().getOrdinalNumber() > desiredQuantity) {
                cancelClothOrder(o);
            }
        });
    }

    private void changeArticle(MainOrder order, long clientArticleId) {
        long actualArticleId = order.getDesiredClientArticle().getId();
        if (actualArticleId != clientArticleId) {
            ClientArticle article = clientArticlesRepository.getBy(clientArticleId);
            order.setDesiredClientArticle(article);
            order.getClothOrders().stream()
                    .forEach(clothOrder -> clothOrder.getClothToRelease().setClientArticle(article));
        }
    }

    private void editSize(MainOrder mainOrder, ClothSize clothSize) {
        ClothSize previousDesiredSize = mainOrder.getDesiredSize();
        if (!(previousDesiredSize.equals(clothSize) || clothSize.equals(ClothSize.SIZE_SAME))) {
            mainOrder.setDesiredSize(clothSize);
            mainOrder.getClothOrders().stream()
                    .filter(clothOrder -> clothOrder.isActive())
                    .forEach(clothOrder -> clothOrder.getClothToRelease().setSize(clothSize));
        }
    }

    private void editClientArticle(MainOrder mainOrder, long desiredClientArticleId) {
        long previousDesiredArticleId = mainOrder.getDesiredClientArticle().getId();
        if(desiredClientArticleId > 0 && desiredClientArticleId != previousDesiredArticleId) {
            ClientArticle desiredClientArticle = clientArticlesRepository.getBy(desiredClientArticleId);
            if(!mainOrder.isReported()) {
                mainOrder.setDesiredClientArticle(desiredClientArticle);
                mainOrder.getClothOrders().stream()
                        .filter(clothOrder -> clothOrder.isActive())
                        .forEach(clothOrder -> clothOrder.getClothToRelease().setClientArticle(desiredClientArticle));
            }
        }
    }


    public Set<MainOrder> update(
            OrderStage orderStage,
            Set<MainOrder> orders,
            User user) {
        Set<MainOrder> updatedOrders = new HashSet<>();
        orders.forEach(o -> {
            if (!o.getOrderStatus().getOrderStage().equals(orderStage)) {
                MainOrder updated = updateMainOrder(orderStage, o, user);
                updatedOrders.add(updated);
            }
        });
        return updatedOrders;
    }

}
