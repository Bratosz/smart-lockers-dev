package pl.bratosz.smartlockers.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.web.bind.annotation.*;
import pl.bratosz.smartlockers.model.ClientArticle;
import pl.bratosz.smartlockers.model.Views;
import pl.bratosz.smartlockers.response.StandardResponse;
import pl.bratosz.smartlockers.service.ClientArticleService;

import java.util.List;

@RestController
@RequestMapping("/client-article")
public class ClientArticleController {

    private ClientArticleService clientArticleService;

    public ClientArticleController(ClientArticleService clientArticleService) {
        this.clientArticleService = clientArticleService;
    }

    @PostMapping("/add/{articleId}/{badgeNumber}/{userId}")
    @JsonView(Views.Public.class)
    public StandardResponse add(
            @PathVariable long articleId,
            @PathVariable long badgeNumber,
            @PathVariable long userId) {
        return clientArticleService.add(articleId, badgeNumber, userId);
    }

    @PostMapping("/create-article-and-add-client-article" +
            "/{articleNumber}/{articleName}/{badgeNumber}/{userId}")
    public StandardResponse createArticleAndAddClientArticle(
            @PathVariable int articleNumber,
            @PathVariable String articleName,
            @PathVariable long badgeNumber,
            @PathVariable long userId) {
        return clientArticleService.createArticleAndAddClientArticle(
                articleNumber,
                articleName,
                badgeNumber,
                userId);
    }

    @GetMapping("/get-all/{userId}")
    @JsonView(Views.Public.class)
    public List<ClientArticle> get(
            @PathVariable long userId) {
        return clientArticleService.get(userId);
    }

    @GetMapping("/get-badges")
    public StandardResponse getBadges() {
        return clientArticleService.getBadges();
    }

    @PutMapping("/update-price/{price}/{clientArticleId}")
    @JsonView(Views.Public.class)
    public ClientArticle updatePrice(
            @PathVariable float price,
            @PathVariable long clientArticleId) {
        return clientArticleService.updatePrice(price, clientArticleId);
    }

    @PostMapping("/set-depreciation-period/for-all/{periodInMonths}/{userId}")
    @JsonView(Views.Public.class)
    public List<ClientArticle> setDepreciationPeriod(
            @PathVariable int periodInMonths,
            @PathVariable long userId) {
        return clientArticleService.setDepreciationPeriod(
                periodInMonths,
                userId);
    }

    @PutMapping("/set-percentage-cap/for-all/{percentageCap}/{userId}")
    @JsonView(Views.Public.class)
    public List<ClientArticle> setDepreciationPercentageCap(
            @PathVariable int percentageCap,
            @PathVariable long userId) {
        return clientArticleService.setDepreciationPercentageCap(
                percentageCap,
                userId);

    }
}
