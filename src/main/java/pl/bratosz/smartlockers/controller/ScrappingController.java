package pl.bratosz.smartlockers.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.bratosz.smartlockers.service.update.ScrapingService;

@RestController
@RequestMapping("/scrap")
public class ScrappingController {
    private ScrapingService scrapingService;

    @Autowired
    public ScrappingController(ScrapingService scrapingService) {
        this.scrapingService = scrapingService;
    }

//    @GetMapping("/load-employee/{boxId}")
//    @JsonView(Views.InternalForBoxes.class)
//    public Box loadEmployee(@PathVariable long boxId) {
//       return scrapingService.loadEmployee(boxId);
//    }

//    @PostMapping("/update-clothes")
//    public StandardResponse updateClothes(
//            @RequestBody Plant plant) {
//        return scrapingService.updateClothes(plant);
//        //refactor in to by plantId
//    }

}
