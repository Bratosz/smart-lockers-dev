package pl.bratosz.smartlockers.response;

public class DownloadFileResponse {
    private boolean succeed;
    private String fileName;
    private String fileDownloadUri;
    private String fileType;
    private String message;
    private long size;

    private DownloadFileResponse() {
    }

    public DownloadFileResponse(
            boolean succeed,
            String fileName,
            String fileDownloadUri,
            String fileType,
            long size) {
        this.succeed = succeed;
        this.fileName = fileName;
        this.fileDownloadUri = fileDownloadUri;
        this.fileType = fileType;
        this.size = size;
    }

    public DownloadFileResponse(String message) {
        this.message = message;
        this.succeed = false;
        this.fileName = "";
        this.fileDownloadUri = "";
        this.fileType = "";
        this.size = 0;
    }

    public static DownloadFileResponse createForFileAlreadyExist(
            String filename,
            String fileDownloadUri) {
        DownloadFileResponse r = new DownloadFileResponse();
        r.setSucceed(false);
        r.setFileName(filename);
        r.setFileDownloadUri(fileDownloadUri);
        r.setFileType("");
        return r;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileDownloadUri() {
        return fileDownloadUri;
    }

    public void setFileDownloadUri(String fileDownloadUri) {
        this.fileDownloadUri = fileDownloadUri;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public boolean isSucceed() {
        return succeed;
    }

    public void setSucceed(boolean succeed) {
        this.succeed = succeed;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
