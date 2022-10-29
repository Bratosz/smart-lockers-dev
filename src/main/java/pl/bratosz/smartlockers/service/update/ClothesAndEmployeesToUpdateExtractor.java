package pl.bratosz.smartlockers.service.update;

import pl.bratosz.smartlockers.model.SimpleEmployee;
import pl.bratosz.smartlockers.model.clothes.Cloth;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class ClothesAndEmployeesToUpdateExtractor {
    private List<SimpleEmployee> actualEmployees;
    private final List<SimpleCloth> actualClothes;
    private final List<Cloth> previousClothes;
    private final List<SimpleCloth> previousSimpleClothes;
    private final LocalDate lastPlantUpdate;
    private Set<SimpleCloth> clothesToUpdate;
    private Set<SimpleEmployee> employeesToUpdate;


    public ClothesAndEmployeesToUpdateExtractor(EmployeesAndClothes actualEmployeesAndClothes, List<Cloth> previousClothes, LocalDate lastPlantUpdate) {
        this.actualEmployees = actualEmployeesAndClothes.getEmployees();
        this.actualClothes = getSortedSimpleClothes(actualEmployeesAndClothes.getClothes());
        this.previousClothes = previousClothes;
        this.previousSimpleClothes = getSimpleClothesFromClothes(previousClothes);
        this.lastPlantUpdate = lastPlantUpdate;
        clothesToUpdate = new HashSet<>();
        employeesToUpdate = new HashSet<>();
        sort();
    }

    public ClothesAndEmployeesToUpdate getClothesAndEmployeesToUpdate() {
        return new ClothesAndEmployeesToUpdate(
                getWashedUpdatedClothes(),
                getEmployeesToUpdate());
    }

    private Set<Cloth> getWashedUpdatedClothes() {
        Set<Cloth> washedClothes = new HashSet<>();
        for(SimpleCloth c : clothesToUpdate) {
            Cloth cloth = previousClothes.stream()
                    .filter(pc -> pc.getBarcode() == c.getBarcode())
                    .findFirst().get();
            cloth.setLastWashing(c.getLastWashing());
            washedClothes.add(cloth);
        }
        return washedClothes;
    }

    private Set<SimpleEmployee> getEmployeesToUpdate() {
        return employeesToUpdate;
    }

    private void sort() {
        List<SimpleCloth> clothesToUpdate = new ArrayList<>();
        for (SimpleEmployee e : actualEmployees) {
            clothesToUpdate.clear();
            clothesToUpdate = getClothesToUpdate(e.getClothes());
            if(employeeIsToUpdate(clothesToUpdate)) {
                employeesToUpdate.add(e);
            } else {
                this.clothesToUpdate.addAll(clothesToUpdate);
            }
        }
        for(SimpleCloth previousCloth : previousSimpleClothes) {
            if(Collections.binarySearch(actualClothes, previousCloth) < 0) {
                employeesToUpdate.add(previousCloth.getSimpleEmployee());
            }
        }
    }
    //IT will mark employees with clothes that are not released yet, because previou
    private boolean employeeIsToUpdate(List<SimpleCloth> clothesToUpdate) {
        for(SimpleCloth employeeCloth : clothesToUpdate) {
            if(Collections.binarySearch(previousSimpleClothes, employeeCloth) < 0) {
                return true;
            }
        }
        return false;
    }

    private List<SimpleCloth> getClothesToUpdate(Set<SimpleCloth> clothes) {
        return clothes.stream()
                .filter(c -> c.getLastWashing().isAfter(lastPlantUpdate))
                .collect(Collectors.toList());
    }

    private List<SimpleCloth> getSortedSimpleClothes(List<SimpleCloth> actualClothes) {
        Collections.sort(actualClothes);
        return actualClothes;
    }

    private List<SimpleCloth> getSimpleClothesFromClothes(List<Cloth> previousClothes) {
        List<SimpleCloth> clothes = new LinkedList<>();
        for(Cloth c : previousClothes) {
            clothes.add(new SimpleCloth(c));
        }
        return clothes;
    }

}
