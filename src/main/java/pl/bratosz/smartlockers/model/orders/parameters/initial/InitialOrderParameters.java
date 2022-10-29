package pl.bratosz.smartlockers.model.orders.parameters.initial;

import pl.bratosz.smartlockers.model.clothes.Article;
import pl.bratosz.smartlockers.model.clothes.Cloth;
import pl.bratosz.smartlockers.model.clothes.ClothSize;

public class InitialOrderParameters implements InitialForNewOne {
    private Article article;
    private ClothSize size;
    private Cloth clothToExchange;

    public InitialOrderParameters(Article article, ClothSize size, Cloth clothToExchange) {
        this.article = article;
        this.size = size;
        this.clothToExchange = clothToExchange;
    }

    @Override
    public Article getArticle() {
        return article;
    }

    @Override
    public ClothSize getSize() {
        return size;
    }

    public Cloth getClothToExchange() {
        return clothToExchange;
    }
}
