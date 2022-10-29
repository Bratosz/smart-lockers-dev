package pl.bratosz.smartlockers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.bratosz.smartlockers.model.Location;

public interface LocationsRepository extends JpaRepository<Location, Long> {
}
