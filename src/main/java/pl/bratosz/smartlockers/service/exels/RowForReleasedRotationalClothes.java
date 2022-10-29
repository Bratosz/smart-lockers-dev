package pl.bratosz.smartlockers.service.exels;

import org.apache.poi.ss.usermodel.Row;
import pl.bratosz.smartlockers.date.LocalDateConverter;
import pl.bratosz.smartlockers.service.exels.columns.ColumnType;

import java.util.Date;
import java.util.Map;

import static pl.bratosz.smartlockers.service.exels.columns.ColumnType.*;

public class RowForReleasedRotationalClothes extends RowForCloth {
    private Date releaseDate;

    public RowForReleasedRotationalClothes(Row row, Map<ColumnType, Integer> indexes) {
        super(row, indexes);
        setReleaseDate(RELEASE_DATE);
    }

    private void setReleaseDate(ColumnType releaseDate) {
        this.releaseDate = LocalDateConverter.getDate(getStringCellValueByColumnType(releaseDate));
    }

    public Date getReleaseDate() {
        return releaseDate;
    }
}
