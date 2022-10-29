package pl.bratosz.smartlockers.service.exels;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;
import pl.bratosz.smartlockers.exception.WrongFileFormatException;
import pl.bratosz.smartlockers.validators.FileFormat;
import pl.bratosz.smartlockers.validators.ExcelFileValidator;

import java.io.IOException;

import static pl.bratosz.smartlockers.validators.FileFormat.*;
public class ExcelExtractor {
    private String name;
    private int number;


    public static Workbook getWorkbook(MultipartFile file) throws IOException {
        ExcelFileValidator fileValidator = new ExcelFileValidator(file);
        FileFormat fileFormat = fileValidator.getFileFormat();
        return getWorkbook(file, fileFormat);
    }

    public static Sheet getSheet(MultipartFile file) throws IOException {
        Workbook workbook = getWorkbook(file);
        return workbook.getSheetAt(0);
    }

    public static Sheet getSheet(MultipartFile file, int sheetIndex) throws IOException {
        Workbook workbook = getWorkbook(file);
        return workbook.getSheetAt(sheetIndex);
    }


    private static Workbook getWorkbook(MultipartFile file, FileFormat fileFormat) throws IOException {
        if(fileFormat.equals(XLSX)){
            return new XSSFWorkbook(file.getInputStream());
        } else if(fileFormat.equals(XLS)){
            return new HSSFWorkbook(file.getInputStream());
        } else {
            throw new WrongFileFormatException();
        }
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
