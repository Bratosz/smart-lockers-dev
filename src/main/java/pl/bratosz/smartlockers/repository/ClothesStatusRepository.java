package pl.bratosz.smartlockers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pl.bratosz.smartlockers.model.clothes.ClothStatus;

@Repository
public interface ClothesStatusRepository extends JpaRepository<ClothStatus, Long> {
    @Transactional
    @Modifying
    @Query("delete from ClothStatus s where s.id = :id ")
    void deleteById(long id);

}
