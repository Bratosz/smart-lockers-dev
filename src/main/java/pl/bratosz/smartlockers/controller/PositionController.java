package pl.bratosz.smartlockers.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.web.bind.annotation.*;
import pl.bratosz.smartlockers.model.Position;
import pl.bratosz.smartlockers.model.Views;
import pl.bratosz.smartlockers.response.StandardResponse;
import pl.bratosz.smartlockers.service.PositionService;

import java.util.Set;

@RestController
@RequestMapping("/position")
public class PositionController {
    private PositionService positionService;

    public PositionController(PositionService positionService) {
        this.positionService = positionService;
    }

    @PostMapping("/create/{positionName}/{userId}")
    @JsonView(Views.Public.class)
    public StandardResponse create(
            @PathVariable String positionName,
            @PathVariable long userId) {
        return positionService.create(positionName, userId);
    }

    @PostMapping("/add-department/{departmentId}/{positionId}")
    @JsonView(Views.Public.class)
    public StandardResponse addDepartment(
            @PathVariable long departmentId,
            @PathVariable long positionId) {
        return positionService.addDepartments(departmentId, positionId);
    }

    @PostMapping("/add-article-with-quantity/{clientArticleId}/{quantity}/{positionId}")
    @JsonView(Views.Public.class)
    public StandardResponse addArticleWithQuantity(
            @PathVariable long clientArticleId,
            @PathVariable int quantity,
            @PathVariable long positionId) {
        return positionService.addArticleWithQuantity(clientArticleId, quantity, positionId);
    }

    @PostMapping("/add-another-article/{clientArticleId}/{articleWithQuantityId}/{positionId}")
    @JsonView(Views.Public.class)
    public StandardResponse addAnotherArticle(
            @PathVariable long clientArticleId,
            @PathVariable long articleWithQuantityId,
            @PathVariable long positionId) {
        return positionService.addAnotherArticle(clientArticleId, articleWithQuantityId, positionId);
    }

    @GetMapping("/get-all/{userId}")
    @JsonView(Views.Public.class)
    public Set<Position> get(@PathVariable long userId) {
        return positionService.get(userId);
    }

    @GetMapping("/get-one/{positionId}")
    @JsonView(Views.Public.class)
    public Position getOne(
            @PathVariable long positionId) {
        return positionService.getById(positionId);
    }


    @GetMapping("/get-all-by-department/{departmentId}")
    @JsonView(Views.Public.class)
    public Set<Position> getAllByDepartment(
            @PathVariable long departmentId) {
        return positionService.getAllByDepartment(departmentId);
    }


    @DeleteMapping("/delete/{positionId}")
    @JsonView(Views.Public.class)
    public StandardResponse delete(
            @PathVariable long positionId) {
        return positionService.delete(positionId);
    }

}
