package pl.bratosz.smartlockers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pl.bratosz.smartlockers.model.Client;
import pl.bratosz.smartlockers.model.Position;

import java.util.Set;

@Repository
public interface PositionsRepository extends JpaRepository<Position, Long> {

    Position getById(long positionId);

    @Query("select p from Position p where p.client.id = :clientId")
    Set<Position> getByClientId(long clientId);

    @Transactional
    @Modifying
    @Query("delete from Position p where p.id = :positionId")
    void deleteById(long positionId);

    @Query("select p from Position p where p.client = :c and p.name = :positionName ")
    Position getByClientIdAndName(Client c, String positionName);

    Position getByClientAndSurrogate(Client c, boolean b);
}
