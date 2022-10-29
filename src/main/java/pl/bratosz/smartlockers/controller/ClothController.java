package pl.bratosz.smartlockers.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.bratosz.smartlockers.model.Views;
import pl.bratosz.smartlockers.model.clothes.ClothAcceptanceType;
import pl.bratosz.smartlockers.model.clothes.ClothSize;
import pl.bratosz.smartlockers.model.clothes.LengthModification;
import pl.bratosz.smartlockers.model.orders.OrderType;
import pl.bratosz.smartlockers.model.clothes.Cloth;
import pl.bratosz.smartlockers.response.ResponseClothAcceptance;
import pl.bratosz.smartlockers.response.ResponseClothAssignment;
import pl.bratosz.smartlockers.response.StandardResponse;
import pl.bratosz.smartlockers.service.ClothService;

import static pl.bratosz.smartlockers.model.Views.*;

@RestController
@RequestMapping("/cloth")
public class ClothController {

    private ClothService clothesService;

    @Autowired
    public ClothController(ClothService clothesService) {
        this.clothesService = clothesService;
    }

    @JsonView(InternalForClothes.class)
    @GetMapping("/{id}")
    public Cloth getClothById(@PathVariable long id){
        return clothesService.getById(id);
    }

    @JsonView(InternalForClothes.class)
    @PostMapping("/exchange/{orderType}/{barcode}/{size}/{lengthModification}/{articleNumber}/{userId}/{clientId}")
    public ResponseClothAcceptance exchangeWithLengthModification(
            @PathVariable OrderType orderType,
            @PathVariable long barcode,
            @PathVariable ClothSize size,
            @PathVariable LengthModification lengthModification,
            @PathVariable int articleNumber,
            @PathVariable long userId){
        return clothesService.exchange(
                orderType, barcode, size, lengthModification, articleNumber, userId);
    }

    @JsonView(InternalForClothes.class)
    @PostMapping("/auto-exchange/{barcode}/{userId}")
    public ResponseClothAcceptance autoExchange(@PathVariable long barcode, @PathVariable long userId) {
        return clothesService.autoExchange(barcode, userId);
    }

    @JsonView(InternalForClothes.class)
    @PostMapping("/exchange/{orderType}/{barcode}/{size}/{articleNumber}/{userId}")
    public ResponseClothAcceptance exchange(
            @PathVariable OrderType orderType,
            @PathVariable long barcode,
            @PathVariable ClothSize size,
            @PathVariable int articleNumber,
            @PathVariable long userId){
        LengthModification modification = LengthModification.NONE;
        return exchangeWithLengthModification(
                 orderType, barcode, size, modification, articleNumber, userId);
    }


    @JsonView(InternalForClothes.class)
    @PostMapping("/accept/{acceptanceType}/{barcode}/{userId}")
    public ResponseClothAcceptance accept(
            @PathVariable ClothAcceptanceType acceptanceType,
            @PathVariable long barcode,
            @PathVariable long userId) {
        return clothesService.accept(acceptanceType, barcode, userId);
    }


    @PostMapping("/assign-withdrawn-cloth/{userId}/{employeeId}/{articleNumber}/{size}")
    public ResponseClothAssignment assignWithdrawnCloth(
            @PathVariable long userId,
            @PathVariable long employeeId,
            @PathVariable int articleNumber,
            @PathVariable ClothSize size,
            @RequestBody Cloth withdrawnCloth) {
        return clothesService.assignWithdrawnCloth(
                userId, employeeId, articleNumber, size, withdrawnCloth);
    }



    
}
