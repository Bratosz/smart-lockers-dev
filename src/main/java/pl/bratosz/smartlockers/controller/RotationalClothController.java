package pl.bratosz.smartlockers.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.web.bind.annotation.*;
import pl.bratosz.smartlockers.model.Views;
import pl.bratosz.smartlockers.response.ResponseClothAssignment;
import pl.bratosz.smartlockers.response.StandardResponse;
import pl.bratosz.smartlockers.service.RotationalClothService;

@RestController
@RequestMapping("/rotational-cloth")
public class RotationalClothController {

    private RotationalClothService rotationalClothService;

    public RotationalClothController(RotationalClothService rotationalClothService) {
        this.rotationalClothService = rotationalClothService;
    }

    @PostMapping("/update-released/{clientId}")
    public StandardResponse updateReleasedClothes(
            @PathVariable long clientId) {
        return rotationalClothService.updateReleased(clientId);
    }

    @PostMapping("/release/{barcode}/{employeeId}/{userId}")
    public ResponseClothAssignment releaseRotationalCloth(
            @PathVariable long barcode,
            @PathVariable long employeeId,
            @PathVariable long userId) {
        return rotationalClothService.releaseRotationalCloth(
                barcode, employeeId, userId);
    }

    @JsonView(Views.Public.class)
    @PostMapping("/set-as-rotational/{lockerNumber}/{startingBoxNumber}" +
            "/{endBoxNumber}/{plantId}")
    public StandardResponse setClothesInBoxesAsRotational(
            @PathVariable int lockerNumber,
            @PathVariable int startingBoxNumber,
            @PathVariable int endBoxNumber,
            @PathVariable long plantId) {
        return rotationalClothService.setAsRotational(
                lockerNumber, startingBoxNumber, endBoxNumber, plantId);
    }

    @JsonView(Views.Public.class)
    @PostMapping("/return-rotational-clothes/{userId}")
    public StandardResponse returnRotationalClothes(
            @PathVariable long userId,
            @RequestBody long[] barcodes) {
        return rotationalClothService.returnRotationalClothes(userId, barcodes);
    }
}
