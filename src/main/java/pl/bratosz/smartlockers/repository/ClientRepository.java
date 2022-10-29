package pl.bratosz.smartlockers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.bratosz.smartlockers.model.Client;

import javax.persistence.NonUniqueResultException;
import java.util.List;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    Client getById(long clientId);

    @Query("select c from Client c")
    List<Client> getAll();

    @Query("select c from Client c join c.plants p where p.plantNumber = :plantNumber")
    Client getByPlantNumber(int plantNumber) throws NonUniqueResultException;
}
