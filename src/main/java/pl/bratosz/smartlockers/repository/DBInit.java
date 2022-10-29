//package pl.bratosz.smartlockers.repository;
//
//import com.fasterxml.jackson.annotation.JsonView;
//import com.sun.scenario.effect.impl.state.BoxRenderState;
//import org.springframework.stereotype.Component;
//import pl.bratosz.smartlockers.controller.EmployeeController;
//import pl.bratosz.smartlockers.controller.LockersController;
//import pl.bratosz.smartlockers.model.*;
//import pl.bratosz.smartlockers.service.EmployeeService;
//
//import javax.annotation.PostConstruct;
//import java.util.LinkedList;
//import java.util.List;
//
//
//@Component
//public class DBInit {
//
//
//    private BoxesRepository boxesRepository;
//    private LockersRepository lockersRepository;
//    private EmployeesRepository employeesRepository;
//
//    public DBInit(BoxesRepository boxesRepository, LockersRepository lockersRepository, EmployeesRepository employeesRepository) {
//        this.boxesRepository = boxesRepository;
//        this.lockersRepository = lockersRepository;
//        this.employeesRepository = employeesRepository;
//    }
//
//    @PostConstruct
//    public void init() {
//        for (int i = 1; i <= 3; i++) {
//            Locker locker = new Locker(i, 10, Department.METAL,
//                    Locker.DepartmentNumber.DEP_384,
//                    Locker.Location.OLDSIDE);
//            lockersRepository.save(locker);
//            List<Box> boxes = new LinkedList<>();
//
//            for (int j = 1; j <= 10; j++) {
//                Employee employee = employeesRepository.save(new Employee("Jan", "Nowak" + j, Department.METAL));
//                Employee dismissedEmp = employeesRepository.save(new Employee("", "", Department.METAL));
//                Box box = new Box(j, Box.BoxStatus.OCCUPY, dismissedEmp.getArticlesWithQuantityId());
//
//                List<Employee> dismissedEmployees = new LinkedList<>();
//                dismissedEmployees.addArticle(dismissedEmp);
//
//                box.setEmployee(employee);
//                box.setReleasedEmployees(dismissedEmployees);
//                boxes.addArticle(box);
//
//            }
//            locker.setBoxes(boxes);
//            lockersRepository.save(locker);
//        }
//    }
//}
//
//
