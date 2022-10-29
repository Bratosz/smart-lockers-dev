package pl.bratosz.smartlockers.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.web.bind.annotation.*;
import pl.bratosz.smartlockers.model.Location;
import pl.bratosz.smartlockers.model.Views;
import pl.bratosz.smartlockers.response.CreateResponse;
import pl.bratosz.smartlockers.service.LocationService;
import pl.bratosz.smartlockers.utils.string.MyString;

import java.util.List;

@RestController
@RequestMapping("/location")
public class LocationController {
    private LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @JsonView(Views.Public.class)
    @GetMapping("/get-all/{userId}")
    public List<Location> getAll(@PathVariable long userId) {
        return locationService.getAll(userId);
    }

    public Location create(@PathVariable long clientId,
                           @PathVariable String locationName) {
        locationName = MyString.create(locationName).get();
        return locationService.create(clientId, locationName);
    }

    @JsonView(Views.BasicLocationInfo.class)
    @PostMapping("/create/{locationName}/{plantId}")
    public CreateResponse create(
            @PathVariable String locationName,
            @PathVariable long plantId) {
        return locationService.create(locationName, plantId);
    }


    public Location create(long clientId,
                           String locationName,
                           boolean surrogate) {
        locationName = MyString.create(locationName).get();
        return locationService.create(clientId, locationName, surrogate);
    }

    @PostMapping("/assign_to_plant/{locationId}/{plantNumber}")
    public Location assignToPlant(@PathVariable long locationId,
                                  @PathVariable int plantNumber) {
        return locationService.assignToPlant(locationId, plantNumber);
    }
}
