package pl.bratosz.smartlockers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.bratosz.smartlockers.model.EmployeeDummy;

@Repository
public interface EmployeesDummyRepository extends JpaRepository<EmployeeDummy, Long> {
}
