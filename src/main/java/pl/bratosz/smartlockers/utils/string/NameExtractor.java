package pl.bratosz.smartlockers.utils.string;

import pl.bratosz.smartlockers.service.jgenderize.GenderizeIoAPI;
import pl.bratosz.smartlockers.service.jgenderize.client.Genderize;
import pl.bratosz.smartlockers.service.jgenderize.model.Gender;
import pl.bratosz.smartlockers.service.jgenderize.model.NameGender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static pl.bratosz.smartlockers.utils.string.PolishSignsRemover.removePolishSigns;

public class NameExtractor {
    private static NameExtractor instance;
    private Genderize genderize;

    public static NameExtractor getInstance() {
        if (instance == null) {
            return new NameExtractor();
        } else {
            return instance;
        }
    }

    private NameExtractor() {
        this.genderize = GenderizeIoAPI.create();
    }

    public EmployeeNameAndGender get(String employeeName) {
        String[] split = employeeName.split("\\s+");
        if (nameConsistFromTwoElements(split)) {
            return get(split);
        } else {
            throw new IllegalArgumentException("Name contains too much or to little parts");
        }
    }

    private EmployeeNameAndGender get(String[] split) {
        List<String> names = new ArrayList<>(Arrays.asList(split));
        String[] rps = removePolishSigns(split);
        List<String> wpsNames = new ArrayList<>(Arrays.asList(rps));
            List<NameGender> genders = genderize.getGenders(rps);
            if (genderIsUnknown(genders)) return EmployeeNameAndGender.createForGenderUnknown(genders);
            if (oneGenderIsKnown(genders)) {
                NameGender nameGender = genders.stream().filter(g -> !g.isNull()).findFirst().get();
                return getEmployeeName(names, wpsNames, nameGender);
            } else {
                NameGender nameGender = getGenderName(genders);
                return getEmployeeName(names, wpsNames, nameGender);
            }
    }

    private NameGender getGenderName(List<NameGender> genders) {
        long count = genders.stream().filter(g -> g.getProbability() > 0.9f).count();
        if (count == 1) return genders.stream().filter(g -> g.getProbability() > 0.9f).findFirst().get();
        else if (count == 2) {
            count = genders.stream().filter(g -> g.getProbability() >= 0.95f).count();
            if (count == 1) return genders.stream().filter(g -> g.getProbability() >= 0.95f).findFirst().get();
        }
        Collections.sort(genders);
        return genders.get(0);
    }

    private EmployeeNameAndGender getEmployeeName(List<String> names, List<String> rpsNames, NameGender nameGender) {
        String name = nameGender.getName();
        int i = rpsNames.indexOf(name);
        String firstName = names.remove(i);
        String lastName = names.get(0);
        Gender genderType = nameGender.getGenderType();
        return EmployeeNameAndGender.create(lastName, firstName, genderType);
    }


    private boolean oneGenderIsKnown(List<NameGender> genders) {
        long count = genders.stream().filter(g -> g.isNull()).count();
        if (count == 1) return true;
        else return false;
    }

    private boolean genderIsUnknown(List<NameGender> genders) {
        return genders.stream().allMatch(g -> g.isNull());

    }

    private boolean nameConsistFromTwoElements(String[] split) {
        if (split.length > 2) {
            return false;
        } else if (split.length < 2) {
            return false;
        } else {
            return true;
        }
    }
}
