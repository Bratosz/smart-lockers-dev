package pl.bratosz.smartlockers.service.exels;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import pl.bratosz.smartlockers.model.clothes.ClothType;
import pl.bratosz.smartlockers.service.exels.columns.ColumnType;

import java.util.HashMap;
import java.util.Map;

import static pl.bratosz.smartlockers.service.exels.CellValueManager.*;
import static pl.bratosz.smartlockers.service.exels.columns.ColumnType.*;

public class ColumnAssigner {
    private Row headerRow;
    private Map<ColumnType, Integer> columnIndexes;

    public ColumnAssigner(Row headerRow) {
        this.headerRow = headerRow;
        columnIndexes = assignColumnsToTheirTypeWithIndexes();
    }

    public Map<ColumnType, Integer> assignColumnsToTheirTypeWithIndexes() {
        Map<ColumnType, Integer> columnIndexes = new HashMap<>();
        for (int i = 0; i < headerRow.getPhysicalNumberOfCells(); i++) {
            Cell cell = headerRow.getCell(i);
            if (isCellValueValid(cell)) {
                String content = getNormalizedStringValue(cell);
                switch (content) {
                    case "IMIĘ":
                    case "IMIE":
                    case "NAME":
                    case "FIRST_NAME":
                        columnIndexes.put(FIRST_NAME, i);
                        break;
                    case "NAZWISKO":
                    case "LAST_NAME":
                    case "SURNAME":
                        columnIndexes.put(LAST_NAME, i);
                        break;
                    case "NAZWISKO I IMIĘ":
                        columnIndexes.put(LAST_AND_FIRST_NAME, i);
                        break;
                    case "KOD KRESKOWY":
                    case "KODKRESKOWY":
                    case "KOD":
                    case "BARCODE":
                    case "BAR-CODE":
                    case "BAR CODE":
                        columnIndexes.put(BAR_CODE, i);
                        break;
                    case "LP":
                    case "LP.":
                    case "LICZBA PORZĄDKOWA":
                    case "LICZBA PORZADKOWA":
                    case "ORDINAL NUMBER":
                        columnIndexes.put(ORDINAL_NUMBER, i);
                        break;
                    case "NRART":
                    case "NR ART":
                    case "NUMER ARTYKUŁU":
                    case "NUMER ARTYKULU":
                    case "ARTICLE NUMBER":
                    case "KOD ART.":
                        columnIndexes.put(ARTICLE_NUMBER, i);
                        break;
                    case "NAZWA ODZIEŻY DO UMOWY":
                        columnIndexes.put(ARTICLE_NAME, i);
                        break;
                    case "ILOŚĆ":
                        columnIndexes.put(ARTICLE_QUANTITY, i);
                        break;
                    case "SZAFA":
                    case "SZAF":
                    case "NUMER SZAFY":
                    case "LOCKER":
                    case "LOCKER NUMBER":
                        columnIndexes.put(LOCKER_NUMBER, i);
                        break;
                    case "BOX":
                    case "BOX NUMBER":
                    case "NUMER BOXA":
                    case "NUMER SKRYTKI":
                    case "SKRYTKA":
                    case "SKR":
                        columnIndexes.put(BOX_NUMBER, i);
                        break;
                    case "STATUS BOXA":
                    case "STATUS SZAFKI":
                    case "BOX STATUS":
                    case "BOXSTATUS":
                        columnIndexes.put(BOX_STATUS, i);
                        break;
                    case "DATA WYDANIA":
                    case "RELEASEDATE":
                    case "RELEASE DATE":
                        columnIndexes.put(RELEASE_DATE, i);
                        break;
                    case "DATA PRANIA":
                    case "OSTATNIE PRANIE":
                    case "W PRANIU":
                    case "WASHINGDATE":
                    case "WASHING DATE":
                        columnIndexes.put(WASHING_DATE, i);
                        break;
                    case "PLANT NUMBER":
                    case "PLANTNUMBER":
                    case "NUMER ZAKŁADU":
                        columnIndexes.put(PLANT_NUMBER, i);
                        break;
                    case "DEPARTMENT":
                    case "ODDZIAŁ":
                    case "NAZWA ODDZIAŁU":
                        columnIndexes.put(DEPARTMENT, i);
                        break;
                    case "LOCATION":
                    case "LOCKER LOCATION":
                    case "LOKALIZACJA SZAFKI":
                    case "LOKALIZACJA":
                    case "LOKACJA":
                        columnIndexes.put(ColumnType.LOCATION, i);
                        break;
                    case "POJEMNOŚĆ":
                    case "CAPACITY":
                        columnIndexes.put(CAPACITY, i);
                        break;
                    case "ROZMIAR":
                        columnIndexes.put(SIZE, i);
                        break;
                    case "UWAGI ROZMIAROWE" :
                        columnIndexes.put(CLOTH_MODIFICATIONS, i);
                        break;
                    case "ID":
                    case "ID.":
                        columnIndexes.put(ID, i);
                        break;
                    default:
                        break;
                }
            }
        }
        return columnIndexes;
    }

    public Map<ColumnType, Integer> getColumnIndexes() {
        return columnIndexes;
    }

    public int getColumnIndex(ColumnType type) {
        return columnIndexes.get(type);
    }

    public ColumnType getColumnType(int index) {
        return columnIndexes.keySet().stream()
                .filter(columnType -> columnIndexes.get(columnType) == index)
                .findFirst().get();
    }
}
