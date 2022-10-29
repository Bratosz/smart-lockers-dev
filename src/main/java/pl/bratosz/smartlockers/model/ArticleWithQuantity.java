package pl.bratosz.smartlockers.model;

import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
public class ArticleWithQuantity implements MyEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.Public.class)
    private long id;

    @JsonView(Views.Public.class)
    @ManyToMany
    private Set<ClientArticle> availableArticles;

    @JsonView(Views.Public.class)
    private int quantity;

    @ManyToOne
    private Position position;

    public ArticleWithQuantity() {
    }

    public ArticleWithQuantity(ClientArticle article, int quantity) {
        this.quantity = quantity;
        availableArticles = new LinkedHashSet<>();
        availableArticles.add(article);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Set<ClientArticle> getAvailableArticles() {
        return availableArticles;
    }

    public void setAvailableArticles(Set<ClientArticle> availableArticles) {
        this.availableArticles = availableArticles;
    }

    public void addArticle(ClientArticle article) {
        if(availableArticles == null) {
            availableArticles = new HashSet<>();
        }
        availableArticles.add(article);
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }


}
