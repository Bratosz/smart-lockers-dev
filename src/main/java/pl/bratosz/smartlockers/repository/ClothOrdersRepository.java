package pl.bratosz.smartlockers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pl.bratosz.smartlockers.model.orders.ClothOrder;

import java.util.Set;

@Repository
public interface ClothOrdersRepository extends JpaRepository<ClothOrder, Long> {

    @Query("select o from ClothOrder o where o.clothToRelease.employee.id = :employeeId")
    Set<ClothOrder> getByEmployeeId(long employeeId);

    @Query("select  c from ClothOrder c where c.id = :id ")
    ClothOrder getById(long id);

    @Transactional
    @Modifying
    @Query("delete from ClothOrder c where c.id = :id ")
    void deleteById(long id);
}
