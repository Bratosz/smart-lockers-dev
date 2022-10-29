package pl.bratosz.smartlockers.service.clothes.rotational;

import pl.bratosz.smartlockers.model.Employee;
import pl.bratosz.smartlockers.model.clothes.Cloth;
import pl.bratosz.smartlockers.response.ResponseClothAssignment;
import pl.bratosz.smartlockers.response.StandardResponse;
import pl.bratosz.smartlockers.service.RotationalClothService;

import java.time.LocalDate;
import java.util.List;

public class RotationalClothesManager {
    private RotationalClothService service;


    public static RotationalClothesManager create(RotationalClothService service) {
        return new RotationalClothesManager(service);
    }

    public ResponseClothAssignment releaseAsRotational(Cloth cloth, Employee employee) {
        if (!cloth.isRotational()) {
            return ResponseClothAssignment.createForFailure("To nie jest odzież rotacyjna nicponiu");
        } else if (!clothCanBeReleasedAsRotational(cloth)) {
            return ResponseClothAssignment.createForFailure(
                    "Ta odzież została już wydana");
        } else {
            cloth.setReleasedToEmployee(true);
            cloth.setReleasedToEmployeeAsRotation(LocalDate.now());
            cloth.setRotationTemporaryOwner(employee);
            service.getClothesRepository().save(cloth);
            return ResponseClothAssignment.createForSucceed();
        }
    }

    public void updateReleased(List<Cloth> clothes) {
        clothes.stream()
                .filter(cloth -> isReturned(cloth))
                .forEach(cloth -> updateReturned(cloth));
    }

    public StandardResponse returnClothes(List<Cloth> clothes) {
        clothes.stream()
                .forEach(cloth -> updateReturned(cloth));
        return StandardResponse.createForSucceed();
    }

    private void updateReturned(Cloth c) {
        c.setReleasedToEmployee(false);
        c.setRotationTemporaryOwner(null);
        service.getClothesRepository().save(c);
    }

    private boolean isReturned(Cloth c) {
        return c.getReleasedToEmployeeAsRotation().isBefore(c.getLastWashing());
    }

    private boolean clothCanBeReleasedAsRotational(Cloth cloth) {
        if (cloth.isRotational()) {
            return !cloth.isReleasedToEmployee();
        } else {
            return false;
        }
    }

    private RotationalClothesManager(RotationalClothService service) {
        this.service = service;
    }

}
