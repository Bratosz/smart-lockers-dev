package pl.bratosz.smartlockers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.bratosz.smartlockers.model.Client;
import pl.bratosz.smartlockers.model.Plant;

import java.util.List;
import java.util.Set;

public interface PlantsRepository extends JpaRepository <Plant, Long> {

    Plant getByPlantNumber(int plantNumber);

    @Query("select p from Plant p join p.client c where " +
            "c.id = :clientId order by p.plantNumber ")
    List<Plant> getAll(@Param("clientId") long clientId);

    @Query("select p from Plant p where p.id = :id")
    Plant getById(long id);

    @Query("select p.id from Plant p where p.plantNumber = :plantNumber")
    long getPlantIdByPlantNumber(@Param("plantNumber")int plantNumber);
}
