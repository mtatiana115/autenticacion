package co.com.bancolombia.model.user.exception;

public class EmailAlreadyExistsException extends RuntimeException {

    public EmailAlreadyExistsException (String message){
        super(message);
    }

}
