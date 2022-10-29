package pl.bratosz.smartlockers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import pl.bratosz.smartlockers.model.orders.OrderStatus;

public interface OrdersStatusRepository extends JpaRepository<OrderStatus, Long> {

    @Transactional
    @Modifying
    @Query("delete from OrderStatus os where os.clothOrder.id = :id ")
    void deleteByClothOrderId(long id);

    @Transactional
    @Modifying
    @Query("delete from OrderStatus os where os.mainOrder.id = :id ")
    void deleteByMainOrderId(long id);
}
