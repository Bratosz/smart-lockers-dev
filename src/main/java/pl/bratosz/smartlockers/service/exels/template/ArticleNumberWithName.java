package pl.bratosz.smartlockers.service.exels.template;

public class ArticleNumberWithName {
    private String number;
    private String name;

    public ArticleNumberWithName(String number, String name) {
        this.number = number;
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return number + " " + name;
    }
}
