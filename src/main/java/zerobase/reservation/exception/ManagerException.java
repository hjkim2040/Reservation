package zerobase.reservation.exception;

import lombok.Getter;
import zerobase.reservation.type.ErrorCode;

@Getter
public class ManagerException extends RuntimeException{
    private final ErrorCode errorCode;
    private final String errorMessage;

    public ManagerException(ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getDescription();
    }
}
