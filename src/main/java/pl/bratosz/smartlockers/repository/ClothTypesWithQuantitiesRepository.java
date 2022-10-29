package pl.bratosz.smartlockers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pl.bratosz.smartlockers.model.ArticleWithQuantity;

@Repository
public interface ClothTypesWithQuantitiesRepository extends JpaRepository<ArticleWithQuantity, Long> {

    @Transactional
    @Modifying
    @Query("delete from ArticleWithQuantity c where c.id = :id")
    void deleteById(long id);
}
