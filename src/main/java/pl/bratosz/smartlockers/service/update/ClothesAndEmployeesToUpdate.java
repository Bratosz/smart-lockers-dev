package pl.bratosz.smartlockers.service.update;

import pl.bratosz.smartlockers.model.SimpleEmployee;
import pl.bratosz.smartlockers.model.clothes.Cloth;

import java.util.Set;

public class ClothesAndEmployeesToUpdate {
    private Set<Cloth> updatedClothes;
    private Set<SimpleEmployee> employeesToUpdate;

    public ClothesAndEmployeesToUpdate(Set<Cloth> updatedClothes, Set<SimpleEmployee> employeesToUpdate) {
        this.updatedClothes = updatedClothes;
        this.employeesToUpdate = employeesToUpdate;
    }

    public Set<Cloth> getUpdatedClothes() {
        return updatedClothes;
    }

    public Set<SimpleEmployee> getEmployeesToUpdate() {
        return employeesToUpdate;
    }
}
