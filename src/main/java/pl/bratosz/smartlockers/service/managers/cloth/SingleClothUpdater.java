package pl.bratosz.smartlockers.service.managers.cloth;

import pl.bratosz.smartlockers.model.clothes.Cloth;
import pl.bratosz.smartlockers.model.clothes.LifeCycleStatus;
import pl.bratosz.smartlockers.service.ClothService;

import java.time.LocalDate;

import static pl.bratosz.smartlockers.service.managers.cloth.ClothUpdateReason.*;

public class SingleClothUpdater {
    private static final LocalDate INITIAL_DATE = LocalDate.of(1970, 1, 1);
    private ClothService clothService;

    public SingleClothUpdater(ClothService clothService) {
        this.clothService = clothService;
    }


    public void updateAssignedCloth(Cloth newCloth, Cloth recentlyAssigned) {
        ClothUpdateReason reason = CLOTH_ASSIGNMENT;
        newCloth.setBarcode(recentlyAssigned.getBarcode());
        updateCloth(newCloth, recentlyAssigned, reason);
    }

    public void updateReleasedCloth(Cloth cloth, Cloth recentlyReleased) {
        ClothUpdateReason reason = CLOTH_RELEASE;
        updateCloth(cloth, recentlyReleased, reason);
    }

    public void updateAddedCloth(Cloth cloth) {
        ClothUpdateReason reason = CLOTH_ADDED;
        updateStatus(cloth, reason);
        clothService.getClothesRepository().save(cloth);
    }

    private void updateCloth(Cloth toUpdate, Cloth actual, ClothUpdateReason reason) {
        updateDates(toUpdate, actual);
        updateStatus(toUpdate, reason);
        updateOrder(toUpdate);
        clothService.getClothesRepository().save(toUpdate);
    }

    public void updateWithdrawnCloth(Cloth cloth) {
        ClothUpdateReason reason = CLOTH_WITHDRAWAL;
        cloth.setActive(false);
        updateStatus(cloth, reason);
        updateOrder(cloth);
        clothService.getClothesRepository().save(cloth);
    }

    private void updateOrder(Cloth cloth) {
        clothService.getOrderService().update(cloth, clothService.getUser());
    }

    private void updateStatus(Cloth cloth, ClothUpdateReason reason) {
        switch(reason) {
            case CLOTH_ASSIGNMENT:
            case CLOTH_RELEASE:
            case CLOTH_ADDED:
                LocalDate releaseDate = cloth.getReleaseDate();
                cloth.setLifeCycleStatus(updateLifeCycleStatus(releaseDate));
                break;
            case CLOTH_WITHDRAWAL:
                cloth.setLifeCycleStatus(LifeCycleStatus.WITHDRAWN);
            default:
                break;
        }
        cloth.setStatus(clothService.getClothStatusService().create(
                        cloth,
                        reason,
                        clothService.getUser()));
    }

    private LifeCycleStatus updateLifeCycleStatus(LocalDate releaseDate) {
        if (releaseDate.isEqual(INITIAL_DATE)) {
            return LifeCycleStatus.BEFORE_RELEASE;
        } else {
            return LifeCycleStatus.IN_ROTATION;
        }
    }

    private void updateDates(Cloth assigned, Cloth recentlyAdded) {
        assigned.setAssignment(recentlyAdded.getAssignment());
        assigned.setLastWashing(recentlyAdded.getLastWashing());
        assigned.setReleaseDate(recentlyAdded.getReleaseDate());
    }


}
