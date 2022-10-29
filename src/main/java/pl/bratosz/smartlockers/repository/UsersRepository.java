package pl.bratosz.smartlockers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.bratosz.smartlockers.model.users.User;

@Repository
public interface UsersRepository extends JpaRepository <User, Long> {
    User getById(long id);

    User getUserByFirstName(String firstName);

    @Query("select u.actualClientId from User u where u.id = :userId")
    long getActualClientId(@Param("userId")long userId);
}
