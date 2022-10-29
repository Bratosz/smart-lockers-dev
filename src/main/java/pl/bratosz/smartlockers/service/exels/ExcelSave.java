package pl.bratosz.smartlockers.service.exels;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;

public class ExcelSave {

    public String save(XSSFWorkbook workbook, String folderName) throws IOException {
        String fileName = workbook.getSheetName(0) + ".xlsx";
        FileOutputStream fileOut = new FileOutputStream("C:/Users/HP/Desktop/files_to_testing/Lear/" + folderName + "/"
                + fileName);
        workbook.write(fileOut);
        fileOut.close();
        workbook.close();
        return fileName;
    }
}
