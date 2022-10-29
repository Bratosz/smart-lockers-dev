package pl.bratosz.smartlockers.validators;

import org.springframework.web.multipart.MultipartFile;
import pl.bratosz.smartlockers.exception.WrongFileFormatException;

import static pl.bratosz.smartlockers.validators.FileFormat.*;

public class ExcelFileValidator {
    private String fileExtension;

    public ExcelFileValidator(MultipartFile file) throws WrongFileFormatException {
        fileExtension = getFileExtension(file.getOriginalFilename());
        isFileInExcelFormat();
    }


    public FileFormat getFileFormat(){
        if(fileExtension.equals(XLSX.getName())){
            return XLSX;
        } else {
            return XLS;
        }
    }

    private void isFileInExcelFormat() throws WrongFileFormatException {
        if(!(isFileInXLSXFormat() || isFileInXLSFormat())) {
        String message = "Wrong file format. Given file extension is: " + fileExtension + "." +
                "It should be " + XLS.getName() + " or " + XLSX.getName();
        throw new WrongFileFormatException(message);
        }
    }

    private boolean isFileInXLSFormat() {
        if(fileExtension.equals(XLS.getName())) {
            return true;
        }
        return false;
    }

    private boolean isFileInXLSXFormat() {
        if(fileExtension.equals(XLSX.getName())){
            return true;
        }
        return false;
    }

    private String getFileExtension(String originalFilename) {
        int dotIndex = getDotIndex(originalFilename);
        return originalFilename.substring(dotIndex);
    }

    private int getDotIndex(String originalFilename) {
        return originalFilename.indexOf('.');
    }
}
