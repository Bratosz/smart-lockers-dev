package pl.bratosz.smartlockers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.bratosz.smartlockers.model.EmployeeGeneral;

@Repository
public interface EmployeeGeneralRepository extends JpaRepository<EmployeeGeneral, Long> {
    EmployeeGeneral getById(long id);
}
