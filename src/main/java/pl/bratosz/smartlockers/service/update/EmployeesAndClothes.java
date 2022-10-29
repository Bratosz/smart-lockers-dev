package pl.bratosz.smartlockers.service.update;

import pl.bratosz.smartlockers.model.SimpleEmployee;

import java.util.List;

public class EmployeesAndClothes {
    private final List<SimpleEmployee> employees;
    private final List<SimpleCloth> clothes;

    public EmployeesAndClothes(List<SimpleEmployee> employees, List<SimpleCloth> clothes) {
        this.employees = employees;
        this.clothes = clothes;
    }

    public List<SimpleEmployee> getEmployees() {
        return employees;
    }

    public List<SimpleCloth> getClothes() {
        return clothes;
    }
}
