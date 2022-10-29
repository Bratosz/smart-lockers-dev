package pl.bratosz.smartlockers.service;

import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.exception.ArticleNotExistException;
import pl.bratosz.smartlockers.exception.MyException;
import pl.bratosz.smartlockers.model.clothes.Article;
import pl.bratosz.smartlockers.model.clothes.ClothType;
import pl.bratosz.smartlockers.repository.ArticlesRepository;
import pl.bratosz.smartlockers.resolvers.ClothTypeResolver;
import pl.bratosz.smartlockers.service.creators.ArticleCreator;

import java.util.List;

@Service
public class ArticleService {
    private ArticlesRepository articlesRepository;

    public ArticleService(ArticlesRepository articlesRepository) {
        this.articlesRepository = articlesRepository;
    }

    public Article getByArticleNumber(int articleNumber) {
        return articlesRepository.getBy(articleNumber);
    }

    public Article get(long articleId) {
        return articlesRepository.getById(articleId);
    }

    public List<Article> getAll() {
        return articlesRepository.getAll();
    }


    public Article addNewArticle(int articleNumber, String articleName) throws MyException {
            ClothTypeResolver clothTypeResolver = new ClothTypeResolver();
            ClothType clothType = clothTypeResolver.resolve(articleName);
            ArticleCreator creator = ArticleCreator.create(this);
            Article article = creator.create(articleNumber, articleName, clothType);
            return articlesRepository.save(article);
    }

    public Article determineDesiredArticle(int articleNumber, Article article) throws ArticleNotExistException {
        if(articleNumber == 0) {
            return article;
        } else{
             Article a = getByArticleNumber(articleNumber);
             if(a.equals(null)) {
                 throw new ArticleNotExistException("Article number: " + articleNumber);
             } else {
                 return a;
             }
        }
    }

    public Article getExceptionalArticle(int articleNumber, String articleName) {
        ClothType clothType = ClothType.EXCEPTIONAL;
        ArticleCreator creator = ArticleCreator.create(this);
        Article article = creator.createExceptional(articleNumber, articleName, clothType);
        return articlesRepository.save(article);
    }
}
