package pl.bratosz.smartlockers.service.exels.plant.template.data;

import pl.bratosz.smartlockers.model.ClientArticle;
import pl.bratosz.smartlockers.model.clothes.ClothSize;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


public class PlantDataContainer {
    private Set<TemplatePosition> positions;
    private Map<Integer, TemplateArticle> articles;
    private Map<String, TemplateEmployee> employees;
    private Set<TemplateLockers> lockers;
    private Set<String> locations;
    private Set<String> departments;
    private Map<String, Map<TemplateArticle, ClothSize>> employeesWithArticlesAndSizes;

 public PlantDataContainer(List<ClientArticle> clientArticles) {
     Set<TemplateArticle> articles = convert(clientArticles);
     Map<Integer, TemplateArticle> articlesMap = articles.stream()
             .collect(Collectors.toMap(a -> a.getArticleNumber(), a -> a));
     this.articles = articlesMap;
     employeesWithArticlesAndSizes = new HashMap<>();
    }

    private Set<TemplateArticle> convert(List<ClientArticle> clientArticles) {
        return clientArticles.stream()
                .map(a -> new TemplateArticle(
                        a.getArticle().getNumber(),
                        a.toString(),
                        a.getBadge().getNumber()))
                .collect(Collectors.toSet());
    }

    public Set<TemplatePosition> getPositions() {
        return positions;
    }

    public void setPositions(Set<TemplatePosition> positions) {
        this.positions = positions;
    }

    public Map<Integer, TemplateArticle> getArticles() {
        return articles;
    }

    public Set<TemplateArticle> getArticlesSet() {
     return articles.values().stream().collect(Collectors.toSet());
    }

    public void setArticles(Map<Integer, TemplateArticle> articles) {
        this.articles = articles;
    }

    public Map<String, TemplateEmployee> getEmployees() {
        return employees;
    }

    public void setEmployees(Map<String, TemplateEmployee> employees) {
        this.employees = employees;
    }

    public Set<TemplateLockers> getLockers() {
        return lockers;
    }

    public void setLockers(Set<TemplateLockers> lockers) {
        this.lockers = lockers;
    }

    public Set<String> getLocations() {
        return locations;
    }

    public void setLocations(Set<String> locations) {
        this.locations = locations;
    }

    public Set<String> getDepartments() {
        return departments;
    }

    public void setDepartments(Set<String> departments) {
        this.departments = departments;
    }

    public Map<String, Map<TemplateArticle, ClothSize>> getEmployeesWithArticlesAndSizes() {
        return employeesWithArticlesAndSizes;
    }

    public void setEmployeesWithArticlesAndSizes(Map<String, Map<TemplateArticle, ClothSize>> employeesWithArticlesAndSizes) {
        this.employeesWithArticlesAndSizes = employeesWithArticlesAndSizes;
    }

    public Set<TemplateEmployee> getEmployeesSet() {
        return employees.values().stream().collect(Collectors.toSet());
    }
}
