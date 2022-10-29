package pl.bratosz.smartlockers.service.exels.plant.template.data;

import pl.bratosz.smartlockers.exception.MyException;

import java.util.*;

public class TemplatePosition {
    String name;
    Set<String> departments;
    Map<TemplateArticle, Integer> articlesWithQuantities;

    public TemplatePosition(String name) {
        this.name = name;
        this.departments = new HashSet<>();
        this.articlesWithQuantities = new HashMap<>();
    }

    public TemplatePosition(TemplatePosition position) {
        this.name = position.getName();
        this.departments = new HashSet<>();
        this.articlesWithQuantities = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public Set<String> getDepartments() {
        return departments;
    }

    public void setDepartments(Set<String> departments) {
        this.departments = departments;
    }

    public void addDepartment(String department) {
        this.departments.add(department);
    }

    public Map<TemplateArticle, Integer> getArticlesWithQuantities() {
        return articlesWithQuantities;
    }

    public void setArticlesWithQuantities(Map<TemplateArticle, Integer> articlesWithQuantities) {
        this.articlesWithQuantities = articlesWithQuantities;
    }

    public void addArticleWithQuantity(TemplateArticle article, int quantity) {
        this.articlesWithQuantities.put(article, quantity);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TemplatePosition that = (TemplatePosition) o;
        return getName().equals(that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}
