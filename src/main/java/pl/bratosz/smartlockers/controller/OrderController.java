package pl.bratosz.smartlockers.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.web.bind.annotation.*;
import pl.bratosz.smartlockers.model.*;
import pl.bratosz.smartlockers.model.orders.*;
import pl.bratosz.smartlockers.model.orders.parameters.newArticle.OrderParameters;
import pl.bratosz.smartlockers.response.ResponseOrdersCreated;
import pl.bratosz.smartlockers.response.StandardResponse;
import pl.bratosz.smartlockers.response.UpdateResponse;
import pl.bratosz.smartlockers.service.OrderService;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/order")
public class OrderController {
    private OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/delete/{orderId}")
    public int delete(@PathVariable long orderId) {
        orderService.hardDelete(orderId);
        return 1;
    }

    @PostMapping("/place-for-new-clothes/{employeeId}/{userId}")
    public ResponseOrdersCreated placeForNewClothes(
            @PathVariable long employeeId,
            @PathVariable long userId,
            @RequestBody Set<OrderParameters> parametersForNewArticles) {
        return orderService.placeForNewEmployee(
                parametersForNewArticles,
                employeeId,
                userId);
    }

    @JsonView(Views.OrderBasicInfo.class)
    @PostMapping("/place-many/{userId}")
    public StandardResponse placeMany(@PathVariable long userId, @RequestBody OrderParameters parameters) {
        return orderService.placeMany(parameters, userId);
    }

    @JsonView(Views.OrderBasicInfo.class)
    @GetMapping("/get-by-employee/{employeeId}")
    public Set<MainOrder> getByEmployeeId(@PathVariable long employeeId) {
        return orderService.getByEmployeeId(employeeId);
    }

    @PutMapping("/perform-action/{actionType}/{userId}")
    @JsonView(Views.InternalForClothOrders.class)
    public UpdateResponse performActionOnOrders(
            @PathVariable ActionType actionType,
            @PathVariable long userId,
            @RequestBody List<OrderEditInfo> orderEditInfos) {
        return orderService.performActionOnOrders(
                actionType,
                orderEditInfos,
                userId);
    }

    @JsonView(Views.OrderBasicInfo.class)
    @GetMapping("/get-to-report/{userId}")
    public StandardResponse getToReport(
            @PathVariable long userId) {
        return orderService.getMainOrdersToReport(userId);
    }

    @JsonView(Views.Public.class)
    @PostMapping("/report/{mainOrderId}/{userId}")
    public StandardResponse report(
            @PathVariable long mainOrderId,
            @PathVariable long userId) {
        orderService.updateReportedMainOrder(mainOrderId);
        return StandardResponse.createForSucceed();
    }
}
