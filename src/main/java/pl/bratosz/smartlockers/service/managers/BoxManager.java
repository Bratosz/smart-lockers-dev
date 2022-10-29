package pl.bratosz.smartlockers.service.managers;

import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.model.Box;
import pl.bratosz.smartlockers.model.Employee;
import pl.bratosz.smartlockers.model.EmployeeGeneral;
import pl.bratosz.smartlockers.model.users.User;
import pl.bratosz.smartlockers.repository.BoxesRepository;

import java.util.Date;

import static pl.bratosz.smartlockers.model.Box.BoxStatus.*;

//Manager should be created before each operation

@Service
public class BoxManager {
    private BoxesRepository boxesRepository;

    public BoxManager(BoxesRepository boxesRepository) {
        this.boxesRepository = boxesRepository;
    }

    public Box release(Box box) {
        if(box.getBoxStatus().equals(OCCUPY)) {
            box.setEmployee(setDummy(box));
            box.setBoxStatus(FREE);
            return boxesRepository.save(box);
        } else {
            return box;
        }
    }

    public EmployeeGeneral extractEmployee(Box box) {
        EmployeeGeneral employee = box.getEmployee();
        if(employeeIsPresent(employee)) {
            employee.setBox(null);
            return employee;
        } else {
            return employee;
        }
    }

    private EmployeeGeneral setDummy(Box box) {
        return box.getEmployeeDummy();
    }



    private boolean employeeIsPresent(EmployeeGeneral employee) {
        if(employee.getClass().isInstance(Employee.class)) {
            return true;
        } else {
            return false;
        }
    }
}
