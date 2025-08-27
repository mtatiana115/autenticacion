package co.com.bancolombia.model.user.exception;

public class RequiredDataMissingException extends RuntimeException {

    public RequiredDataMissingException (String message){
        super(message);
    }
}
