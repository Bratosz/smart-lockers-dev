package pl.bratosz.smartlockers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.bratosz.smartlockers.model.MeasurementList;

@Repository
public interface MeasurementListRepository extends JpaRepository<MeasurementList, Long> {

    @Query("select e from MeasurementList e where e.client.id = :clientId")
    MeasurementList getByClientId(long clientId);
}
