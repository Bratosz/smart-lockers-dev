package pl.bratosz.smartlockers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.bratosz.smartlockers.model.Badge;

import java.util.Set;

@Repository
public interface BadgesRepository extends JpaRepository<Badge, Long> {

    @Query("select b from Badge b where b.number = :badgeNumber")
    Badge getByNumber(@Param("badgeNumber")int badgeNumber);

    @Query("select b from Badge b")
    Set<Badge> getAll();
}
