package pl.bratosz.smartlockers.service.exels.plant.template.data;

import java.util.HashMap;
import java.util.Map;

public class TemplateEmployee {

    private String personalNumber;
    private String firstName;
    private String lastName;
    private String department;
    private TemplatePosition position;
    private Map<TemplateArticle, TemplateClothSize> articlesWithSizes;
    private String location;
    private int lockerNumber;
    private int boxNumber;

    public TemplateEmployee(String personalNumber, String firstName, String lastName, String department, TemplatePosition position, String location) {
        this.personalNumber = personalNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.department = department;
        this.position = position;
        this.location = location;
        this.articlesWithSizes = new HashMap<>();
    }

    public TemplateEmployee(String personalNumber, String firstName, String lastName, String department, TemplatePosition position, int lockerNumber, int boxNumber) {
        this.personalNumber = personalNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.department = department;
        this.position = position;
        this.lockerNumber = lockerNumber;
        this.boxNumber = boxNumber;
        this.location = "";
        this.articlesWithSizes = new HashMap<>();
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public TemplatePosition getPosition() {
        return position;
    }

    public String getLocation() {
        return location;
    }

    public String getDepartment() {
        return department;
    }

    public String getPersonalNumber() {
        return personalNumber;
    }

    public int getLockerNumber() {
        return lockerNumber;
    }

    public int getBoxNumber() {
        return boxNumber;
    }

    public Map<TemplateArticle, TemplateClothSize> getArticlesWithSizes() {

        return articlesWithSizes;
    }

    public void setArticlesWithSizes(Map<TemplateArticle, TemplateClothSize> articlesWithSizes) {
        this.articlesWithSizes = articlesWithSizes;
    }

    public void addArticleWithSize(TemplateArticle article, TemplateClothSize size) {
        this.articlesWithSizes.put(article, size);
    }

    @Override
    public String toString() {
        return firstName + " " + lastName + " " + position;
    }
}
