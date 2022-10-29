package pl.bratosz.smartlockers.service.creators;

import pl.bratosz.smartlockers.exception.MyException;
import pl.bratosz.smartlockers.model.clothes.Article;
import pl.bratosz.smartlockers.model.clothes.ClothType;
import pl.bratosz.smartlockers.service.ArticleService;
import pl.bratosz.smartlockers.utils.string.MyString;

public class ArticleCreator {
    private ArticleService articleService;

    private ArticleCreator(ArticleService articleService) {
        this.articleService = articleService;
    }

    public static ArticleCreator create(ArticleService articleService) {
        return new ArticleCreator(articleService);
    }


    public Article create(
            int articleNumber,
            String articleName,
            ClothType clothType) throws MyException {
        articleName = MyString.create(articleName).get();
        validateArticleName(articleName);
        validateArticleNumber(articleNumber);
        return new Article(articleNumber, articleName, clothType);
    }

    public Article createExceptional(
            int articleNumber,
            String articleName,
            ClothType clothType) {
        return new Article(articleNumber, articleName, clothType);
    }

    private void validateArticleNumber(int articleNumber) throws MyException {
        if(articleNumber < 1000 || articleNumber > 9999) {
            throw new MyException("Numer artykułu musi być > 999 i < 10000");
        } else {
            Article article = articleService.getByArticleNumber(articleNumber);
            if(article != null) {
                throw new MyException("Istnieje już artykuł o takim numerze: " +
                        article.getNumber() + " " + article.getName());
            }
        }
    }

    private void validateArticleName(String articleName) throws MyException {
        if(articleName.length() < 5) {
            throw new MyException("Nazwa jest za krótka");
        }
    }
}
