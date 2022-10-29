package pl.bratosz.smartlockers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.bratosz.smartlockers.model.SimpleBox;

public interface SimpleBoxesRepository extends JpaRepository<SimpleBox, Integer> {
}
