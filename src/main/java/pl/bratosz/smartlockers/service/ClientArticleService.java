package pl.bratosz.smartlockers.service;

import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.exception.MyException;
import pl.bratosz.smartlockers.model.Badge;
import pl.bratosz.smartlockers.model.Client;
import pl.bratosz.smartlockers.model.ClientArticle;
import pl.bratosz.smartlockers.model.clothes.Article;
import pl.bratosz.smartlockers.repository.ClientArticlesRepository;
import pl.bratosz.smartlockers.repository.ClientRepository;
import pl.bratosz.smartlockers.repository.UsersRepository;
import pl.bratosz.smartlockers.response.StandardResponse;
import pl.bratosz.smartlockers.service.exels.plant.template.data.TemplateArticle;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ClientArticleService {
    private ClientArticlesRepository clientArticlesRepository;
    private ArticleService articleService;
    private UsersRepository usersRepository;
    private BadgeService badgeService;
    private ClientRepository clientRepository;

    public ClientArticleService(ClientArticlesRepository clientArticlesRepository, ArticleService articleService, UsersRepository usersRepository, BadgeService badgeService, ClientRepository clientRepository) {
        this.clientArticlesRepository = clientArticlesRepository;
        this.articleService = articleService;
        this.usersRepository = usersRepository;
        this.badgeService = badgeService;
        this.clientRepository = clientRepository;
    }

    public StandardResponse add(long articleId, long badgeNumber, long userId) {
        long clientId = usersRepository.getActualClientId(userId);
        Client client = clientRepository.getById(clientId);
        Article article = articleService.get(articleId);
        Badge badge = badgeService.getByNumber((int) badgeNumber);
        if(!clientArticleExist(article, clientId)) {
            create(article, badge, client);
            return StandardResponse.createForSucceed("Utworzono artykuł");
        } else {
            return StandardResponse.createForFailure("Artykuł o numerze: " + article.getNumber() +
                    " jest już przypisany do tego klienta.");
        }
    }

    public StandardResponse createArticleAndAddClientArticle(
            int articleNumber,
            String articleName,
            long badgeNumber,
            long userId) {
        try {
            Article article = articleService.addNewArticle(articleNumber, articleName);
            return add(article.getId(), badgeNumber, userId);
        } catch (MyException e) {
            return StandardResponse.createForFailure(e.getMessage());
        }
    }


    private boolean clientArticleExist(Article article, long clientId) {
        if(clientArticlesRepository.getBy(
                article.getNumber(), clientId) != null)
            return true;
        else
            return false;

    }

    public List<ClientArticle> get(long userId) {
        long clientId = usersRepository.getActualClientId(userId);
        return clientArticlesRepository.getAllBy(clientId);
    }

    public ClientArticle get(int articleNumber, long clientId) {
        return clientArticlesRepository.getBy(articleNumber, clientId);
    }

    public ClientArticle getArticle(int articleNumber, Client client) {
        return clientArticlesRepository.getBy(articleNumber, client.getId());
    }

    public ClientArticle getById(long clientArticleId) {
        return clientArticlesRepository.getBy(clientArticleId);
    }

    public ClientArticle updatePrice(float price, long clientArticleId) {
        ClientArticle article = clientArticlesRepository.getBy(clientArticleId);
        article.setRedemptionPrice(price);
        return clientArticlesRepository.save(article);
    }

    public List<ClientArticle> setDepreciationPeriod(
            int depreciationPeriod,
            long userId) {
        long clientId = usersRepository.getActualClientId(userId);
        List<ClientArticle> updatedArticles =
                clientArticlesRepository.getAllBy(clientId)
                        .stream()
                        .map(a -> setDepreciationPeriod(depreciationPeriod, a))
                        .collect(Collectors.toList());
        return clientArticlesRepository.saveAll(updatedArticles);
    }

    public List<ClientArticle> setDepreciationPercentageCap(
            int percentageCap,
            long userId) {
        long clientId = usersRepository.getActualClientId(userId);
        List<ClientArticle> updatedArticles = clientArticlesRepository.getAllBy(clientId)
                .stream()
                .map(a -> setDepreciationPercentageCap(percentageCap, a))
                .collect(Collectors.toList());
        return clientArticlesRepository.saveAll(updatedArticles);
    }

    private ClientArticle setDepreciationPercentageCap(
            int percentageCap,
            ClientArticle article) {
        article.setDepreciationPercentageCap(percentageCap);
        return article;
    }

    private ClientArticle setDepreciationPeriod(
            int depreciationPeriod,
            ClientArticle article) {
        article.setDepreciationPeriod(depreciationPeriod);
        return article;
    }


    public ClientArticle get(int articleNumber, Client client, String articleName) {
        return client.getArticles()
                .stream()
                .filter(clientArticle ->
                        clientArticle.getArticle().getNumber() == articleNumber)
                .findFirst()
                .orElseGet(() ->
                        addNewArticle(articleNumber, articleName, client));
    }

    public ClientArticle addNewArticle(
            int articleNumber, String articleName, Client client) {
        return addNewArticle(articleNumber, articleName, 0, client);
    }

    public ClientArticle addNewArticle(
            int articleNumber,
            String articleName,
            int badgeNumber,
            Client client) {
        Article article = articleService.getByArticleNumber(articleNumber);
        if(article == null) {
            try {
                article = articleService.addNewArticle(articleNumber, articleName);
            } catch (MyException e) {
                articleService.getExceptionalArticle(articleNumber, articleName);
            }
        }
        Badge badge = badgeService.getByNumber(badgeNumber);
        return create(article, badge, client);
    }

    private ClientArticle create(Article article, Badge badge, Client client) {
        ClientArticle clientArticle = new ClientArticle();
        clientArticle.setArticle(article);
        clientArticle.setAvailable(true);
        clientArticle.addClient(client);
        clientArticle.setBadge(badge);
        clientArticle.setRedemptionPrice(0);
        clientArticle.setDepreciationPeriod(42);
        clientArticle.setDepreciationPercentageCap(100);
        return clientArticlesRepository.save(clientArticle);
    }


    public void addNewArticles(List<TemplateArticle> articles, Client client) {
        articles.forEach(a -> addNewArticle(
                a.getArticleNumber(),
                a.getArticleName(),
                a.getBadgeNumber(),
                client));
    }

    public StandardResponse getBadges() {
        Set<Badge> badges = badgeService.getBadges();
        return StandardResponse.createForSucceed(badges);
    }
}
