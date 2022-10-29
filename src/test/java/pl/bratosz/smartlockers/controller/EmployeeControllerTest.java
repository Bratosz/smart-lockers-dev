package pl.bratosz.smartlockers.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import pl.bratosz.smartlockers.model.Department;
import pl.bratosz.smartlockers.model.Employee;
import pl.bratosz.smartlockers.model.Locker;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EmployeeControllerTest {


    private Employee employee;
    private Integer boxNumber = 1;
    private Integer lockerNumber = 2;


    @Autowired
    EmployeeController employeeController;

    @BeforeEach
    void init() {

    }


}