package pl.bratosz.smartlockers.service;

import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.model.clothes.Cloth;
import pl.bratosz.smartlockers.model.clothes.LifeCycleStatus;
import pl.bratosz.smartlockers.model.orders.*;
import pl.bratosz.smartlockers.model.users.User;
import pl.bratosz.smartlockers.repository.OrdersStatusRepository;

import java.util.Date;
import java.util.List;

import static pl.bratosz.smartlockers.model.orders.OrderStatus.OrderStage.*;

@Service
public class OrderStatusService {
    private OrdersStatusRepository ordersStatusRepository;

    public OrderStatusService(OrdersStatusRepository ordersStatusRepository) {
        this.ordersStatusRepository = ordersStatusRepository;
    }

    public OrderStatus create(OrderType orderType, User user) {
        OrderStatus.OrderStage orderStage = resolveOrderStage(
                orderType,
                user);
        return create(orderStage, user);
    }

    public OrderStatus create(
            OrderStatus.OrderStage orderStage,
            ActionType actionType,
            User user) {
        OrderStatus status = create(orderStage, user);
        status.setActionPerformed(actionType);
        return ordersStatusRepository.save(status);
    }

    public OrderStatus create(
            OrderStatus.OrderStage orderStage,
            User user) {
        OrderStatus status = new OrderStatus();
        status.setOrderStage(orderStage);
        status.setUser(user);
        status.setDateOfUpdate(new Date());
        return ordersStatusRepository.save(status);
    }

    private OrderStatus.OrderStage resolveOrderStage(OrderType orderType, User user) {
        if (orderType.equals(OrderType.EMPTY)) {
            return EMPTY;
        } else {
            return user.getInitialStageForOrders();
        }
    }


    public void hardDelete(long clothOrderId) {
        ordersStatusRepository.deleteByClothOrderId(clothOrderId);
    }

    public void hardDeleteBy(long mainOrderId) {
        ordersStatusRepository.deleteByMainOrderId(mainOrderId);
    }

    public OrderStatus createInitialStageForReportedMainOrder(
            ExchangeStrategy exchangeStrategy,
            User user) {
        switch (exchangeStrategy) {
            case PIECE_FOR_PIECE:
                return create(IN_REALIZATION, user);
            case RELEASE_BEFORE_RETURN:
            default:
                return create(IN_REALIZATION, user);
        }
    }

    public OrderStatus createInitialStageForReportedClothOrder(
            Cloth clothForExchange,
            ExchangeStrategy exchangeStrategy,
            User user) {
        switch (exchangeStrategy) {
            case PIECE_FOR_PIECE:
                if(clothForExchange.getLifeCycleStatus()
                        .equals(LifeCycleStatus.ACCEPTED)) {
                    return create(READY_FOR_REALIZATION, user);
                } else {
                    return create(READY_BUT_PENDING_FOR_CLOTH_RETURN, user);
                }
            case RELEASE_BEFORE_RETURN:
                if(clothForExchange.getLifeCycleStatus()
                        .equals(LifeCycleStatus.IN_ROTATION)) {
                    return create(RELEASED_AND_PENDING_FOR_OLD_CLOTH_RETURN, user);
                } else {
                    return create(READY_FOR_REALIZATION, user);
                }
            default:
                return create(IN_REALIZATION, user);
        }
    }

    public OrderStatus createCopy(OrderStatus orderStatus) {
        return create(
                orderStatus.getOrderStage(),
                orderStatus.getActionPerformed(),
                orderStatus.getUser());
    }
}
