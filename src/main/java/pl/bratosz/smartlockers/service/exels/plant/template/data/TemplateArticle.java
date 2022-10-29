package pl.bratosz.smartlockers.service.exels.plant.template.data;

import java.util.Objects;

public class TemplateArticle {
    private int articleNumber;
    private String articleName;
    private int badgeNumber;

    public TemplateArticle(int articleNumber, String articleName, int badgeNumber) {
        this.articleNumber = articleNumber;
        this.articleName = articleName;
        this.badgeNumber = badgeNumber;
    }

    public int getArticleNumber() {
        return articleNumber;
    }

    public String getArticleName() {
        return articleName;
    }

    public int getBadgeNumber() {
        return badgeNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TemplateArticle that = (TemplateArticle) o;
        return getArticleNumber() == that.getArticleNumber();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getArticleNumber());
    }
}
