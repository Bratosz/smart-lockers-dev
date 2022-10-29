package pl.bratosz.smartlockers.service.clothes;

import pl.bratosz.smartlockers.model.ClientArticle;
import pl.bratosz.smartlockers.model.Employee;
import pl.bratosz.smartlockers.model.clothes.Cloth;
import pl.bratosz.smartlockers.model.clothes.ClothSize;

import java.util.*;
import java.util.stream.Collectors;

public class OrdinalNumberResolver {
    private ClothSize desiredSize;
    private ClientArticle desiredClientArticle;
    private Set<Cloth> actualClothes;
    private Employee employee;
    private int highestOrdinalNumber;
    private int nextOrdinalNumber;
    private List<Integer> missingOrdinalNumbers;
    private List<Integer> actualOrdinalNumbers;


    public static OrdinalNumberResolver createForChangeSize(
            ClothSize desiredSize,
            Cloth clothToExchange,
            Employee employee) {
        return new OrdinalNumberResolver(
                desiredSize,
                clothToExchange.getClientArticle(),
                employee);
    }

    public static OrdinalNumberResolver createForChangeArticle(
            ClothSize desiredSize,
            ClientArticle desiredClientArticle,
            Cloth clothToExchange) {
        return new OrdinalNumberResolver(
                desiredSize,
                desiredClientArticle,
                clothToExchange.getEmployee());
    }

    public static OrdinalNumberResolver createForExchangeForNewOnes(Cloth clothToExchange) {
        return new OrdinalNumberResolver(
                clothToExchange.getSize(),
                clothToExchange.getClientArticle(),
                clothToExchange.getEmployee());
    }

    public static OrdinalNumberResolver createForNewArticle(
            ClothSize size,
            ClientArticle article,
            Employee employee) {
        return new OrdinalNumberResolver(size, article, employee);
    }

    public int getNextNumber() {
        if (missingOrdinalNumbers.size() > 0) {
            int ordinalNumber = missingOrdinalNumbers.remove(0);
            if (ordinalNumber == highestOrdinalNumber)
                nextOrdinalNumber = ordinalNumber;
            else {
                actualOrdinalNumbers.add(ordinalNumber);
                return ordinalNumber;
            }
        } else if (nextOrdinalNumber == 0) {
            nextOrdinalNumber = actualOrdinalNumbers.size() + 1;
        }
        return nextOrdinalNumber++;
    }

    public int getForExchangeForNewOne(int actualOrdinalNumber) {
        if(missingOrdinalNumbers.isEmpty()) {
            return actualOrdinalNumber;
        } else {
            nextOrdinalNumber = missingOrdinalNumbers.get(0);
            if(actualOrdinalNumber < nextOrdinalNumber) {
                return actualOrdinalNumber;
            } else {
                missingOrdinalNumbers.remove(0);
                missingOrdinalNumbers.add(0, actualOrdinalNumber);
                Collections.sort(missingOrdinalNumbers);
                return nextOrdinalNumber;
            }
        }
    }

    private OrdinalNumberResolver(
            ClothSize desiredSize,
            ClientArticle desiredClientArticle,
            Employee employee) {
        this.desiredSize = desiredSize;
        this.desiredClientArticle = desiredClientArticle;
        this.employee = employee;
        this.actualClothes = getActualClothes();
        this.actualOrdinalNumbers = getActualOrdinalNumbersAndSetTheHighestOne(actualClothes);
        this.missingOrdinalNumbers = getMissingOrdinalNumbers(actualOrdinalNumbers, actualClothes.size());
    }

    private List<Integer> getMissingOrdinalNumbers(List<Integer> actualOrdinalNumbers, int numberOfClothes) {
        List<Integer> missingOrdinalNumbers = new LinkedList<>();
        for (int i = 1; i <= numberOfClothes; i++) {
            if (!actualOrdinalNumbers.contains(i)) {
                missingOrdinalNumbers.add(i);
                if (i > highestOrdinalNumber)
                    highestOrdinalNumber = i;
            }
        }
        Collections.sort(missingOrdinalNumbers);
        return missingOrdinalNumbers;
    }

    private List<Integer> getActualOrdinalNumbersAndSetTheHighestOne(Set<Cloth> actualClothes) {
        List<Integer> actualOrdinalNumbers = new LinkedList<>();
        actualClothes.forEach(c -> {
            actualOrdinalNumbers.add(c.getOrdinalNumber());
            if (c.getOrdinalNumber() > highestOrdinalNumber)
                highestOrdinalNumber = c.getOrdinalNumber();
        });
        Collections.sort(actualOrdinalNumbers);
        return actualOrdinalNumbers;
    }

    private Set<Cloth> getActualClothes() {
        List<Cloth> clothes = employee.getClothes();
        clothes = clothes.stream()
                .filter(c -> c.isActive())
                .filter(c -> c.getClientArticle().equals(desiredClientArticle))
                .filter(c -> c.getSize().equals(desiredSize))
                .collect(Collectors.toList());
        Set<Cloth> actualClothes = new HashSet<>();
        clothes.forEach(c -> {
            if (c.getExchangeOrder() == null) {
                actualClothes.add(c);
            } else {
                actualClothes.add(c.getExchangeOrder().getClothToRelease());
            }
        });
        return actualClothes;
    }
}
