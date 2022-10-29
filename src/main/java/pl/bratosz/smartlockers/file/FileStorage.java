package pl.bratosz.smartlockers.file;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.bratosz.smartlockers.response.DownloadFileResponse;
import pl.bratosz.smartlockers.utils.Utils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileStorage {
    private Path fileLocation;
    private String fileName;
    private String fileType;
    private String fileNameWithExtension;
    private XSSFWorkbook workbook;
    private XWPFDocument document;
    private DownloadFileResponse downloadFileResponse;

    public FileStorage() {
        String property = System.getProperty("java.io.tmpdir");
        if(!property.contains("my-temp")) {
            try {
                fileLocation = Files.createTempDirectory("my-temp");
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.setProperty(
                    "java.io.tmpdir",
                    fileLocation.toAbsolutePath().toString());
        } else {
            fileLocation = Paths.get(property);
        }
    }

    public DownloadFileResponse get(XSSFWorkbook workbook, String fileName) {
        this.workbook = workbook;
        this.fileName = fileName;
        fileType = ".xlsx";
        createResponse();
        if(fileAlreadyExist(this.fileName, fileType)) {
            return downloadFileResponse;
        } else {
            try {
                saveWorkbook();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return downloadFileResponse;
        }
    }

    public DownloadFileResponse storeAndGet(XSSFWorkbook workbook, String fileName) {
        int i = 0;
        this.workbook = workbook;
        this.fileName = fileName;
        fileType = ".xlsx";
        while (fileAlreadyExist(this.fileName, fileType)) {
            this.fileName = fileName + " (" + ++i + ")";
        }
        createResponse();
        try {
            saveWorkbook();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return downloadFileResponse;
    }

    private boolean fileAlreadyExist(String fileName, String fileExtension) {
        String fileNameWithExtension = fileName + fileExtension;
        try {
            Utils.getTempFileBy(fileNameWithExtension);
        } catch (FileNotFoundException e) {
            return false;
        }
        return true;
    }

    private DownloadFileResponse createResponse() {
        createFileNameWithExtension();
        String uri = createDownloadURI();
        downloadFileResponse =
                new DownloadFileResponse(true, fileName, uri, fileType, 0l);
        return downloadFileResponse;
    }

    private String createDownloadURI() {
        return ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/file/download/")
                .path(fileNameWithExtension)
                .toUriString();
    }

    private String createDownloadURI(String fileNameWithExtension) {
        return ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/file/download/")
                .path(fileNameWithExtension)
                .toUriString();
    }

    private void saveWorkbook() throws Exception {
        FileOutputStream fileOut = getOutputStream();
        workbook.write(fileOut);
        fileOut.close();
        workbook.close();
    }

    private void saveDocument() throws Exception {
        FileOutputStream fileOut = getOutputStream();
        document.write(fileOut);
        fileOut.close();
        document.close();
    }

    private FileOutputStream getOutputStream() throws FileNotFoundException {
        Path targetLocation = fileLocation.resolve(fileNameWithExtension);
        String fileLocation = targetLocation.toAbsolutePath().toString();
        return new FileOutputStream(fileLocation);
    }

    private void createFileNameWithExtension() {
        fileNameWithExtension = fileName + fileType;
    }
}
