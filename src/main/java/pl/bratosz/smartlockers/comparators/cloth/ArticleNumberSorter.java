package pl.bratosz.smartlockers.comparators.cloth;

import pl.bratosz.smartlockers.model.clothes.Cloth;

import java.util.Comparator;

public class ArticleNumberSorter implements Comparator<Cloth> {

    @Override
    public int compare(Cloth c1, Cloth c2) {
        return c1.getClientArticle().getArticle().getNumber()
                - c2.getClientArticle().getArticle().getNumber();
    }
}
