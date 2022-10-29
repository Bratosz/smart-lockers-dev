package pl.bratosz.smartlockers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.bratosz.smartlockers.model.Client;
import pl.bratosz.smartlockers.model.Location;

import java.util.List;

public interface LocationRepository extends JpaRepository<Location, Long> {

    @Query("select l from Location l join l.plant p where " +
            "p.plantNumber = :plantNumber and " +
            "l.name = :name ")
    Location getByNameAndPlantNumber(@Param("name")String name,
                                     @Param("plantNumber") int plantNumber);

    @Query("select l from Location l join l.client c where " +
            "c.id = :clientId order by l.name desc")
    List<Location> getAll(long clientId);

    Location getBySurrogateAndClient(boolean surrogate, Client client);

    Location getBySurrogateAndClientId(boolean surrogate, long clientId);

    @Query("select l from Location l where l.id = :id")
    Location getById(long id);

    @Query("select l from Location l where l.name = :name and l.client.id  = :clientId")
    Location getByNameAndClient(@Param("name") String name, @Param("clientId") long clientId);
}
