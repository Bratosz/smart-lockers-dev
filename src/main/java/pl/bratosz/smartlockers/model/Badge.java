package pl.bratosz.smartlockers.model;

import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Badge {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @JsonView(Views.Public.class)
    private String name;

    @JsonView(Views.Public.class)
    private int number;

    @OneToMany(mappedBy = "badge")
    private Set<ClientArticle> clientArticles;

    public Badge() {
    }

    public Badge(String name, int number) {
        this.name = name;
        this.number = number;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public Set<ClientArticle> getClientArticles() {
        if (clientArticles == null)
            return new HashSet<>();
        else
            return clientArticles;
    }

    public void setClientArticles(Set<ClientArticle> clientArticles) {
        this.clientArticles = clientArticles;
    }

    public void addClientArticle(ClientArticle clientArticle) {
        Set<ClientArticle> clientArticles = getClientArticles();
        clientArticles.add(clientArticle);
        setClientArticles(clientArticles);
    }
}
