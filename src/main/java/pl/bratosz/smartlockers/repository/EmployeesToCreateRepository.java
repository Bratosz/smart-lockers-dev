package pl.bratosz.smartlockers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.bratosz.smartlockers.service.pasting.employee.EmployeeToCreate;

import java.util.List;

@Repository
public interface EmployeesToCreateRepository extends JpaRepository<EmployeeToCreate, Long> {

    @Query("select e from EmployeeToCreate e where e.client.id = :clientId order by e.client.id")
    List<EmployeeToCreate> getAllByClient(long clientId);

    EmployeeToCreate getById(long id);
}
