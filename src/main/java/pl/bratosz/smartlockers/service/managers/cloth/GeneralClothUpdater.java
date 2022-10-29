package pl.bratosz.smartlockers.service.managers.cloth;

import pl.bratosz.smartlockers.model.Employee;
import pl.bratosz.smartlockers.model.Position;
import pl.bratosz.smartlockers.model.clothes.Cloth;
import pl.bratosz.smartlockers.service.ClothService;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;


public class GeneralClothUpdater {
    private List<Cloth> priorClothes;
    private List<Cloth> actualClothes;
    private List<Cloth> notAssignedClothes;
    private List<Cloth> beforeReleaseClothes;
    private List<Cloth> withdrewClothes;
    private List<Cloth> recentlyAddedClothes;
    private SingleClothUpdater singleClothUpdater;
    private Employee employee;
    private boolean isRotational;
    private ClothService clothService;
    private static LocalDate INITIAL_DATE = LocalDate.of(1970, 1, 1);

    public GeneralClothUpdater(
            List<Cloth> priorClothes,
            List<Cloth> actualClothes,
            Employee employee,
            ClothService clothService) {
        this.singleClothUpdater = new SingleClothUpdater(clothService);
        this.employee = employee;
        this.isRotational = employeeIsRotational(employee);
        this.clothService = clothService;
        this.priorClothes = priorClothes;
        this.actualClothes = actualClothes;
        //by barcode == 0
        this.notAssignedClothes = popNotAssignedClothesFrom(priorClothes);
        //by compare barcode
        this.withdrewClothes = popWithdrewClothesFrom(priorClothes);
        this.recentlyAddedClothes = popRecentlyAddedClothesFrom(actualClothes);
    }

    public void update() {
        updateAssigned();
        updateRecentlyAddedClothes();
        updateWithdrawnClothes();
    }

    private void updateAssigned() {
        beforeReleaseClothes = popBeforeReleaseClothes();
        updateBeforeRelease();
        updateWashed();
    }

    private void updateWashed() {
        priorClothes.forEach(c -> c.setLastWashing(actualClothes.stream()
                .filter(ac -> ac.equals(c))
                .findFirst().get().getLastWashing()));
        if(isRotational) {
            priorClothes.forEach(c -> {

            });
        }
        clothService.getClothesRepository().saveAll(priorClothes);

    }

    private void updateBeforeRelease() {
        beforeReleaseClothes.forEach(c -> {
            if(clothIsRecentlyReleased(c)) {
                Cloth recentlyReleasedCloth = popByEquals(c, actualClothes);
                singleClothUpdater.updateReleasedCloth(c, recentlyReleasedCloth);
            }
        });
    }

    private void updateRecentlyAddedClothes() {
        updateNotAssigned();
        recentlyAddedClothes.forEach(c -> {
            c.setEmployee(employee);
            c.setRotational(isRotational);
            clothService.getClothesRepository().save(c);
            singleClothUpdater.updateAddedCloth(c);
        });
    }

    private void updateNotAssigned() {
        notAssignedClothes.forEach(c -> {
            if (clothIsRecentlyAdded(c)) {
                Cloth recentlyAddedCloth = popByCompare(c, recentlyAddedClothes);
                singleClothUpdater.updateAssignedCloth(c, recentlyAddedCloth);
            }
        });
    }


    private void updateWithdrawnClothes() {
        withdrewClothes.forEach(c -> {
            singleClothUpdater.updateWithdrawnCloth(c);
        });
    }


    private Cloth popByCompare(Cloth cloth, List<Cloth> from) {
        Cloth popped = from.stream()
                .filter(c -> c.compareTo(cloth) == 0)
                .findFirst()
                .get();
        from.remove(popped);
        return popped;
    }

    private Cloth popByEquals(Cloth cloth, List<Cloth> from) {
        Cloth popped = from.stream()
                .filter(c -> c.equals(cloth))
                .findFirst()
                .get();
        from.remove(popped);
        return popped;
    }

    private boolean clothIsRecentlyAdded(Cloth cloth) {
        return recentlyAddedClothes.stream()
                .anyMatch(c -> c.compareTo(cloth) == 0);
    }

    private boolean clothIsRecentlyReleased(Cloth bc) {
        return actualClothes.stream()
                .filter(ac -> ac.equals(bc))
                .anyMatch(ac -> ac.getReleaseDate().isAfter(INITIAL_DATE));

    }

    private List<Cloth> popBeforeReleaseClothes() {
        List<Cloth> beforeReleaseClothes = new LinkedList<>();
        List<Cloth> clothesToRemove = new LinkedList<>();
        priorClothes.forEach(c -> {
            if (c.getReleaseDate().equals(INITIAL_DATE)) {
                clothesToRemove.add(c);
                beforeReleaseClothes.add(c);
            }
        });
        priorClothes.removeAll(clothesToRemove);
        return beforeReleaseClothes;
    }

    private List<Cloth> popNotAssignedClothesFrom(List<Cloth> priorClothes) {
        List<Cloth> notAssignedClothes = new LinkedList<>();
        List<Cloth> clothesToRemove = new LinkedList<>();
        priorClothes.forEach(c -> {
            if (c.getBarcode() == 0) {
                clothesToRemove.add(c);
                notAssignedClothes.add(c);
            }
        });
        priorClothes.removeAll(clothesToRemove);
        return notAssignedClothes;
    }

    private List<Cloth> difference(List<Cloth> list1st, List<Cloth> list2nd) {
        List<Cloth> difference = new LinkedList<>(list1st);
        difference.removeAll(list2nd);
        list1st.removeAll(difference);
        return difference;
    }

    private List<Cloth> popWithdrewClothesFrom(List<Cloth> priorClothes) {
        return difference(this.priorClothes, actualClothes);
    }

    private List<Cloth> popRecentlyAddedClothesFrom(List<Cloth> actualClothes) {
        return difference(this.actualClothes, priorClothes);
    }

    private boolean employeeIsRotational(Employee employee) {
        Position position = employee.getPosition();
        if (position != null && position.getName().equals("ROTACJA"))
            return true;
        else
            return false;
    }

}


