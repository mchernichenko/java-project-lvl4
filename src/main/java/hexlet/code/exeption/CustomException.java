package hexlet.code.exeption;

import lombok.Getter;

@Getter
public class CustomException extends Throwable {
    private static final String EXCEPTION_DESCRIPTION_FORMAT = "[%s]: %s";
    private String errorCode;
    private String message;

    public CustomException(String errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

/*    *//*
     * Полное описание ошибки.
     * @return - ErrorDescription в формате errorCode: message
     *//*
    public String getErrorDescription() {
        return String.format(EXCEPTION_DESCRIPTION_FORMAT, this.errorCode, this.message);
    }*/
}
