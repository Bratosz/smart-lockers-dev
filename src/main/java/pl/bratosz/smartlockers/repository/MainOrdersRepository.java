package pl.bratosz.smartlockers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pl.bratosz.smartlockers.model.orders.MainOrder;

import javax.persistence.Table;
import java.util.Set;

@Repository
public interface MainOrdersRepository extends JpaRepository<MainOrder, Long> {

    @Query("select o from MainOrder o where o.employee.id = :employeeId")
    Set<MainOrder> getByEmployeeId(long employeeId);

    @Query("select o from MainOrder o where o.id = :id")
    MainOrder getById(long id);

    @Transactional
    @Modifying
    @Query("delete from MainOrder mo where mo.id = :id")
    void deleteMainOrderById(long id);

    @Query("select o from MainOrder o where o.desiredClientArticle.client.id = :clientId " +
            "and o.active = true and o.reported = false")
    Set<MainOrder> getOrdersToReport(long clientId);
}
