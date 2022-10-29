package pl.bratosz.smartlockers.service.exels;

import org.apache.poi.ss.usermodel.Row;
import pl.bratosz.smartlockers.date.LocalDateConverter;
import pl.bratosz.smartlockers.service.exels.columns.ColumnType;

import java.util.Date;
import java.util.Map;

import static pl.bratosz.smartlockers.service.exels.columns.ColumnType.*;

public class RowForRotationUpdate extends RowForCloth {
    private int ordinalNo;
    private int articleNo;
    private int lockerNo;
    private int boxNo;
    private Date washingDate;

    public RowForRotationUpdate(Row row, Map<ColumnType, Integer> indexes) {
        super(row, indexes);
        setOrdinalNo(ORDINAL_NUMBER);
        setArticleNo(ARTICLE_NUMBER);
        setLockerNo(LOCKER_NUMBER);
        setBoxNo(BOX_NUMBER);
        setWashingDate(WASHING_DATE);
    }

    private void setOrdinalNo(ColumnType columnType) {
        this.ordinalNo = getNumericCellValueByColumnType(columnType);
    }

    private void setArticleNo(ColumnType columnType) {
        this.articleNo = getNumericCellValueByColumnType(columnType);
    }

    private void setLockerNo(ColumnType columnType) {
            this.lockerNo = getNumericCellValueByColumnType(columnType);
    }

    private void setBoxNo(ColumnType columnType) {
            this.boxNo = getNumericCellValueByColumnType(columnType);
    }

    private void setWashingDate(ColumnType columnType) {
        this.washingDate = LocalDateConverter.getDate(getStringCellValueByColumnType(columnType));
    }

    public int getOrdinalNo() {
        return ordinalNo;
    }

    public int getArticleNo() {
        return articleNo;
    }

    public int getLockerNo() {
        return lockerNo;
    }

    public int getBoxNo() {
        return boxNo;
    }

    public Date getWashingDate() {
        return washingDate;
    }
}
