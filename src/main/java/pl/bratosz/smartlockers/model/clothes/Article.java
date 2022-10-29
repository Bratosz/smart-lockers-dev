package pl.bratosz.smartlockers.model.clothes;

import com.fasterxml.jackson.annotation.JsonView;
import pl.bratosz.smartlockers.model.ClientArticle;
import pl.bratosz.smartlockers.model.Views;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Entity
public class Article implements Comparable<Article>{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.Public.class)
    private long id;

    @JsonView(Views.Public.class)
    private int number;

    @JsonView(Views.Public.class)
    private String name;

    @Enumerated(EnumType.STRING)
    @JsonView(Views.Public.class)
    private ClothType clothType;

    @OneToMany(mappedBy = "article", fetch = FetchType.LAZY)
    private Set<ClientArticle> clientArticles;

    public Article() {

    }

    public Article(int number, String name, ClothType clothType) {
        this.number = number;
        this.name = name;
        this.clothType = clothType;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ClothType getClothType() {
        return clothType;
    }

    public void setClothType(ClothType clothType) {
        this.clothType = clothType;
    }

    public Set<ClientArticle> getClientArticles() {
        return clientArticles;
    }

    public void setClientArticles(Set<ClientArticle> clientArticles) {
        this.clientArticles = clientArticles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Article)) return false;
        Article article = (Article) o;
        return getNumber() == article.getNumber();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getNumber());
    }

    @Override
    public int compareTo(Article o) {
        return Integer.compare(getNumber(), o.getNumber());
    }
}
