package pl.bratosz.smartlockers.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.exception.MyException;
import pl.bratosz.smartlockers.model.ArticleWithQuantity;
import pl.bratosz.smartlockers.model.Client;
import pl.bratosz.smartlockers.model.ClientArticle;
import pl.bratosz.smartlockers.model.Employee;
import pl.bratosz.smartlockers.model.clothes.*;
import pl.bratosz.smartlockers.model.orders.*;
import pl.bratosz.smartlockers.model.orders.parameters.complete.CompleteForExchangeAndRelease;
import pl.bratosz.smartlockers.model.orders.parameters.complete.CompleteOrderParameters;
import pl.bratosz.smartlockers.model.orders.parameters.newArticle.OrderParameters;
import pl.bratosz.smartlockers.model.users.User;
import pl.bratosz.smartlockers.repository.*;
import pl.bratosz.smartlockers.response.ResponseOrdersCreated;
import pl.bratosz.smartlockers.response.StandardResponse;
import pl.bratosz.smartlockers.response.UpdateResponse;
import pl.bratosz.smartlockers.service.clothes.OrdinalNumberResolver;
import pl.bratosz.smartlockers.service.managers.OrderManager;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static pl.bratosz.smartlockers.model.orders.ExchangeStrategy.NONE;
import static pl.bratosz.smartlockers.model.orders.OrderStatus.OrderStage;
import static pl.bratosz.smartlockers.model.orders.OrderType.NEW_ARTICLE;

@Service
public class OrderService {
    private ClothOrdersRepository clothOrdersRepository;
    private UserService userService;
    private ClientArticleService clientArticleService;
    private OrderStatusService orderStatusService;
    private OrderManager orderManager;
    private MainOrdersRepository mainOrdersRepository;
    private ClientRepository clientRepository;
    @Autowired
    private ClothService clothesService;
    private UsersRepository usersRepository;
    private EmployeesRepository employeesRepository;
    private MeasurementListService measurementListService;

    public OrderService(ClothOrdersRepository clothOrdersRepository,
                        UserService userService,
                        ClientArticleService clientArticleService,
                        OrderStatusService orderStatusService,
                        OrderManager orderManager,
                        MainOrdersRepository mainOrdersRepository, ClientRepository clientRepository, UsersRepository usersRepository, EmployeesRepository employeesRepository, MeasurementListService measurementListService) {
        this.clothOrdersRepository = clothOrdersRepository;
        this.userService = userService;
        this.clientArticleService = clientArticleService;
        this.orderStatusService = orderStatusService;
        this.orderManager = orderManager;
        this.mainOrdersRepository = mainOrdersRepository;
        this.clientRepository = clientRepository;
        this.usersRepository = usersRepository;
        this.employeesRepository = employeesRepository;
        this.measurementListService = measurementListService;
    }

    public ResponseOrdersCreated placeForNewEmployee(
            Set<OrderParameters> parameters,
            long employeeId,
            long userId) {
        long clientId = usersRepository.getActualClientId(userId);
        OrderType orderType = NEW_ARTICLE;
        ExchangeStrategy exchangeStrategy = NONE;
        Employee employee = employeesRepository.getEmployeeById(employeeId);
        Map<Long, MainOrder> mainOrdersByArticleIdMap =
                mapActiveMainOrderWithClientArticle(employee.getMainOrders());
        Map<Long, MainOrder> articleToReleaseToMainOrder = mapArticleWithQuantityToMainOrder(
                employee.getPosition().getArticlesWithQuantities(),
                mainOrdersByArticleIdMap);
        User user = userService.getUserById(userId);
        Set<ClothOrder> orders = new HashSet<>();
        parameters.stream()
                .forEach(params -> {
                    if (paramsAreNotEmpty(params)) {
                        if (mainOrderExist(articleToReleaseToMainOrder, params)) {
                            MainOrder mainOrder = articleToReleaseToMainOrder.get(params.getArticlesWithQuantityId());
                            orderManager.edit(mainOrder, params, user);
                        } else {
                            MainOrder mainOrder = getMainOrder(orderType, exchangeStrategy, employee, user, params);
                            orders.addAll(createClothOrdersForNewClothes(
                                    params.getQuantity(),
                                    mainOrder,
                                    user));
                        }
                    }
                });
        if (employee.getPosition().getArticlesWithQuantities().size() == employee.getMainOrders().size()) {
            measurementListService.updateMeasurementListWithMeasuredEmployee(employee, clientId);
        }
        return ResponseOrdersCreated.createForOrdersCreated(orderType, orders.size());
    }

    private MainOrder getMainOrder(
            OrderType orderType, ExchangeStrategy exchangeStrategy, Employee employee, User user, OrderParameters params) {
        ClientArticle article = clientArticleService
                .getById(params.getClientArticleId());
        OrderStatus orderStatus = orderStatusService
                .create(orderType, user);
        return createOrder(
                params.getSize(),
                params.getLengthModification(),
                article,
                employee,
                orderType,
                exchangeStrategy,
                orderStatus);
    }

    private boolean paramsAreNotEmpty(OrderParameters params) {
        return params.getQuantity() > 0 && !params.getSize().equals(ClothSize.SIZE_EMPTY);
    }

    private boolean mainOrderExist(Map<Long, MainOrder> articleToReleaseToMainOrder, OrderParameters params) {
        return articleToReleaseToMainOrder.containsKey(params.getArticlesWithQuantityId());
    }

    private Map<Long, MainOrder> mapArticleWithQuantityToMainOrder(
            Set<ArticleWithQuantity> articlesToRelease,
            Map<Long, MainOrder> mainOrdersMap) {
        Map<Long, MainOrder> map = new HashMap<>();
        for (ArticleWithQuantity a : articlesToRelease) {
            for (ClientArticle c : a.getAvailableArticles()) {
                long clientArticleId = c.getId();
                if (mainOrdersMap.containsKey(clientArticleId)) {
                    map.put(
                            a.getId(),
                            mainOrdersMap.get(clientArticleId));
                    break;
                }
            }
        }
        return map;
    }

    private Map<Long, MainOrder> mapActiveMainOrderWithClientArticle(List<MainOrder> mainOrders) {
        Map<Long, MainOrder> map = new HashMap<>();
        for (MainOrder o : mainOrders) {
            if (o.isActive()) map.put(o.getDesiredClientArticle().getId(), o);
        }
        return map;
    }

    private Set<ClothOrder> createClothOrdersForNewClothes(
            int clothQuantity,
            MainOrder mainOrder,
            User user) {
        Set<ClothOrder> clothOrders = new HashSet<>();
        Set<Cloth> newClothes = clothesService
                .createAdditional(clothQuantity, mainOrder, user);
        for (Cloth c : newClothes) {
            OrderStatus clothOrderStatus = orderStatusService
                    .create(mainOrder.getOrderType(), user);
            clothesService.updateStatusWhenOrderIsCreated(c, mainOrder, user);
            clothOrders.add(
                    placeOne(c, clothOrderStatus, mainOrder));
        }
        return clothOrders;
    }

    public ClothOrder placeOne(
            Cloth clothForRelease,
            OrderStatus clothOrderStatus,
            MainOrder mainOrder) {
        ClothOrder order = orderManager.createForAdditionalCloth(
                clothForRelease,
                clothOrderStatus,
                mainOrder);
        return clothOrdersRepository.save(order);
    }

    public StandardResponse placeMany(
            OrderParameters p,
            long userId) {
        User user = userService.getUserById(userId);
        Client client = clientRepository.getById(user.getActualClientId());
        List<Cloth> clothes = new LinkedList<>();
        List<Cloth> clothesWithActiveOrders = new LinkedList<>();
        if(p.getBarcodes().length > 0) {
            clothes = clothesService.getByBarcodes(p.getBarcodes());
            clothesWithActiveOrders = getClothesWithActiveOrders(clothes);
        }
        if (clothesWithActiveOrders.isEmpty()) {
            switch (p.getOrderType()) {
                case EXCHANGE_FOR_NEW_ONE:
                    return exchangeForNewOnes(p.getLengthModification(), clothes, p.getOrderType(),
                            p.getExchangeDescription(), user, client);
                case CHANGE_SIZE:
                    return exchangeForAnotherSize(p.getSize(), p.getLengthModification(), clothes, p.getOrderType(),
                            p.getExchangeDescription(), user, client);
                case CHANGE_ARTICLE:
                    return exchangeForAnotherArticle(
                            p.getClientArticleId(), p.getSize(), p.getLengthModification(), clothes, p.getOrderType(),
                            p.getExchangeDescription(), client, user);
                case NEW_ARTICLE:
                    return addNewClothes(p, user);
                default:
                    return StandardResponse.createForFailure("Typ zamówienia nieobsługiwany: " + p.getOrderType().getName());
            }
        } else {
            return StandardResponse.createForFailure(
                    "Nie udało się utworzyć zamówienia, z powodu innych aktywnych zamówień dla tych sztuk odzieży",
                    clothesWithActiveOrders);
        }
    }

    public ClothOrder setOrder(
            OrderStage orderStage,
            ClothOrder order,
            User user) {
        order = orderManager.updateClothOrder(
                orderStage, order, user);
        clothOrdersRepository.save(order);
        return order;
    }

    public ClothOrder getEmpty() {
        return new ClothOrder();
    }

    public void updateReportedMainOrders(Collection<MainOrder> reportedOrders) {
        for (MainOrder o : reportedOrders) {
            updateReportedMainOrder(o);
        }
    }

    public void updateReportedMainOrder(long id) {
        MainOrder order = mainOrdersRepository.getById(id);
        updateReportedMainOrder(order);
    }

    private void updateReportedMainOrder(MainOrder order) {
        order.setReported(true);
        OrderStatus orderStatus = orderStatusService.createInitialStageForReportedMainOrder(
                order.getExchangeStrategy(),
                order.getOrderStatus().getUser());
        order.setOrderStatus(orderStatus);
        updateReportedClothOrders(order.getClothOrders());
        mainOrdersRepository.save(order);
    }

    public void updateReportedEmployees(Set<Employee> employees) {
        for (Employee e : employees) {
            updateReportedMainOrders(e.getMainOrders());
        }
    }


    public void updateReportedClothOrders(Set<ClothOrder> orders) {
        OrderStatus orderStatus;
        for (ClothOrder c : orders) {
            c.setReported(true);
            orderStatus = orderStatusService.createInitialStageForReportedClothOrder(
                    c.getClothToExchange(),
                    c.getExchangeStrategy(),
                    c.getOrderStatus().getUser());
            c.setOrderStatus(orderStatus);
            clothOrdersRepository.save(c);
        }
    }

    private List<Cloth> getClothesWithActiveOrders(List<Cloth> clothes) {
        List<Cloth> clothesWithActiveOrders = new LinkedList<>();
        for (Cloth cloth : clothes) {
            if (containsActiveOrder(cloth)) {
                clothesWithActiveOrders.add(cloth);
            }
        }
        return clothesWithActiveOrders;
    }

    private boolean containsActiveOrder(Cloth cloth) {
        if (cloth.getExchangeOrder() != null) {
            if (cloth.getExchangeOrder().isActive()) {
                return true;
            }
        } else if (cloth.getReleaseOrder() != null) {
            if (cloth.getReleaseOrder().isActive()) {
                return true;
            }
        }
        return false;
    }

    private StandardResponse addNewClothes(
            OrderParameters params,
            User user) {
        OrderType orderType = NEW_ARTICLE;
        ExchangeStrategy exchangeStrategy = NONE;
        ClientArticle article = clientArticleService.getById(params.getClientArticleId());
        Employee employee = employeesRepository.getEmployeeById(params.getEmployeeId());
        try {
            MainOrder mainOrder = getExistingOrder(employee.getMainOrders(), params.getSize(), article);
            if(mainOrder.isOrderEmpty()) {
                 mainOrder = getMainOrder(orderType, exchangeStrategy, employee, user, params);
            }
            createClothOrdersForNewClothes(params.getQuantity(), mainOrder, user);
            return StandardResponse.createForSucceed("Utworzono zamówienie typu: " + orderType.getName());
        } catch (MyException e) {
            return StandardResponse.createForFailure("Nie udało się utworzyć zamówienia z powodu: " + e.getMessage());
        }
    }

    private MainOrder getExistingOrder(List<MainOrder> mainOrders, ClothSize size, ClientArticle article) throws MyException {
        List<MainOrder> matchingMainOrders = mainOrders.stream()
                .filter(o -> o.isActive() && !o.isReported())
                .filter(o -> o.getDesiredSize().equals(size) && o.getDesiredClientArticle().equals(article))
                .collect(Collectors.toList());
        if(matchingMainOrders.size() == 1) {
            return matchingMainOrders.get(0);
        } else if (matchingMainOrders.isEmpty()) {
            return orderManager.createEmpty();
        } else {
            throw new MyException("More than one matching orders exception");
        }
    }

    private StandardResponse exchangeForAnotherArticle(
            long clientArticleId,
            ClothSize desiredSize,
            LengthModification lengthModification,
            List<Cloth> clothesForExchange,
            OrderType orderType,
            String exchangeDescription,
            Client client,
            User user) {
        ClientArticle desiredArticle = clientArticleService.getById(clientArticleId);
        ExchangeStrategy exchangeStrategy = client.getExchangeStrategies().get(orderType);
        OrderStatus orderStatus = orderStatusService.create(orderType, user);
        MainOrder mainOrder = null;
        OrdinalNumberResolver onr = OrdinalNumberResolver.createForChangeArticle(
                desiredSize,
                desiredArticle,
                clothesForExchange.get(0));
        for (Cloth cloth : clothesForExchange) {
            OrderStatus clothOrderStatus = orderStatusService.create(orderType, user);
            clothesService.updateStatusWhenOrderIsCreated(cloth, orderType, exchangeStrategy, user);
            if (mainOrder == null) {
                mainOrder = createMainOrder(
                        desiredArticle,
                        desiredSize,
                        lengthModification,
                        orderType,
                        orderStatus,
                        exchangeStrategy,
                        cloth,
                        exchangeDescription);
            }
            placeOne(
                    cloth,
                    orderType,
                    clothOrderStatus,
                    exchangeStrategy,
                    desiredArticle,
                    desiredSize,
                    lengthModification,
                    mainOrder,
                    onr.getNextNumber(),
                    user);
        }
        return StandardResponse.createForSucceed("Utworzono zamówienie typu: " + orderType);
    }

    private StandardResponse exchangeForNewOnes(
            LengthModification lengthModification,
            List<Cloth> clothesForExchange,
            OrderType orderType,
            String exchangeDescription,
            User user,
            Client client) {
        ExchangeStrategy exchangeStrategy = client.getExchangeStrategies().get(orderType);
        OrderStatus orderStatus = orderStatusService.create(orderType, user);
        MainOrder mainOrder = null;
        OrdinalNumberResolver onr = OrdinalNumberResolver.createForExchangeForNewOnes(clothesForExchange.get(0));
        for (Cloth cloth : clothesForExchange) {
            OrderStatus clothOrderStatus = orderStatusService.create(orderType, user);
            clothesService.updateStatusWhenOrderIsCreated(cloth, orderType, exchangeStrategy, user);
            if (mainOrder == null) {
                mainOrder = createMainOrder(
                        lengthModification,
                        orderType,
                        orderStatus,
                        exchangeStrategy,
                        cloth,
                        exchangeDescription);
            }
            placeOne(
                    cloth,
                    orderType,
                    clothOrderStatus,
                    exchangeStrategy,
                    cloth.getClientArticle(),
                    cloth.getSize(),
                    lengthModification,
                    mainOrder,
                    onr.getForExchangeForNewOne(cloth.getOrdinalNumber()),
                    user);
        }
        return StandardResponse.createForSucceed("Utworzono zamówienie typu: " + orderType);
    }

    private StandardResponse exchangeForAnotherSize(
            ClothSize desiredSize,
            LengthModification lengthModification,
            List<Cloth> clothesForExchange,
            OrderType orderType,
            String exchangeDescription,
            User user,
            Client client) {
        ExchangeStrategy exchangeStrategy = client.getExchangeStrategies().get(orderType);
        OrderStatus orderStatus = orderStatusService.create(orderType, user);
        MainOrder mainOrder = null;
        OrdinalNumberResolver onr = OrdinalNumberResolver.createForChangeSize(
                desiredSize,
                clothesForExchange.get(0),
                employeesRepository.getEmployeeById(clothesForExchange.get(0).getEmployee().getId()));
        for (Cloth cloth : clothesForExchange) {
            OrderStatus clothOrderStatus = orderStatusService.create(orderType, user);
            clothesService.updateStatusWhenOrderIsCreated(cloth, orderType, exchangeStrategy, user);
            if (mainOrder == null) {
                mainOrder = createMainOrder(
                        desiredSize,
                        lengthModification,
                        orderType,
                        orderStatus,
                        exchangeStrategy,
                        cloth,
                        exchangeDescription);
            }
            placeOne(
                    cloth,
                    orderType,
                    clothOrderStatus,
                    exchangeStrategy,
                    cloth.getClientArticle(),
                    desiredSize,
                    lengthModification,
                    mainOrder,
                    onr.getNextNumber(),
                    user);
        }
        return StandardResponse.createForSucceed("Utworzono zamówienie typu: " + orderType);
    }

    private MainOrder createOrder(
            OrderType orderType,
            OrderStatus orderStatus,
            ExchangeStrategy exchangeStrategy,
            Cloth clothToExchange,
            String exchangeDescription) {
        return createOrder(
                clothToExchange.getSize(),
                clothToExchange.getLengthModification(),
                orderType,
                orderStatus,
                exchangeStrategy,
                clothToExchange,
                exchangeDescription);
    }

    private MainOrder createOrder(
            ClothSize desiredSize,
            LengthModification lengthModification,
            OrderType orderType,
            OrderStatus orderStatus,
            ExchangeStrategy exchangeStrategy,
            Cloth clothToExchange,
            String exchangeDescription) {
        return createOrder(
                clothToExchange.getClientArticle(),
                desiredSize,
                lengthModification,
                orderType,
                orderStatus,
                exchangeStrategy,
                clothToExchange,
                exchangeDescription);
    }

    public MainOrder createOrder(
            ClientArticle desiredArticle,
            ClothSize desiredSize,
            LengthModification lengthModification,
            OrderType orderType,
            OrderStatus orderStatus,
            ExchangeStrategy exchangeStrategy,
            Cloth clothToExchange,
            String exchangeDescription) {
        MainOrder mainOrder = MainOrder.createDefault();
        mainOrder.setEmployee(clothToExchange.getEmployee());
        mainOrder.setOrderType(orderType);
        mainOrder.setOrderStatus(orderStatus);
        mainOrder.setExchangeStrategy(exchangeStrategy);
        mainOrder.setDesiredClientArticle(desiredArticle);
        mainOrder.setDesiredSize(desiredSize);
        mainOrder.setLengthModification(lengthModification);
        mainOrder.setPreviousClientArticle(clothToExchange.getClientArticle());
        mainOrder.setPreviousSize(clothToExchange.getSize());
        mainOrder.setDescription(exchangeDescription);
        return mainOrdersRepository.save(mainOrder);
    }

    private MainOrder createOrder(
            ClothSize size,
            LengthModification lengthModification,
            ClientArticle article,
            Employee employee,
            OrderType orderType,
            ExchangeStrategy exchangeStrategy,
            OrderStatus orderStatus) {
        MainOrder mainOrder = MainOrder.createDefault();
        mainOrder.setDesiredClientArticle(article);
        mainOrder.setPreviousClientArticle(article);
        mainOrder.setDesiredSize(size);
        mainOrder.setPreviousSize(size);
        mainOrder.setLengthModification(lengthModification);
        mainOrder.setOrderStatus(orderStatus);
        mainOrder.setExchangeStrategy(exchangeStrategy);
        mainOrder.setEmployee(employee);
        mainOrder.setOrderType(orderType);
        return mainOrdersRepository.save(mainOrder);
    }


    private Cloth getCloth(List<Cloth> clothes) {
        return clothes.stream().findFirst().get();
    }

    //clear cloth and hard hardDelete previous cloth order
    public ClothOrder placeOne(Cloth clothForExchange,
                               OrderType orderType,
                               OrderStatus orderStatus,
                               ExchangeStrategy exchangeStrategy,
                               ClientArticle clientArticle,
                               ClothSize size,
                               LengthModification lengthModification,
                               MainOrder mainOrder,
                               int ordinalNumber,
                               User user) {
        deleteInactiveOrder(clothForExchange);
        Employee employee = clothForExchange.getEmployee();
        Cloth clothForRelease = clothesService
                .createNewForAssignInsteadExisting(
                        ordinalNumber,
                        clientArticle,
                        size,
                        lengthModification,
                        employee,
                        user);
        CompleteForExchangeAndRelease completeParameters =
                CompleteOrderParameters.createForClothExchangeAndRelease(
                        clothForExchange,
                        clothForRelease,
                        employee,
                        orderType,
                        orderStatus,
                        exchangeStrategy,
                        user);
        ClothOrder order = orderManager.createOne(completeParameters, user);
        order.setMainOrder(mainOrder);
        return clothOrdersRepository.save(order);
    }

    private void deleteInactiveOrder(Cloth clothForExchange) {
        if (clothForExchange.getExchangeOrder() != null) {
            ClothOrder exchangeOrder = clothForExchange.getExchangeOrder();
            hardDelete(exchangeOrder);
        }
    }

    public void hardDelete(long orderId) {
        ClothOrder order = clothOrdersRepository.getById(orderId);
        hardDelete(order);
    }

    public void hardDelete(ClothOrder order) {
        int clothOrdersAmount = order.getMainOrder().getClothOrders().size();
        hardDeleteOrderStatuses(order);
        clothOrdersRepository.deleteById(order.getId());
        hardDeleteClothToRelease(order);
        if (clothOrdersAmount == 1) {
            hardDeleteMainOrder(order.getMainOrder());
        }
    }

    public void hardDelete(MainOrder order) {
        order.getClothOrders().stream()
                .forEach(o -> {
                    hardDeleteOrderStatuses(o);
                    clothOrdersRepository.deleteById(o.getId());
                    hardDeleteClothToRelease(o);
                });
        hardDeleteOrderStatuses(order);
        mainOrdersRepository.deleteMainOrderById(order.getId());
    }


    private void hardDeleteMainOrder(MainOrder mainOrder) {
        hardDeleteOrderStatuses(mainOrder);
        mainOrdersRepository.deleteMainOrderById(mainOrder.getId());
    }

    public UpdateResponse performActionOnOrders(
            ActionType actionType,
            List<OrderEditInfo> orderEditInfos,
            long userId) {
        User user = userService.getUserById(userId);
        orderEditInfos.stream().forEach(e -> {
            MainOrder mainOrder = mainOrdersRepository.getById(e.getOrderId());
            e.setMainOrder(mainOrder);
        });
        return orderManager.perform(actionType, orderEditInfos, user);
    }

    public Set<MainOrder> getByEmployeeId(long employeeId) {
        return mainOrdersRepository.getByEmployeeId(employeeId);
    }

    private Set<MainOrder> getClothOrdersByIds(long[] clothOrdersIds) {
        Set<MainOrder> mainOrders = new HashSet<>();
        for (int i = 0; i < clothOrdersIds.length; i++) {
            long id = clothOrdersIds[i];
            MainOrder mainOrder = mainOrdersRepository.getById(id);
            mainOrders.add(mainOrder);
        }
        return mainOrders;
    }

    private void hardDeleteClothToRelease(ClothOrder order) {
        Cloth cloth = order.getClothToRelease();
        if (cloth != null) {
            if (cloth.getLifeCycleStatus() != null && cloth.getLifeCycleStatus().equals(LifeCycleStatus.BEFORE_RELEASE)) {
                clothesService.hardDelete(cloth);
            }
        }

}

    private void hardDeleteOrderStatuses(MainOrder order) {
        orderStatusService.hardDeleteBy(order.getId());
    }

    private void hardDeleteOrderStatuses(ClothOrder order) {
        orderStatusService.hardDelete(order.getId());
    }

    private MainOrder createMainOrder(
            ClientArticle desiredArticle,
            ClothSize desiredSize,
            LengthModification lengthModification,
            OrderType orderType,
            OrderStatus orderStatus,
            ExchangeStrategy exchangeStrategy,
            Cloth cloth,
            String exchangeDescription) {
        return createMainOrder(
                orderType,
                orderStatus,
                desiredArticle,
                desiredSize,
                lengthModification,
                exchangeStrategy,
                cloth,
                exchangeDescription);
    }

    private MainOrder createMainOrder(
            ClothSize desiredSize,
            LengthModification lengthModification,
            OrderType orderType,
            OrderStatus orderStatus,
            ExchangeStrategy exchangeStrategy,
            Cloth cloth,
            String exchangeDescription) {
        return createMainOrder(
                orderType,
                orderStatus,
                cloth.getClientArticle(),
                desiredSize,
                lengthModification,
                exchangeStrategy,
                cloth,
                exchangeDescription);
    }

    private MainOrder createMainOrder(
            LengthModification lengthModification,
            OrderType orderType,
            OrderStatus orderStatus,
            ExchangeStrategy exchangeStrategy,
            Cloth clothForExchange,
            String exchangeDescription) {
        return createMainOrder(
                orderType,
                orderStatus,
                clothForExchange.getClientArticle(),
                clothForExchange.getSize(),
                lengthModification,
                exchangeStrategy,
                clothForExchange,
                exchangeDescription);
    }

    public MainOrder createMainOrder(
            OrderType orderType,
            OrderStatus orderStatus,
            ClientArticle clientArticle,
            ClothSize size,
            LengthModification lengthModification,
            ExchangeStrategy exchangeStrategy,
            Cloth clothForExchange) {
        String exchangeDescription = "";
        return createMainOrder(
                orderType, orderStatus, clientArticle, size, lengthModification,
                exchangeStrategy, clothForExchange, exchangeDescription);
    }

    public MainOrder createMainOrder(
            OrderType orderType,
            OrderStatus orderStatus,
            ClientArticle clientArticle,
            ClothSize size,
            LengthModification lengthModification,
            ExchangeStrategy exchangeStrategy,
            Cloth clothForExchange,
            String exchangeDescription) {
        Optional<MainOrder> mainOrder = clothForExchange.getEmployee().getMainOrders()
                .stream()
                .filter(o ->
                        ((o.getOrderStatus().getOrderStage().isActive()) &&
                                !o.isReported() &&
                                (o.getOrderType() == orderType)) &&
                                (o.getDesiredSize() == size) &&
                                (o.getDesiredClientArticle().equals(clientArticle)) &&
                                (o.getPreviousClientArticle().equals(clothForExchange.getClientArticle())) &&
                                (o.getOrderStatus().getOrderStage().isModificationAllowed())).findFirst();
        if (mainOrder.isPresent()) {
            return mainOrder.get();
        } else {
            return createOrder(
                    clientArticle,
                    size,
                    lengthModification,
                    orderType,
                    orderStatus,
                    exchangeStrategy,
                    clothForExchange,
                    exchangeDescription);
        }
    }

    private void updateMainOrder(ClothOrder order, User user) {
        MainOrder mainOrder = order.getMainOrder();
        Set<ClothOrder> clothOrders = mainOrder.getClothOrders();
        AtomicBoolean isFinalized = new AtomicBoolean(true);
        clothOrders.stream()
                .forEach(c -> {
                    if (!c.getOrderStatus().getOrderStage().equals(OrderStage.FINALIZED)) {
                        isFinalized.set(false);
                    }
                });
        if (isFinalized.get()) {
            OrderStatus finalized = orderStatusService.create(OrderStage.FINALIZED, user);
            mainOrder.setOrderStatus(finalized);
            mainOrder.setActive(false);
        }
        mainOrdersRepository.save(mainOrder);
    }

    public void updateMainOrderForClothAcceptance(ClothOrder order, User user) {
        AtomicBoolean isFinalized = new AtomicBoolean(true);
        MainOrder mainOrder = order.getMainOrder();
        Set<ClothOrder> clothOrders = mainOrder.getClothOrders();
        clothOrders.stream().forEach(clothOrder -> {
            if (!clothOrder.isReported()) mainOrder.setReported(false);
            if (!clothOrder.getOrderStatus().getOrderStage().equals(OrderStage.FINALIZED))
                isFinalized.set(false);
        });
        if (isFinalized.get()) {
            OrderStatus finalized = orderStatusService.create(OrderStage.FINALIZED, user);
            mainOrder.setOrderStatus(finalized);
            mainOrder.setActive(false);
        }
        mainOrdersRepository.save(mainOrder);
    }

    public void updateForWithdrawCloth(Cloth cloth, User user) {
        ClothOrder exchangeOrder = cloth.getExchangeOrder();
        OrderStage orderStage = OrderStage.EMPTY;
        if (exchangeOrder != null && exchangeOrder.isActive()) {
            if (exchangeOrder.getOrderStatus().getOrderStage() != null) {
                orderStage = exchangeOrder.getOrderStatus().getOrderStage();
                switch (orderStage) {
                    case PENDING_FOR_ASSIGNMENT:
                        orderStage = OrderStage.READY_BUT_PENDING_FOR_ASSIGNMENT;
                        break;
                    case READY_BUT_PENDING_FOR_CLOTH_RETURN:
                        orderStage = OrderStage.READY_FOR_REALIZATION;
                        break;
                    case RELEASED_AND_PENDING_FOR_OLD_CLOTH_RETURN:
                        orderStage = OrderStage.FINALIZED;
                        break;
                    default:
                        break;
                }
            }
            OrderStatus orderStatus = orderStatusService.create(orderStage, user);
            exchangeOrder.setActive(orderStage.isActive());
            exchangeOrder.setOrderStatus(orderStatus);
            updateMainOrder(exchangeOrder, user);
            clothOrdersRepository.save(exchangeOrder);
        }
    }

    private void updateForReleasedCloth(Cloth newCloth, User user) {
        ClothOrder releaseOrder = newCloth.getReleaseOrder();
        if (releaseOrder != null) {
            releaseOrder.setActive(false);
            OrderStatus orderStatus = orderStatusService.create(OrderStage.FINALIZED, user);
            releaseOrder.setOrderStatus(orderStatus);
            updateMainOrder(releaseOrder, user);
            clothOrdersRepository.save(releaseOrder);
        }
    }

    public void update(Cloth cloth, User user) {
        ClothStatus clothStatus = cloth.getClothStatus();
        switch (clothStatus.getStatus()) {
            case ASSIGNED:
                updateForAssignedCloth(cloth, user);
                break;
            case RELEASED:
                updateForReleasedCloth(cloth, user);
                break;
            case ACCEPTED_AND_WITHDRAWN:
                updateForWithdrawCloth(cloth, user);
            default:
        }
    }

    private void updateForAssignedCloth(Cloth newCloth, User user) {
        ClothOrder releaseOrder = newCloth.getReleaseOrder();
        OrderStatus orderStatus = orderStatusService.create(OrderStage.IN_REALIZATION, user);
        releaseOrder.setOrderStatus(orderStatus);
        clothOrdersRepository.save(releaseOrder);
    }


    public void updateForEmployeeDismiss(List<MainOrder> mainOrders) {
        for (MainOrder o : mainOrders) {
            hardDelete(o);
        }
    }

    public StandardResponse getMainOrdersToReport(long userId) {
        long clientId = usersRepository.getActualClientId(userId);
        Set<MainOrder> orders = mainOrdersRepository.getOrdersToReport(clientId);
        return StandardResponse.createForSucceed(orders);
    }
}
