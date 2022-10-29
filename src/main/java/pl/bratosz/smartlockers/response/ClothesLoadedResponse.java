package pl.bratosz.smartlockers.response;

import com.fasterxml.jackson.annotation.JsonView;
import pl.bratosz.smartlockers.model.SimpleEmployee;
import pl.bratosz.smartlockers.model.Views;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class ClothesLoadedResponse {

    @JsonView(Views.Public.class)
    private int loadedEmployees;
    @JsonView(Views.Public.class)
    private SimpleEmployee lastEmployee;
    @JsonView(Views.Public.class)
    private List<SimpleEmployee> employeesWithoutClothes;
    @JsonView(Views.Public.class)
    private Set<SimpleEmployee> doubledBoxes;
    @JsonView(Views.Public.class)
    private List<SimpleEmployee> differentEmployees;

    public ClothesLoadedResponse(
            int loadedEmployees,
            SimpleEmployee lastEmployee,
            List<SimpleEmployee> employeesWithoutClothes,
            Set<SimpleEmployee> doubledBoxes,
            List<SimpleEmployee> differentEmployees) {
        this.loadedEmployees = loadedEmployees;
        this.lastEmployee = lastEmployee;
        this.employeesWithoutClothes = employeesWithoutClothes;
        this.doubledBoxes = doubledBoxes;
        this.differentEmployees = differentEmployees;
    }

    public ClothesLoadedResponse(
            List<SimpleEmployee> employeesWithoutClothes,
            Set<SimpleEmployee> doubledBoxes) {
        lastEmployee = new SimpleEmployee();
        this.employeesWithoutClothes = employeesWithoutClothes;
        this.doubledBoxes = doubledBoxes;
        this.differentEmployees = new LinkedList<>();
    }

    public int getLoadedEmployees() {
        return loadedEmployees;
    }

    public Set<SimpleEmployee> getDoubledBoxes() {
        return doubledBoxes;
    }

    public List<SimpleEmployee> getDifferentEmployees() {
        return differentEmployees;
    }

    public List<SimpleEmployee> getEmployeesWithoutClothes() {
        return employeesWithoutClothes;
    }

    public SimpleEmployee getLastEmployee() {
        return lastEmployee;
    }
}
