package pl.bratosz.smartlockers.service;

import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.exception.ClothException;
import pl.bratosz.smartlockers.exception.ClothOrderException;
import pl.bratosz.smartlockers.model.clothes.*;
import pl.bratosz.smartlockers.model.users.User;
import pl.bratosz.smartlockers.repository.ClothesStatusRepository;
import pl.bratosz.smartlockers.service.managers.cloth.ClothUpdateReason;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import static pl.bratosz.smartlockers.model.clothes.ClothActualStatus.*;
import static pl.bratosz.smartlockers.model.clothes.ClothDestination.*;
import static pl.bratosz.smartlockers.model.clothes.LifeCycleStatus.*;

@Service
public class ClothStatusService {
    private ClothesStatusRepository clothesStatusRepository;
    private User user;

    public ClothStatusService(ClothesStatusRepository clothesStatusRepository) {
        this.clothesStatusRepository = clothesStatusRepository;
    }

    public ClothStatus create(ClothDestination destination,
                              User user) {
        ClothActualStatus actualStatus =
                resolveActualStatus(destination);
        ClothStatus clothStatus =
                new ClothStatus(actualStatus, destination, user, LocalDateTime.now());
        return clothesStatusRepository.save(clothStatus);
    }

    public ClothStatus create(ClothActualStatus actualStatus,
                              Cloth cloth,
                              User user) {
        ClothDestination destination =
                resolveDestination(actualStatus);
        ClothStatus clothStatus =
                new ClothStatus(actualStatus, destination, cloth, user, LocalDateTime.now());
        return clothesStatusRepository.save(clothStatus);
    }

    public ClothStatus create(ClothDestination destiny,
                              Cloth cloth,
                              User user) {
        ClothActualStatus status =
                resolveActualStatus(destiny);
        ClothStatus clothStatus =
                new ClothStatus(status, destiny, cloth, user, LocalDateTime.now());
        return clothesStatusRepository.save(clothStatus);
    }

    public ClothStatus create(Cloth cloth, ClothUpdateReason reason, User user) {
        LifeCycleStatus lifeCycleStatus = cloth.getLifeCycleStatus();
        ClothActualStatus actualStatus = resolveActualStatus(reason, lifeCycleStatus);
        ClothDestination destination = resolveDestination(actualStatus);
        ClothStatus clothStatus = new ClothStatus(
                actualStatus, destination, cloth, user, LocalDateTime.now());
        return clothesStatusRepository.save(clothStatus);
    }

    private ClothDestination resolveDestination(ClothActualStatus actualStatus) {
        ClothDestination destination = null;
        switch (actualStatus) {
            case ORDERED:
                destination = FOR_ASSIGN;
                break;
            case ASSIGNED:
            case IN_PREPARATION:
                destination = FOR_RELEASE;
                break;
            case RELEASED:
                destination = FOR_WASH;
                break;
            case ACCEPTED_FOR_EXCHANGE:
                destination = FOR_WITHDRAW_AND_EXCHANGE;
                break;
            case EXCHANGED:
            case ACCEPTED_AND_WITHDRAWN:
                destination = FOR_DISPOSAL;
                break;
        }
        return destination;
    }

    private ClothActualStatus resolveActualStatus(
            ClothUpdateReason reason,
            LifeCycleStatus lifeCycleStatus) {
        switch (reason) {
            case CLOTH_ASSIGNMENT:
            case CLOTH_RELEASE:
            case CLOTH_ADDED:
                if (lifeCycleStatus.equals(BEFORE_RELEASE))
                    return ASSIGNED;
                else if (lifeCycleStatus.equals(IN_ROTATION))
                    return RELEASED;
            case CLOTH_WITHDRAWAL:
                return ACCEPTED_AND_WITHDRAWN;
            default:
                return ClothActualStatus.UNKNOWN;
        }
    }

    private ClothActualStatus resolveActualStatus(ClothDestination destiny) {
        ClothActualStatus status = null;
        switch (destiny) {
            case FOR_ASSIGN:
                status = ORDERED;
                break;
            case FOR_RELEASE:
                status = ASSIGNED;
                break;
            case FOR_WASH:
                status = RELEASED;
                break;
            case FOR_WITHDRAW_AND_DELETE:
                status = RELEASED;
                break;
            case FOR_WITHDRAW_AND_EXCHANGE:
                status = RELEASED;
                break;
            case FOR_DISPOSAL:
                status = ACCEPTED_AND_WITHDRAWN;
                break;
        }
        return status;
    }

    public void hardDelete(List<ClothStatus> statusHistory) {
        for (ClothStatus status : statusHistory) {
            clothesStatusRepository.deleteById(status.getId());
        }
    }
}
