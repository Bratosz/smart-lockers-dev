package pl.bratosz.smartlockers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.bratosz.smartlockers.model.users.ManagementList;

@Repository
public interface ManagementListRepository extends JpaRepository<ManagementList, Long> {

}
