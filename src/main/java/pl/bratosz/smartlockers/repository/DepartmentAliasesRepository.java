package pl.bratosz.smartlockers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.bratosz.smartlockers.model.DepartmentAlias;

@Repository
public interface DepartmentAliasesRepository extends JpaRepository<DepartmentAlias, Long> {
}
