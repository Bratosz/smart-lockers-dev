package pl.bratosz.smartlockers.exception;

public class ArticleNotExistException extends Exception {
    public ArticleNotExistException(){}

    public ArticleNotExistException(String message){
        super(message);
    }
}
