package pl.bratosz.smartlockers.service;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.exception.ClothException;
import pl.bratosz.smartlockers.exception.ClothNotExistException;
import pl.bratosz.smartlockers.exception.ClothOrderException;
import pl.bratosz.smartlockers.exception.NoActiveClothOrderException;
import pl.bratosz.smartlockers.model.clothes.*;
import pl.bratosz.smartlockers.model.orders.*;
import pl.bratosz.smartlockers.model.users.User;
import pl.bratosz.smartlockers.repository.BoxesRepository;
import pl.bratosz.smartlockers.repository.ClothOrdersRepository;
import pl.bratosz.smartlockers.repository.EmployeesRepository;
import pl.bratosz.smartlockers.response.ResponseClothAcceptance;
import pl.bratosz.smartlockers.model.*;
import pl.bratosz.smartlockers.repository.ClothesRepository;

import pl.bratosz.smartlockers.response.ResponseClothAssignment;
import pl.bratosz.smartlockers.service.clothes.OrdinalNumberResolver;
import pl.bratosz.smartlockers.service.managers.cloth.GeneralClothUpdater;
import pl.bratosz.smartlockers.service.managers.cloth.ClothesCreator;
import pl.bratosz.smartlockers.service.managers.OrderManager;
import pl.bratosz.smartlockers.service.managers.cloth.ClothesManager;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static pl.bratosz.smartlockers.model.clothes.ClothActualStatus.ACCEPTED_FOR_EXCHANGE;
import static pl.bratosz.smartlockers.model.clothes.ClothActualStatus.RELEASED;
import static pl.bratosz.smartlockers.model.clothes.ClothSize.*;
import static pl.bratosz.smartlockers.model.clothes.LifeCycleStatus.*;
import static pl.bratosz.smartlockers.model.orders.OrderStatus.OrderStage.*;
import static pl.bratosz.smartlockers.model.orders.OrderType.*;

@Service
public class ClothService {
    private ClothesRepository clothesRepository;
    private ClothOrdersRepository clothOrdersRepository;
    private OrderService orderService;
    private OrderStatusService orderStatusService;
    private UserService userService;
    private OrderManager orderManager;
    private EmployeeService employeeService;
    private ClothStatusService clothStatusService;
    private ClientArticleService clientArticleService;
    private User user;
    private ClothesManager clothesManager;
    private ClothesCreator clothesCreator;
    private BoxesRepository boxesRepository;
    private RotationalClothService rotationalClothService;
    private EmployeesRepository employeesRepository;

    public ClothService(ClothesRepository clothesRepository,
                        ClothOrdersRepository clothOrdersRepository,
                        OrderStatusService orderStatusService,
                        UserService userService,
                        @Lazy OrderService orderService,
                        @Lazy OrderManager orderManager,
                        @Lazy EmployeeService employeeService,
                        ClothStatusService clothStatusService,
                        ClientArticleService clientArticleService,
                        ClothesManager clothesManager,
                        ClothesCreator clothesCreator,
                        BoxesRepository boxesRepository, RotationalClothService rotationalClothService, EmployeesRepository employeesRepository) {
        this.clothesRepository = clothesRepository;
        this.clothOrdersRepository = clothOrdersRepository;
        this.orderService = orderService;
        this.orderStatusService = orderStatusService;
        this.userService = userService;
        this.orderManager = orderManager;
        this.employeeService = employeeService;
        this.clothStatusService = clothStatusService;
        this.clientArticleService = clientArticleService;
        this.clothesManager = clothesManager;
        this.clothesCreator = clothesCreator;
        this.boxesRepository = boxesRepository;
        this.rotationalClothService = rotationalClothService;
        this.employeesRepository = employeesRepository;
    }

    private void loadUser(User user) {
        this.user = user;
    }

    private void loadUser(long userId) {
        this.user = userService.getUserById(userId);
    }

    public Cloth createNewForAssignInsteadExisting(
            int ordinalNumber,
            ClientArticle clientArticle,
            ClothSize size,
            LengthModification lengthModification,
            Employee employee,
            User user
    ) {
        loadUser(user);
        Cloth newCloth = clothesManager.createNewInstead(
                ordinalNumber, clientArticle, size, lengthModification, employee);
        return clothesRepository.save(newCloth);
    }

    public Set<Cloth> createAdditional(
            int clothQuantity,
            MainOrder o,
            User user) {
        loadUser(user);
        Set<Cloth> clothes = new LinkedHashSet<>();
        Cloth cloth;
        OrdinalNumberResolver onr = OrdinalNumberResolver.createForNewArticle(
                o.getDesiredSize(),
                o.getDesiredClientArticle(),
                o.getEmployee());
        for (int i = 0; i < clothQuantity; i++) {
            cloth = clothesCreator
                    .createNew(onr.getNextNumber(), o, user);
            clothes.add(cloth);
        }
        return clothes;
    }

    public Cloth createAdditional(
            MainOrder o,
            int ordinalNumber,
            User user) {
        loadUser(user);
        return clothesCreator.createNew(ordinalNumber, o, user);
    }


    public List<Cloth> getByBarcodes(long[] barCodes) {
        List<Cloth> clothes = new LinkedList<>();
        for (long barCode : barCodes) {
            Cloth cloth = clothesRepository.getByBarcode(barCode);
            clothes.add(cloth);
        }
        return clothes;
    }

    public Cloth getById(long id) {
        return clothesRepository.getById(id);
    }

    public ClothSize resolveDesiredSize(ClothSize desiredSize, ClothSize actualSize) {
        if (desiredSize == SIZE_SAME) {
            return actualSize;
        } else {
            return desiredSize;
        }
    }

    public ResponseClothAssignment assignWithdrawnCloth(
            long userId,
            long employeeId,
            int articleNumber,
            ClothSize size,
            Cloth withdrawnCloth) {
        loadUser(userId);
        long clientId = user.getActualClientId();
        Employee employee = employeeService.getById(employeeId);
        Cloth clothByBarcode = clothesRepository.getByBarcode(withdrawnCloth.getBarcode());
        if (clothIsPresent(clothByBarcode, clientId)) {
            return ResponseClothAssignment.createForFailure(
                    "Ubranie jest aktywne");
        } else if (clothBelongsToOtherClient(clothByBarcode, clientId)) {
            return ResponseClothAssignment.createForFailure(
                    "Ubranie należy do innego klienta");
        } else {
            withdrawnCloth.setSize(size);
            withdrawnCloth.setClientArticle(clientArticleService.get(articleNumber, clientId));
            return assignAsWithdrawnCloth(withdrawnCloth, employee);
        }
    }






    private ResponseClothAssignment assignAsWithdrawnCloth(Cloth withdrawnCloth, Employee employee) {
        ClothStatus clothStatus = clothStatusService.create(ClothDestination.FOR_DISPOSAL, user);
        withdrawnCloth.setStatus(clothStatus);
        withdrawnCloth.setLifeCycleStatus(WITHDRAWN);
        withdrawnCloth.setEmployee(employee);
        clothesRepository.save(withdrawnCloth);
        return ResponseClothAssignment.createForSucceed();
    }

    public ResponseClothAcceptance accept(
            ClothAcceptanceType acceptanceType,
            long barcode,
            long userId) {
        loadUser(userId);
        long clientId = user.getActualClientId();
        Cloth cloth = clothesRepository.getByBarcode(barcode);
        switch (acceptanceType) {
            case CHECK_STATUS:
                return checkStatus(cloth, barcode, clientId);
            default:
                return null;
        }
    }

    private ResponseClothAcceptance checkStatus(Cloth cloth, long barcode, long clientId) {
        if (clothIsAbsent(clientId, cloth)) {
            return createClothNotFoundResponse(cloth, barcode);
        } else if (cloth.getClientArticle().getClient().getId() != clientId) {
            return ResponseClothAcceptance.createClothBelongsToOtherClient(cloth);
        } else if (cloth.isActive()) {
            return ResponseClothAcceptance.createClothIsActive(cloth);
        } else {
            return ResponseClothAcceptance.createClothIsInactive(cloth);
        }
    }

    public ResponseClothAcceptance autoExchange(long barcode, long userId) {
        loadUser(userId);
        long clientId = user.getActualClientId();
        Cloth cloth = clothesRepository.getByBarcode(barcode);
        if(clothIsAbsent(clientId, cloth)) {
            return createClothNotFoundResponse(cloth, barcode);
        } else if (clothIsReturned(cloth)) {
            return ResponseClothAcceptance.createClothAlreadyReturned(cloth);
        } else {
            OrderType actualOrderType = getActualOrderType(cloth);
            return acceptForAutoExchange(cloth, actualOrderType);
        }
    }

    public ResponseClothAcceptance exchange(
            OrderType orderType,
            long barcode,
            ClothSize size,
            LengthModification lengthModification,
            int articleNumber,
            long userId) {
        loadUser(userId);
        long clientId = user.getActualClientId();
        Cloth cloth = clothesRepository.getByBarcode(barcode);
        ExchangeStrategy exchangeStrategy = getExchangeStrategyFor(cloth, orderType);
        if (clothIsAbsent(clientId, cloth)) {
            return createClothNotFoundResponse(cloth, barcode);
        } else if (clothIsReturned(cloth)) {
            return ResponseClothAcceptance.createClothAlreadyReturned(cloth);
        } else {
            OrderType actualOrderType = getActualOrderType(cloth);
            switch (orderType) {
                case AUTO_EXCHANGE:
                    return acceptForAutoExchange(cloth, actualOrderType);
                case EXCHANGE_FOR_NEW_ONE:
                    return acceptForExchangeForNewOne(orderType, cloth, actualOrderType, exchangeStrategy);
                case CHANGE_SIZE:
                    return acceptForChangeSize(orderType, cloth, actualOrderType, size, lengthModification, exchangeStrategy);
                case CHANGE_ARTICLE:
                    ClientArticle clientArticle = clientArticleService.get(articleNumber, clientId);
                    return acceptForChangeArticle(
                            orderType, cloth, actualOrderType, size, lengthModification, clientArticle);
                default:
                    return ResponseClothAcceptance.wrongOrderTypeResponse(orderType);
            }
        }
    }

    private ExchangeStrategy getExchangeStrategyFor(Cloth cloth, OrderType orderType) {
        return cloth.getClientArticle().getClient().getExchangeStrategies().get(orderType);
    }

    private ResponseClothAcceptance acceptForChangeArticle(
            OrderType orderType,
            Cloth cloth,
            OrderType actualOrderType,
            ClothSize size,
            LengthModification lengthModification,
            ClientArticle article) {
        ExchangeStrategy exchangeStrategy = ExchangeStrategy.PIECE_FOR_PIECE;
        if (actualOrderType.equals(OrderType.EMPTY)) {
            Cloth clothForExchange = acceptForExchange(cloth);
            OrderStatus orderStatus = orderStatusService.create(
                    READY_FOR_REALIZATION,
                    user);
            MainOrder mainOrder = orderService.createMainOrder(
                    orderType,
                    orderStatus,
                    article,
                    size,
                    lengthModification,
                    exchangeStrategy,
                    cloth);
            OrdinalNumberResolver onr = OrdinalNumberResolver.createForChangeArticle(
                    size, article, clothForExchange);
            ClothOrder clothOrder = orderService.placeOne(
                    clothForExchange,
                    orderType,
                    orderStatus,
                    exchangeStrategy,
                    article,
                    size,
                    lengthModification,
                    mainOrder,
                    onr.getNextNumber(),
                    user);
            return ResponseClothAcceptance.createNewOrderAddedAndClothAcceptedResponse(clothOrder);
        } else if (actualOrderType.equals(CHANGE_ARTICLE)) {
            ClothOrder order = acceptClothAndUpdateOrder(cloth);
            return ResponseClothAcceptance.createClothAcceptedResponse(order);
        } else {
            return getResponseForAnotherActiveOrder(cloth, 0);
        }
    }

    private ResponseClothAcceptance acceptForChangeSize(
            OrderType orderType,
            Cloth cloth,
            OrderType actualOrderType,
            ClothSize size,
            LengthModification lengthModification,
            ExchangeStrategy exchangeStrategy) {
        if (actualOrderType.equals(OrderType.EMPTY)) {
            Cloth clothForExchange = acceptForExchange(cloth);
            OrderStatus orderStatus = orderStatusService.create(
                    READY_FOR_REALIZATION,
                    user);
            MainOrder mainOrder = getMainOrder(
                    clothForExchange,
                    orderType,
                    orderStatus,
                    cloth.getClientArticle(),
                    size,
                    lengthModification,
                    exchangeStrategy);
            OrdinalNumberResolver onr = OrdinalNumberResolver.createForChangeSize(
                    size, clothForExchange, employeesRepository.getEmployeeById(clothForExchange.getEmployee().getId()));
            ClothOrder clothOrder = orderService.placeOne(
                    clothForExchange,
                    orderType,
                    orderStatus,
                    exchangeStrategy,
                    cloth.getClientArticle(),
                    size,
                    lengthModification,
                    mainOrder,
                    onr.getNextNumber(),
                    user);
            return ResponseClothAcceptance.createNewOrderAddedAndClothAcceptedResponse(clothOrder);
        } else if (actualOrderType.equals(CHANGE_SIZE)) {
            ClothOrder order = acceptClothAndUpdateOrder(cloth);
            return ResponseClothAcceptance.createClothAcceptedResponse(order);
        } else {
            return getResponseForAnotherActiveOrder(cloth, 0);
        }
    }

    private boolean clothIsPresent(Cloth cloth, long clientId) {
        return !clothIsAbsent(clientId, cloth);
    }

    private boolean clothIsReturned(Cloth cloth) {
        if (cloth.getLifeCycleStatus().equals(LifeCycleStatus.ACCEPTED)) {
            return true;
        } else {
            return false;
        }
    }

    public void loadClothes(
            List<Cloth> clothesToLoad,
            Employee employee,
            User user) {
        loadUser(user);
        if (employee.getClothes().isEmpty()) {
            employee.addClothes(clothesToLoad);
            employeeService.save(employee);
        }
    }

    public void updateLastWashingDate(Set<Cloth> updatedClothes) {
        for(Cloth c : updatedClothes) {
            clothesRepository.updateLastWashingById(c.getLastWashing(), c.getId());
        }
    }

    public void updateClothes(
            List<Cloth> actualClothes,
            Employee employee,
            User user) throws ClothException {
        loadUser(user);
        List<Cloth> newClothes = new LinkedList<>();
        List<Cloth> priorClothes = employee.getClothes().stream()
                .filter(c -> c.isActive()).collect(Collectors.toList());
        if (priorClothes.isEmpty()) {
            for (Cloth cloth : actualClothes) {
                cloth.setEmployee(employee);
                cloth.setRotational(employeeIsRotational(employee));
                newClothes.add(cloth);
                clothesRepository.saveAll(newClothes);
            }
        } else {
            GeneralClothUpdater gcu = new GeneralClothUpdater(
                    priorClothes,
                    actualClothes,
                    employee,
                    this);
            gcu.update();
        }
    }

    private boolean employeeIsRotational(Employee employee) {
        Position position = employee.getPosition();
        if(position != null && position.getName().equals("ROTACJA"))
            return true;
        else
            return false;
    }

    private void updateWithdrawnCloth(Cloth priorCloth) {
            priorCloth.setActive(false);
            priorCloth.setLifeCycleStatus(WITHDRAWN);
            if (priorCloth.getExchangeOrder() != null) {
                orderService.updateForWithdrawCloth(priorCloth, user);
            }
    }

    private Cloth getOrderedClothByActualCloth(
            List<Cloth> priorClothes,
            Cloth actualCloth)
            throws ClothException, ClothNotExistException {
        List<Cloth> clothesToAssign = priorClothes.stream()
                .filter(c ->
                        c.getClientArticle().getArticle()
                                .equals(actualCloth.getClientArticle().getArticle()))
                .filter(c -> c.getSize().equals(actualCloth.getSize()))
                .filter(c -> c.getBarcode() == 0)
                .filter(c -> c.getOrdinalNumber() == actualCloth.getOrdinalNumber())
                .collect(Collectors.toList());
        if (clothesToAssign.size() > 1) {
            throw ClothException.createForMistakeInAssignedClothes(
                    "There was more than 1 cloth to assign " +
                            "with same parameters",
                    clothesToAssign);
        } else if (clothesToAssign.isEmpty()) {
            throw new ClothNotExistException();
        } else {
            return clothesToAssign.get(0);
        }
    }

    private boolean clothIsAbsent(long clientId, Cloth cloth) {
        return cloth == null ||
                clothBelongsToOtherClient(cloth, clientId);
    }

    private ResponseClothAcceptance acceptForExchangeForNewOne(
            OrderType orderType,
            Cloth cloth,
            OrderType actualOrderType,
            ExchangeStrategy exchangeStrategy) {
        if (actualOrderType.equals(OrderType.EMPTY)) {
            Cloth clothForExchange = acceptForExchange(cloth);
            OrderStatus orderStatus = orderStatusService.create(
                    READY_FOR_REALIZATION,
                    user);
            MainOrder mainOrder = getMainOrder(
                    clothForExchange,
                    orderType,
                    orderStatus,
                    cloth.getClientArticle(),
                    cloth.getSize(),
                    cloth.getLengthModification(),
                    exchangeStrategy);
            OrdinalNumberResolver onr = OrdinalNumberResolver.createForExchangeForNewOnes(
                    clothForExchange);
            ClothOrder clothOrder = orderService.placeOne(
                    clothForExchange,
                    orderType,
                    orderStatus,
                    exchangeStrategy,
                    clothForExchange.getClientArticle(),
                    clothForExchange.getSize(),
                    clothForExchange.getLengthModification(),
                    mainOrder,
                    onr.getForExchangeForNewOne(clothForExchange.getOrdinalNumber()),
                    user);
            return ResponseClothAcceptance.createNewOrderAddedAndClothAcceptedResponse(clothOrder);
        } else if (actualOrderType.equals(EXCHANGE_FOR_NEW_ONE)) {
            ClothOrder order = acceptClothAndUpdateOrder(cloth);
            return ResponseClothAcceptance.createClothAcceptedResponse(order);
        } else {
            return getResponseForAnotherActiveOrder(cloth, 0);
        }
    }

    private MainOrder getMainOrder(
            Cloth clothForExchange,
            OrderType orderType,
            OrderStatus orderStatus,
            ClientArticle clientArticle,
            ClothSize size,
            LengthModification lengthModification,
            ExchangeStrategy exchangeStrategy) {
        return orderService.createMainOrder(
                orderType,
                orderStatus,
                clientArticle,
                size,
                lengthModification,
                exchangeStrategy,
                clothForExchange);
    }

    private ResponseClothAcceptance acceptForAutoExchange(Cloth cloth, OrderType actualOrderType) {
        if (actualOrderType.equals(OrderType.EMPTY)) {
            return ResponseClothAcceptance.createNoActiveOrderPresentResponse(cloth);
        } else {
            ClothOrder order = acceptClothAndUpdateOrder(cloth);
            orderService.updateMainOrderForClothAcceptance(order, user);
            return ResponseClothAcceptance.createClothAcceptedResponse(order);
        }
    }

    private ResponseClothAcceptance getResponseForAnotherActiveOrder(Cloth cloth, long barCode) {
        try {
            ClothOrder activeOrder = getOrder(cloth);
            return ResponseClothAcceptance.createAnotherOrderIsActiveResponse(activeOrder);
        } catch (ClothOrderException e) {
            e.printStackTrace();
            e.getMessage();
            return ResponseClothAcceptance.createClothNotFound(barCode);
        }
    }

    private OrderType getActualOrderType(Cloth cloth) {
        ClothOrder actualOrder = null;
        try {
            actualOrder = getOrder(cloth);
        } catch (NoActiveClothOrderException e) {
            return OrderType.EMPTY;
        } catch (ClothOrderException e) {
            e.printStackTrace();
            e.getMessage();
        }
        return actualOrder.getOrderType();
    }

    private ClothOrder acceptClothAndUpdateOrder(Cloth cloth) {
        Cloth acceptedCloth = acceptForExchange(cloth);
        try {
            OrderStatus.OrderStage actualOrderStage = getOrder(acceptedCloth).getOrderStatus().getOrderStage();
            switch (actualOrderStage) {
                case PENDING_FOR_ASSIGNMENT:
                    return orderService.setOrder(
                            READY_BUT_PENDING_FOR_ASSIGNMENT,
                            getOrder(acceptedCloth),
                            user);
                case READY_BUT_PENDING_FOR_CLOTH_RETURN:
                    return orderService.setOrder(
                            READY_FOR_REALIZATION,
                            getOrder(acceptedCloth),
                            user);
                case RELEASED_AND_PENDING_FOR_OLD_CLOTH_RETURN:
                    return orderService.setOrder(
                            FINALIZED,
                            getOrder(acceptedCloth),
                            user);
                default:
                    return orderService.getEmpty();
            }
        } catch (ClothOrderException e) {
            e.printStackTrace();
            return orderService.getEmpty();
        }
    }

    private Cloth acceptForExchange(Cloth cloth) {
        ClothStatus actualStatus = clothStatusService.create(ACCEPTED_FOR_EXCHANGE, cloth, user);
        cloth = clothesManager.updateCloth(actualStatus, cloth);
        return clothesRepository.save(cloth);
    }

    private ClothOrder getOrder(Cloth cloth) throws ClothOrderException {
        if (cloth.getExchangeOrder() == null) {
            throw new NoActiveClothOrderException("");
        } else {
            ClothOrder clothOrder = cloth.getExchangeOrder();
            if (clothOrder.isActive()) {
                return clothOrder;
            } else {
                throw new NoActiveClothOrderException("");
            }
        }

    }

    private ResponseClothAcceptance createClothNotFoundResponse(Cloth cloth, long id) {
        if (cloth == null) {
            return ResponseClothAcceptance.createClothNotFound(id);
        }
        return ResponseClothAcceptance.createClothBelongsToOtherClient(cloth);
    }


    private boolean clothBelongsToOtherClient(Cloth cloth, long clientId) {
        if (cloth != null && clientId != cloth.getClientId()) {
            return true;
        } else {
            return false;
        }
    }

    public List<Cloth> createExisting(List<Cloth> clothes) {
        return clothesRepository.saveAll(clothes);
    }

    public void hardDelete(List<Cloth> clothes) {
        clothes.stream().forEach(c -> hardDelete(c));
    }

    public void hardDelete(Cloth cloth) {
        List<ClothStatus> statusHistory = cloth.getStatusHistory();
        clothStatusService.hardDelete(statusHistory);
        cloth.setReleaseOrder(null);
        clothesRepository.deleteById(cloth.getId());
    }


    public void setAs(LifeCycleStatus status, Cloth cloth) {
        switch (status) {
            case IN_ROTATION:
                cloth.setLifeCycleStatus(status);
                clothesRepository.save(cloth);
                break;
        }
    }

    public void returnToEmployee(Cloth clothToReturn) {
        if (clothToReturn.getLifeCycleStatus().equals(ACCEPTED)) {
            setAs(IN_ROTATION, clothToReturn);
        }
    }

    public void updateStatusWhenOrderIsCreated(
            Cloth c,
            MainOrder mainOrder,
            User user) {
        updateStatusWhenOrderIsCreated(
                c,
                mainOrder.getOrderType(),
                mainOrder.getExchangeStrategy(),
                user);
    }


    public void updateStatusWhenOrderIsCreated(
            Cloth c,
            OrderType orderType,
            ExchangeStrategy exchangeStrategy,
            User user) {
        ClothDestination clothDestination;
        if (orderType.equals(NEW_ARTICLE)) {
            clothDestination = ClothDestination.FOR_RELEASE;
        } else {
            switch (exchangeStrategy) {
                case PIECE_FOR_PIECE:
                    clothDestination = ClothDestination.FOR_WITHDRAW_AND_EXCHANGE;
                    break;
                case NONE:
                case RELEASE_BEFORE_RETURN:
                default:
                    clothDestination = ClothDestination.FOR_WASH;
                    break;
            }
        }
        ClothStatus clothStatus = createClothStatus(c, clothDestination, user);
        c.setStatus(clothStatus);
        clothesRepository.save(c);
    }

    public void updateStatusWhenOrderIsCreated(Cloth c, OrderType ot, User u) {
        ClothDestination cd;
        switch (ot) {
            case REPAIR:
                cd = ClothDestination.FOR_REPAIR;
                break;
            case LENGTH_MODIFICATION:
                cd = ClothDestination.FOR_MODIFICATION;
                break;
            default:
                cd = ClothDestination.FOR_WASH;
                break;
        }
        ClothStatus cs = createClothStatus(c, cd, u);
        c.setStatus(cs);
        clothesRepository.save(c);
    }



    private ClothStatus createClothStatus(Cloth c, ClothDestination d, User u) {
        ClothActualStatus status;
        if (c.getStatusHistory().isEmpty()) {
            status = RELEASED;
        } else {
            status = c.getClothStatus().getStatus();
        }
        ClothStatus clothStatus = new ClothStatus();
        clothStatus.setCloth(c);
        clothStatus.setClothDestination(d);
        clothStatus.setDateOfUpdate(LocalDateTime.now());
        clothStatus.setStatus(status);
        clothStatus.setUser(u);
        return clothStatus;
    }

    public ClothesRepository getClothesRepository() {
        return clothesRepository;
    }

    public ClothOrdersRepository getClothOrdersRepository() {
        return clothOrdersRepository;
    }

    public OrderService getOrderService() {
        return orderService;
    }

    public OrderStatusService getOrderStatusService() {
        return orderStatusService;
    }

    public UserService getUserService() {
        return userService;
    }

    public OrderManager getOrderManager() {
        return orderManager;
    }

    public EmployeeService getEmployeeService() {
        return employeeService;
    }

    public ClothStatusService getClothStatusService() {
        return clothStatusService;
    }

    public ClientArticleService getClientArticleService() {
        return clientArticleService;
    }

    public User getUser() {
        return user;
    }

    public ClothesManager getClothesManager() {
        return clothesManager;
    }

    public ClothesCreator getClothesCreator() {
        return clothesCreator;
    }

    public BoxesRepository getBoxesRepository() {
        return boxesRepository;
    }
}
