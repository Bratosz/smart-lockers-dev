package pl.bratosz.smartlockers.scraping;

import java.util.Arrays;
import java.util.List;

public enum TableHeader {
    DEPARTMENT(Arrays.asList("NR ODDZIAŁU")),
    LAST_NAME(Arrays.asList("NAZWISKO")),
    FIRST_NAME(Arrays.asList("IMIE")),
    LOCKER_NUMBER(Arrays.asList("SZAFA")),
    BOX_NUMBER(Arrays.asList("BOX")),
    BARCODE(Arrays.asList("KOD KRESKOWY")),
    ASSIGNMENT(Arrays.asList("DATA PRZYPISANIA")),
    ORDINAL_NUMBER(Arrays.asList("EGZ.")),
    ARTICLE_NUMBER(Arrays.asList("NR ART.", "ARTYKUŁ")),
    ARTICLE_NAME(Arrays.asList("NAZWA ART.", "NAZWA ART")),
    COMMENT(Arrays.asList("UWAGI")),
    LAST_WASHING(Arrays.asList("OSTATNIE PRANIE")),
    SIZE(Arrays.asList("ROZMIAR")),
    RELEASE(Arrays.asList("DATA WYDANIA"));

    List<String> names;

    TableHeader(List<String> names) {
        this.names = names;
    }

    public List<String> getNames() {
        return names;
    }
}
