package pl.bratosz.smartlockers.service;

        import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.model.Box;
import pl.bratosz.smartlockers.model.Employee;
import pl.bratosz.smartlockers.model.clothes.Cloth;
import pl.bratosz.smartlockers.model.users.User;
import pl.bratosz.smartlockers.repository.BoxesRepository;
import pl.bratosz.smartlockers.repository.ClothesRepository;
import pl.bratosz.smartlockers.repository.EmployeesRepository;
import pl.bratosz.smartlockers.response.ResponseClothAssignment;
import pl.bratosz.smartlockers.response.StandardResponse;
import pl.bratosz.smartlockers.service.clothes.rotational.RotationalClothesManager;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Service
public class RotationalClothService {

    private ClothesRepository clothesRepository;
    private UserService userService;
    private BoxesRepository boxesRepository;
    private EmployeesRepository employeesRepository;
    private User user;

    public RotationalClothService(ClothesRepository clothesRepository, UserService userService, BoxesRepository boxesRepository, EmployeesRepository employeesRepository) {
        this.clothesRepository = clothesRepository;
        this.userService = userService;
        this.boxesRepository = boxesRepository;
        this.employeesRepository = employeesRepository;
    }

    public StandardResponse updateReleased(long plantId) {
        List<Cloth> clothes = clothesRepository
                .getReleasedRotationalClothes(plantId);
        RotationalClothesManager manager = RotationalClothesManager.create(this);
        manager.updateReleased(clothes);
        return StandardResponse.createForSucceed();
    }

    public StandardResponse setAsRotational(
            int lockerNumber,
            int startingBoxNumber,
            int endBoxNumber,
            long plantId) {
        getBoxesFromRange(lockerNumber, startingBoxNumber, endBoxNumber, plantId)
                .stream()
                .map(b -> b.getEmployee())
                .forEach(e -> setClothesAsRotational(e.getId()));
        return StandardResponse.createForSucceed("Ustawiono odzież jako rotacyjną");
    }



    public void setClothesAsRotational(Employee employee) {
        clothesRepository.setRotationalByEmployee(employee);
    }

    public void setClothesAsRotational(long employeeId) {
        clothesRepository.setRotationalByEmployeeId(employeeId);
    }


    public ResponseClothAssignment releaseRotationalCloth(
            long barcode, long employeeId, long userId) {
        loadUser(userId);
        long clientId = user.getActualClientId();
        Employee employee = employeesRepository.getEmployeeById(employeeId);
        Cloth cloth = clothesRepository.getByBarcode(barcode);
        RotationalClothesManager rotationManager = RotationalClothesManager
                .create(this);
        if (clothIsPresent(cloth, clientId)) {
            return rotationManager.releaseAsRotational(cloth, employee);
        } else {
            return ResponseClothAssignment.createForFailure(
                    "Brak ubrania o podanym kodzie kreskowym");
        }
    }

    public StandardResponse returnRotationalClothes(
            long userId,
            long[] barcodes) {
        loadUser(userId);
        List<Cloth> clothes = new LinkedList<>();
        Arrays.stream(barcodes).forEach(b -> clothes.add(
                clothesRepository.getByBarcode(b)));
        RotationalClothesManager rotationManager = RotationalClothesManager
                .create(this);
        return rotationManager.returnClothes(clothes);
    }

    public void setAsRotational(Employee e) {
        e.getClothes().stream()
                .forEach(c -> c.setRotational(true));
    }



    private Set<Box> getBoxesFromRange(
            int lockerNumber,
            int startingBoxNumber,
            int endBoxNumber,
            long plantId) {
        return boxesRepository.getBoxesFromRangeByPlantId(
                lockerNumber, startingBoxNumber, endBoxNumber, plantId);
    }

    private boolean clothIsPresent(Cloth cloth, long clientId) {
        return !clothIsAbsent(clientId, cloth);
    }

    private boolean clothIsAbsent(long clientId, Cloth cloth) {
        return cloth == null ||
                clothBelongsToOtherClient(cloth, clientId);
    }

    private boolean clothBelongsToOtherClient(Cloth cloth, long clientId) {
        if (cloth != null && clientId != cloth.getClientId()) {
            return true;
        } else {
            return false;
        }
    }

    private void loadUser(long userId) {
        this.user = userService.getUserById(userId);
    }

    public ClothesRepository getClothesRepository() {
        return clothesRepository;
    }

    public UserService getUserService() {
        return userService;
    }

    public BoxesRepository getBoxesRepository() {
        return boxesRepository;
    }

    public EmployeesRepository getEmployeesRepository() {
        return employeesRepository;
    }

    public User getUser() {
        return user;
    }

}
