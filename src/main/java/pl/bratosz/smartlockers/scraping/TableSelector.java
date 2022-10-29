package pl.bratosz.smartlockers.scraping;

public enum TableSelector {
    TABLE_OF_EMPLOYEE_CLOTHES("#ctl00_MainContent_GridView2 > tbody"),
    TABLE_OF_BOXES("#ctl00_MainContent_GridView102 > tbody"),
    ALL_CLOTHES_TABLE("#ctl00_MainContent_GridView100 > tbody"),
    HEADER(" > tr:nth-child(1) > th"),
    BODY(" > tr:not(:first-child)");

    private String selector;

    TableSelector(String selector) {
        this.selector = selector;
    }

    public String get() {
        return selector;
    }
}
