package tech.tongyu.examples.util;

public class CustomerException extends RuntimeException {

    private ErrorCode errorCode;

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public CustomerException(ErrorCode errorCode, String message){
        super(message);
        this.errorCode = errorCode;
    }

    public CustomerException(String message) {
        super(message);
    }

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

}
