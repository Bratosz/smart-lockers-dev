package pl.bratosz.smartlockers.service.managers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.model.*;
import pl.bratosz.smartlockers.model.clothes.Cloth;
import pl.bratosz.smartlockers.model.clothes.ClothDestination;
import pl.bratosz.smartlockers.model.users.User;
import pl.bratosz.smartlockers.service.MeasurementListService;
import pl.bratosz.smartlockers.service.OrderService;
import pl.bratosz.smartlockers.service.SimpleBoxService;
import pl.bratosz.smartlockers.service.managers.cloth.ClothesManager;

import java.util.LinkedList;
import java.util.List;

@Service
public class EmployeeManager {
    private ClothesManager clothesManager;
    private BoxManager boxManager;
    private Employee employee;
    private SimpleBoxService simpleBoxService;
    private MeasurementListService measurementListService;
    private User user;
    @Autowired
    private OrderService orderService;

    public EmployeeManager(ClothesManager clothesManager, BoxManager boxManager,
                           SimpleBoxService simpleBoxService, MeasurementListService measurementListService) {
        this.clothesManager = clothesManager;
        this.boxManager = boxManager;
        this.simpleBoxService = simpleBoxService;
        this.measurementListService = measurementListService;
    }

    public Employee dismiss(Employee employee, boolean clothesReturned, User user) {
        this.employee = employee;
        this.user = user;
        if(employee.getBox() != null) {
            updateBoxAndSetAsPastBoxForDismiss();
        }
        updateOrdersForDismiss();
        updateClothesForDismiss(clothesReturned);
        updateEmployeeForDismiss();
        updateListOfEmployeesToRelease();
        return this.employee;
    }

    private void updateOrdersForDismiss() {
        if(!employee.getMainOrders().isEmpty()){
            orderService.updateForEmployeeDismiss(employee.getMainOrders());
            employee.setMainOrders(new LinkedList<>());
        }
    }

    private void updateListOfEmployeesToRelease() {
        measurementListService.remove(employee);
    }

    private void updateEmployeeForDismiss() {
        employee.setActive(false);
        employee.setBox(null);
    }

    private void updateClothesForDismiss(boolean clothesReturned) {
        List<Cloth> clothes = employee.getClothes();
        if(clothesReturned) {
            clothes = clothesManager.set(ClothDestination.FOR_DISPOSAL, clothes, user);
        } else {
            clothes = clothesManager.set(ClothDestination.FOR_WITHDRAW_AND_DELETE, clothes, user);
        }
        employee.setClothes(clothes);
    }

    private void updateBoxAndSetAsPastBoxForDismiss() {
        Box box = employee.getBox();
        boxManager.release(box);
        employee.setAsPastBox(
                simpleBoxService.createSimpleBox(box, employee));
    }
}