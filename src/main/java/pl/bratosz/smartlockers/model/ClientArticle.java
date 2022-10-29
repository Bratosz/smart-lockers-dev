package pl.bratosz.smartlockers.model;

import com.fasterxml.jackson.annotation.JsonView;
import pl.bratosz.smartlockers.model.clothes.Article;
import pl.bratosz.smartlockers.model.clothes.Cloth;

import javax.persistence.*;
import java.util.Comparator;
import java.util.Objects;
import java.util.Set;

@Entity
public class ClientArticle implements Comparable<ClientArticle> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.Public.class)
    private long id;

    @JsonView(Views.Public.class)
    @ManyToOne(fetch = FetchType.LAZY)
    private Article article;

    @JsonView(Views.Public.class)
    private double redemptionPrice;

    @JsonView(Views.Public.class)
    private boolean available;

    @ManyToOne(fetch = FetchType.LAZY)
    private Client client;

    @JsonView(Views.Public.class)
    private int depreciationPercentageCap;

    @JsonView(Views.Public.class)
    private int depreciationPeriod;

    @OneToMany(mappedBy = "clientArticle", fetch = FetchType.LAZY)
    private Set<Cloth> clothes;

    @ManyToOne
    @JsonView(Views.Public.class)
    private Badge badge;

    public ClientArticle() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    public double getRedemptionPrice() {
        return redemptionPrice;
    }

    public void setRedemptionPrice(double redemptionPrice) {
        this.redemptionPrice = redemptionPrice;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public Client getClient() {
        return client;
    }

    public Set<Cloth> getClothes() {
        return clothes;
    }

    public void setClothes(Set<Cloth> clothes) {
        this.clothes = clothes;
    }

    public void setClient(Client client) {
        this.client = client;
    }
    public int getDepreciationPercentageCap() {
        return depreciationPercentageCap;
    }

    public void setDepreciationPercentageCap(int depreciationPercentageCap) {
        this.depreciationPercentageCap = depreciationPercentageCap;
    }

    public int getDepreciationPeriod() {
        return depreciationPeriod;
    }

    public void setDepreciationPeriod(int depreciationPeriod) {
        this.depreciationPeriod = depreciationPeriod;
    }

    public void addClient(Client client) {
        client.addArticle(this);
        setClient(client);
    }

    public Badge getBadge() {
        return badge;
    }

    public void setBadge(Badge badge) {
        this.badge = badge;
    }

    @Override
    public int compareTo(ClientArticle o) {
        return Comparator.comparing(ClientArticle::getArticle)
                .compare(this, o);
    }

    @Override
    public String toString() {
        return article.getNumber() + " " + article.getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientArticle that = (ClientArticle) o;
        return getId() == that.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
