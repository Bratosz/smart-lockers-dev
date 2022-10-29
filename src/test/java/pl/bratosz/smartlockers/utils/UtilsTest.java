package pl.bratosz.smartlockers.utils;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pl.bratosz.smartlockers.model.Client;
import pl.bratosz.smartlockers.model.Department;
import pl.bratosz.smartlockers.model.Location;
import pl.bratosz.smartlockers.service.pasting.employee.EmployeeToCreate;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;
import static pl.bratosz.smartlockers.model.clothes.LengthModification.*;
import static pl.bratosz.smartlockers.model.clothes.StringsForClothExtractor.*;

class UtilsTest {

    @Test
    void shouldReturnTrueWhenClientIsTheSame() {
        //given
        Client c1 = new Client();
        c1.setId(1);

        EmployeeToCreate e1 = new EmployeeToCreate();
        EmployeeToCreate e2 = new EmployeeToCreate();
        e1.setClient(c1);
        e2.setClient(c1);

        List<EmployeeToCreate> emps = new ArrayList<>();
        emps.add(e1);
        emps.add(e2);

        Department dep = new Department();
        dep.setClient(c1);
        Location loc = new Location();
        loc.setClient(c1);
        //when
        boolean result = Utils.haveSameClient(emps, dep, loc);
        //then
        assertThat(result, is(equalTo(true)));
    }

    @Test
    void shouldReturnValueWhenArgsIsEmpty() {
        //given
        Client c1 = new Client();
        c1.setId(1);

        EmployeeToCreate e1 = new EmployeeToCreate();
        EmployeeToCreate e2 = new EmployeeToCreate();
        e1.setClient(c1);
        e2.setClient(c1);

        List<EmployeeToCreate> employeesWithTheSameClient = new ArrayList<>();
        employeesWithTheSameClient.add(e1);
        employeesWithTheSameClient.add(e2);
        //when
        boolean result = Utils.haveSameClient(employeesWithTheSameClient);
        //then
        assertThat(result, is(equalTo(true)));

    }

    @Test
    void shouldReturnFalseWhenClientIsDifferent() {
        //given
        Client c1 = new Client();
        Client c2 = new Client();
        c1.setId(1);
        c2.setId(2);

        EmployeeToCreate e1 = new EmployeeToCreate();
        EmployeeToCreate e2 = new EmployeeToCreate();
        EmployeeToCreate e3 = new EmployeeToCreate();
        e1.setClient(c1);
        e2.setClient(c1);
        e3.setClient(c2);

        List<EmployeeToCreate> employeesWithSameClient = new ArrayList<>();
        List<EmployeeToCreate> employeesWithDifferentClients = new ArrayList<>();
        employeesWithSameClient.add(e1);
        employeesWithSameClient.add(e2);
        employeesWithDifferentClients.add(e1);
        employeesWithDifferentClients.add(e3);

        Department departmentWithClient1 = new Department();
        Department departmentWithClient2 = new Department();
        departmentWithClient1.setClient(c1);
        departmentWithClient2.setClient(c2);

        Location locationWithClient1 = new Location();
        locationWithClient1.setClient(c1);
        //when
        boolean resultForDifferentDepartmentClients = Utils.haveSameClient(
                employeesWithSameClient, departmentWithClient1, departmentWithClient2);
        boolean resultForDifferentEmployeeClients = Utils.haveSameClient(
                employeesWithDifferentClients, departmentWithClient1, locationWithClient1);
        //then
        assertThat(resultForDifferentDepartmentClients, is(equalTo(false)));
        assertThat(resultForDifferentEmployeeClients, is(equalTo(false)));
    }
}