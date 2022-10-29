package pl.bratosz.smartlockers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.bratosz.smartlockers.model.ClientArticle;

import java.util.List;

@Repository
public interface ClientArticlesRepository extends JpaRepository<ClientArticle, Long> {

    @Query("select a from ClientArticle a where a.client.id = :clientId order by a.article.number")
    List<ClientArticle> getAllBy(
            @Param("clientId") long clientId);


    @Query("select a from ClientArticle a where a.id = :id")
    ClientArticle getBy(
            @Param("id") long id);

    @Query("select a from ClientArticle a where " +
            " a.article.number = :articleNumber and " +
            " a.client.id = :clientId ")
    ClientArticle getBy(
            @Param("articleNumber") int articleNumber,
            @Param("clientId") long clientId);
}
