package zerobase.reservation.exception;
import lombok.Getter;
import zerobase.reservation.type.ErrorCode;
@Getter
public class MemberException extends RuntimeException{
    private final ErrorCode errorCode;
    private final String errorMessage;

    public MemberException(ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getDescription();
    }
}
