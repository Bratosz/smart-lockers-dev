package pl.bratosz.smartlockers.service.managers.cloth;

import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.date.LocalDateConverter;
import pl.bratosz.smartlockers.model.*;
import pl.bratosz.smartlockers.model.clothes.*;
import pl.bratosz.smartlockers.model.users.User;
import pl.bratosz.smartlockers.service.ClothStatusService;
import pl.bratosz.smartlockers.service.managers.OrderManager;
import pl.bratosz.smartlockers.service.managers.cloth.ClothesCreator;
import pl.bratosz.smartlockers.utils.Utils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ClothesManager {
    private ClothStatusService clothStatusService;
    private OrderManager orderManager;
    private ClothesCreator creator;
    private Set<Cloth> clothes;
    private Cloth cloth;

    public ClothesManager(ClothStatusService clothStatusService,
                          ClothesCreator creator,
                          OrderManager orderManager) {
        this.clothStatusService = clothStatusService;
        this.creator = creator;
        this.orderManager = orderManager;
    }

    public Cloth createNewInstead(int ordinalNumber,
                                  ClientArticle clientArticle,
                                  ClothSize size,
                                  LengthModification lengthModification,
                                  Employee employee) {
        return creator.createNewInstead(
                ordinalNumber, clientArticle, size, lengthModification, employee);
    }



    public Cloth createExisting(Cloth cloth, User user) {
        return creator.createExisting(cloth, user);
    }

    public Cloth createExisting(long barCode,
                                Date assignment,
                                Date lastWashing,
                                Date release,
                                int ordinalNo,
                                ClientArticle clientArticle,
                                ClothSize size,
                                User user) {
        cloth = new Cloth();
        cloth.setBarcode(barCode);
        cloth.setAssignment(LocalDateConverter.convert(assignment));
        cloth.setLastWashing(LocalDateConverter.convert(lastWashing));
        cloth.setReleaseDate(LocalDateConverter.convert(release));
        cloth.setOrdinalNumber(ordinalNo);
        cloth.setClientArticle(clientArticle);
        cloth.setSize(size);
        return createExisting(cloth, user);
    }

    public List<Cloth> set(ClothDestination destiny,
                           List<Cloth> clothes,
                           User user) {
        List<Cloth> updatedClothes = new LinkedList<>();
        for(Cloth cloth : clothes) {
            ClothStatus clothStatus = clothStatusService.create(destiny, cloth, user);
            cloth = updateCloth(clothStatus, cloth);
            updatedClothes.add(cloth);
        }
        return updatedClothes;
    }

    public Cloth updateCloth(ClothStatus actualStatus, Cloth clothToUpdate) {
        clothToUpdate.setStatus(actualStatus);
        LifeCycleStatus lifeCycleStatus = actualStatus.getStatus().getLifeCycleStatus();
        clothToUpdate.setLifeCycleStatus(lifeCycleStatus);
        if(lifeCycleStatus == LifeCycleStatus.WITHDRAWN ||
        lifeCycleStatus == LifeCycleStatus.BEFORE_RELEASE) {
            clothToUpdate.setActive(false);
        } else {
            clothToUpdate.setActive(true);
        }
        return clothToUpdate;
    }


    public int getHighestOrdinalNumber(
            Employee employee,
            ClientArticle clientArticle) {
        List<Cloth> clothes = employee.getClothes();
        Article article = clientArticle.getArticle();
        List<Cloth> articles = clothes.stream()
                .filter(c -> c.getClientArticle().getArticle().equals(article))
                .collect(Collectors.toList());
        if(articles.isEmpty()) {
            return 0;
        } else {
            List<Integer> ordinalNumbers = articles.stream().map(a -> a.getOrdinalNumber()).collect(Collectors.toList());
            int highestNumber = Utils.getHighestNumber(ordinalNumbers);
            return highestNumber;
        }
    }
}
