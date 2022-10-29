package pl.bratosz.smartlockers.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.bratosz.smartlockers.model.Views;
import pl.bratosz.smartlockers.response.ClothesLoadedResponse;
import pl.bratosz.smartlockers.response.StandardResponse;
import pl.bratosz.smartlockers.service.*;

@RestController
@RequestMapping("/load")
public class PlantLoadingController {
    private PlantLoadingService plantLoadingService;

    public PlantLoadingController(PlantLoadingService plantLoadingService) {
        this.plantLoadingService = plantLoadingService;
    }

    @PostMapping("/update-clothes/{plantId}")
    @JsonView(Views.Public.class)
    public StandardResponse updateClothes(
            @PathVariable long plantId) {
        return plantLoadingService.updateClothes(plantId);
    }

    @PostMapping("/plant-without-clothes/{plantId}")
    @JsonView(Views.Public.class)
    public StandardResponse loadLockersWithBoxesAndEmployees(
            @PathVariable long plantId) {
        return plantLoadingService.loadLockersWithBoxesAndEmployees(plantId);
    }

    @PostMapping("/update-rotational-clothes/{plantId}")
    public StandardResponse updateRotationalClothes(
            @PathVariable long plantId) {
        plantLoadingService.updateRotationalClothes(plantId);
        return null;
    }



    @PostMapping("/plant-box-by-box-from-lockers-range/{from}/{to}/{plantId}")
    public StandardResponse loadLockersWithBoxesAndEmployeesFromLockersRange(
            @PathVariable int from,
            @PathVariable int to,
            @PathVariable long plantId) {
        return plantLoadingService.loadLockersWithBoxesAndEmployeesFromLockersRange(
                from,
                to,
                plantId);
    }

    @PostMapping("/all-clothes/{plantId}")
    public ClothesLoadedResponse loadAllClothes(@PathVariable long plantId) {
        return plantLoadingService.loadAllClothes(plantId);
    }

    @PostMapping("/clothes/{from}/{to}/{plantId}")
    public ClothesLoadedResponse loadClothesFromLockersRange(
            @PathVariable int from,
            @PathVariable int to,
            @PathVariable long plantId) {
        return plantLoadingService.loadClothesFromLockersRange(
                from, to, plantId);
    }

    /**
     *WARNING! This method will delete clothes that already exist in indicated boxes
     */
    @JsonView(Views.Public.class)
    @PostMapping("/rotational-clothes" +
            "/{lockerNumber}" +
            "/{startingBoxNumber}" +
            "/{endBoxNumber}" +
            "/{plantId}" +
            "/{userId}")
    public ClothesLoadedResponse setAsRotational(
            @PathVariable int lockerNumber,
            @PathVariable int startingBoxNumber,
            @PathVariable int endBoxNumber,
            @PathVariable long plantId,
            @PathVariable long userId) {
        return plantLoadingService.loadRotationalClothes(
                lockerNumber, startingBoxNumber, endBoxNumber, plantId, userId);
    }

    @PostMapping("/test")
    public void test() {
        plantLoadingService.test();
    }


}
