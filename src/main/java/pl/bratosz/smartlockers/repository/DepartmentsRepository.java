package pl.bratosz.smartlockers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.bratosz.smartlockers.model.Client;
import pl.bratosz.smartlockers.model.Department;
import pl.bratosz.smartlockers.service.ClientService;

import java.util.List;

@Repository
public interface DepartmentsRepository extends JpaRepository<Department, Long> {


    Department getByNameAndMainPlantNumber(String name, int mainPlantNumber);

    @Query("select d from Department d where d.name = :name " +
            "and d.client.id = :clientId")
    Department getByNameAndClientId(String name, long clientId);


    @Query("select d from Department d join d.aliases a " +
            "where a.alias = :alias " +
            "and d.client.id = :clientId")
    Department getByAliasAndClientId(String alias, long clientId);

    @Query("select d from Department d join d.client c where " +
            "c.id = :clientId order by d.mainPlantNumber")
    List<Department> getAll(long clientId);

    Department getById(long departmentId);

    Department getBySurrogateAndClient(boolean surrogate, Client client);

    Department getBySurrogateAndClientId(boolean surrogate, long clientId);
}
