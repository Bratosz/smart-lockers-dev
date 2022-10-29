package pl.bratosz.smartlockers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.bratosz.smartlockers.model.*;
import pl.bratosz.smartlockers.model.orders.OrderType;
import pl.bratosz.smartlockers.service.employees.EmployeeWithActiveOrders;

import java.util.List;
import java.util.Set;

@Repository
public interface EmployeesRepository extends JpaRepository<Employee, Long> {
    List<Employee> getByFirstNameAndLastName(String firstName, String lastName);


    Employee getEmployeeById(Long id);

    @Query("select e from Employee e " +
            "where e.lastName like %:lastName% " +
            "and e.department.client.id = :clientId")
    List<Employee> getEmployeesByLastName(
            @Param("lastName") String lastName,
            @Param("clientId") long clientId);

    Integer deleteEmployeeById(Long id);

    @Query("select e from Employee e " +
    "where e.firstName like %:firstName% and e.department.client.id = :clientId")
    List<Employee> getEmployeesByFirstName(
            @Param("firstName") String firstName,
            @Param("clientId") long clientId);

    @Query("select e from Employee e " +
            "where e.box.locker.lockerNumber = :lockerNumber " +
            "and e.box.boxNumber = :boxNumber " +
            "and e.box.locker.plant = :plant " +
            "and e.department = :department")
    List<Employee> getBy(
            int lockerNumber, int boxNumber, Plant plant, Department department);

    @Query("select e from Employee e " +
            "where e.box.locker.lockerNumber = :lockerNumber " +
            "and e.box.boxNumber = :boxNumber " +
            "and e.box.locker.plant = :plant")
    List<Employee> getBy(
            int lockerNumber, int boxNumber, Plant plant);

    @Query("select e from Employee e ")
    List<Employee> getAll();

    @Query("select e from Employee e join e.mainOrders o where o.active = true and e.box.locker.plant.client.id = :clientId")
    Set<Employee> getEmployeesWithActiveOrders(long clientId);

    @Query("select e from Employee e join e.mainOrders o where o.reported = false and o.active = true and e.box.locker.plant.client.id = :clientId " +
            "and e.active = true")
    Set<Employee> getEmployeesWithActiveAndNotReportedOrders(long clientId);

    @Query("select e from Employee e where e.active = true and e.box.locker.plant.client.id = :clientId")
    Set<Employee> getAllActive(long clientId);

    @Query("select new pl.bratosz.smartlockers.model.SimpleEmployee(e.firstName, e.lastName, e.box.locker.lockerNumber, e.box.boxNumber, e.department.name) " +
            "from Employee e join e.mainOrders o where e.box.locker.plant = :plant and o.active = true")
    Set<SimpleEmployee> getSimpleEmployeesWithActiveOrdersBy(Plant plant);

    @Query("select e from Employee e where e.box.locker.plant = :plant and e.position = :position")
    Set<Employee> getByPlantAndPosition(Plant plant, Position position);

    @Query("select new pl.bratosz.smartlockers.service.employees.EmployeeWithActiveOrders(" +
            "e.id, e.department.name, e.box.locker.plant.plantNumber, e.box.locker.lockerNumber, e.box.boxNumber," +
            "e.lastName, e.firstName, count(o))  from Employee e join e.mainOrders o on e.id = o.employee.id where e.department.client.id = :clientId and o.active = true group by e order by e.box.locker.plant.plantNumber, e.box.locker.lockerNumber")
    List<EmployeeWithActiveOrders> getWithActiveOrders(long clientId);

    @Query("select new pl.bratosz.smartlockers.service.employees.EmployeeWithActiveOrders(" +
            "e.id, e.department.name, e.box.locker.plant.plantNumber, e.box.locker.lockerNumber, e.box.boxNumber," +
            "e.lastName, e.firstName, count(o))  " +
            "from Employee e join e.mainOrders o on e.id = o.employee.id " +
            "where e.department.client.id = :clientId and o.active = true and o.orderType = :orderType " +
            "group by e order by e.box.locker.plant.plantNumber, e.box.locker.lockerNumber")
    List<EmployeeWithActiveOrders> getWithActiveOrdersByOrderType(long clientId, OrderType orderType);
}
