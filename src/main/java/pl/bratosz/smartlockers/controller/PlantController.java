package pl.bratosz.smartlockers.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.web.bind.annotation.*;
import pl.bratosz.smartlockers.model.Plant;
import pl.bratosz.smartlockers.model.Views;
import pl.bratosz.smartlockers.response.CreateResponse;
import pl.bratosz.smartlockers.response.StandardResponse;
import pl.bratosz.smartlockers.service.PlantService;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/plant")
public class PlantController {

    private PlantService plantService;

    public PlantController(PlantService plantService) {
        this.plantService = plantService;
    }

    @JsonView(Views.PlantBasicInfo.class)
    @GetMapping("/get-all/{userId}")
    public List<Plant> getAll(@PathVariable long userId) {
        return plantService.getAll(userId);
    }

    @GetMapping("/get_by_number/{plantNumber}")
    public Plant getByNumber(@PathVariable int plantNumber) {
        return plantService.getByNumber(plantNumber);
    }

    @JsonView(Views.PlantBasicInfo.class)
    @PostMapping("/create/{clientId}")
    public CreateResponse create(
            @PathVariable long clientId,
            @RequestBody Plant plant) {
        return plantService.createWithResponse(plant, clientId);
    }

}
