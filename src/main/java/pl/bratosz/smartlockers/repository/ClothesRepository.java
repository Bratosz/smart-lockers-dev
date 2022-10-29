package pl.bratosz.smartlockers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pl.bratosz.smartlockers.model.Employee;
import pl.bratosz.smartlockers.model.Plant;
import pl.bratosz.smartlockers.model.clothes.Cloth;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Repository
public interface ClothesRepository extends JpaRepository<Cloth, Long> {
    @Query("select c.barcode from Cloth c where c.employee.box.locker.plant.id = :plantId " +
            "and c.employee.box.locker.lockerNumber = 0 " +
            "and c.lastWashing > :date")
    List<Object[]> getBarcodeAndLastWashing(long plantId, Date date);

    @Query("select c from Cloth c where c.barcode = :barcode and c.active = true")
    Cloth getByBarcode(long barcode);

    @Transactional
    @Modifying
    @Query("delete from Cloth c where c.id = :id ")
    void deleteById(long id);

    @Transactional
    @Modifying
    @Query("update Cloth c set c.lastWashing = :lastWashing " +
            "where c.id = :id")
    void updateLastWashingById(LocalDate lastWashing, long id);

    @Transactional
    @Modifying
    @Query("update Cloth c set c.rotational = true " +
            "where c.employee = :employee")
    void setRotationalByEmployee(Employee employee);

    @Transactional
    @Modifying
    @Query("update Cloth c set c.rotational = true " +
            "where c.employee.id = :employeeId and " +
            "c.rotational = false")
    void setRotationalByEmployeeId(long employeeId);

    Cloth getById(long id);

    @Query("select c from Cloth c " +
            "where c.rotational = true " +
            "and c.releasedToEmployee = true " +
            "and c.employee.box.locker.plant.id = :plantId")
    List<Cloth> getReleasedRotationalClothes(long plantId);

    @Query("select c.barcode from Cloth c where c.active = true and c.employee.box.locker.plant = :plant order by c.barcode")
    List<Long> getAllActiveBy(Plant plant);

    @Query("select new pl.bratosz.smartlockers.model.clothes.Cloth (c.id, c.barcode, c.lastWashing, c.employee) " +
            "from Cloth c where c.active = true and c.employee.box.locker.plant = :plant order by c.barcode")
    List<Cloth> getActiveSimpleClothesBy(Plant plant);

}
