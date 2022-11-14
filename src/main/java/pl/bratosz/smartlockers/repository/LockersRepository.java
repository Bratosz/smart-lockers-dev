package pl.bratosz.smartlockers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pl.bratosz.smartlockers.model.Department;
import pl.bratosz.smartlockers.model.Location;
import pl.bratosz.smartlockers.model.Locker;
import pl.bratosz.smartlockers.model.Plant;

import java.util.List;

@Repository
public interface LockersRepository extends JpaRepository<Locker, Long> {

    @Query("select l from Locker l where l.plant.client.id = :clientId order by l.plant.plantNumber, l.lockerNumber ")
    List<Locker> getAllByClientId(@Param("clientId") long clientId);

    @Query("select l from Locker l where " +
            "l.plant.plantNumber = :plantNumber " +
            "order by l.lockerNumber")
    List<Locker> findAllByPlantNumber(int plantNumber);


    @Query("select count(l.plant.id) from Locker l where l.plant.id = :plantId ")
    int getAmountOfLockersByPlantId(@Param("plantId") long plantId);

    Locker deleteLockerById(Long id);

    @Query("select l from Locker l where l.lockerNumber = :lockerNumber " +
            "and " +
            "l.plant.plantNumber = :plantNumber " +
            "and " +
            "l.location = :location ")
    Locker getLockerByParameters(@Param("lockerNumber") Integer lockerNumber,
                                 @Param("plant") int plantNumber,
                                 @Param("location") Location location);

    @Query("select l from Locker l where l.plant.plantNumber = :plantNumber " +
            "and " +
            "(l.lockerNumber between :firstLocker and :lastLocker) " +
            "order by l.lockerNumber ")
    List<Locker> getLockersFromRange(int plantNumber, int firstLocker, int lastLocker);

    @Query("select l from Locker l where " +
            "(l.plant = :plant or :plant is null) " +
            "and " +
            "(l.department = :department or :department is null) " +
            "and " +
            "(l.location = :location or :location is null) order by l.lockerNumber ")
    List<Locker> getFiltered(
            @Param("plant") Plant plant,
            @Param("department") Department department,
            @Param("location") Location location);


    @Query("select l from Locker l where " +
            "l.plant.id = :plantId " +
            "and " +
            "l.lockerNumber = :lockerNumber")
    List<Locker> getLockersByPlantAndNumber(
            @Param("plantId") long plantId,
            @Param("lockerNumber") int lockerNumber);

    Locker getLockerById(long lockerId);

    @Query("select l from Locker l where " +
            "l.plant.id = :plantId " +
            "and l.lockerNumber between :startingLockerNumber " +
            "and :endLockerNumber " +
            "order by l.lockerNumber")
    List<Locker> getLockers(
            @Param("startingLockerNumber") int startingLockerNumber,
            @Param("endLockerNumber") int endLockerNumber,
            @Param("plantId")long plantId);

    @Query("select l from Locker l where " +
            "l.plant.id = :plantId " +
            "order by l.lockerNumber")
    List<Locker> getAllByPlantId(
            @Param("plantId") long plantId);

    List<Locker> getByPlantId(long plantId);

    List<Locker> getByPlantIdAndLocationId(long plantId, long locationId);

    List<Locker> getByPlantIdAndDepartmentIdAndLocationId(long plantId, long departmentId, long locationId);

    List<Locker> getByPlantIdAndDepartmentId(long plantId, long departmentId);

    @Transactional
    @Modifying
    @Query("delete from Locker l where l.id = :id")
    void deleteHardById(long id);
}