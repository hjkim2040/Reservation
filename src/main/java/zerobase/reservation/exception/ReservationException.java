package zerobase.reservation.exception;
import lombok.Getter;
import zerobase.reservation.type.ErrorCode;
@Getter
public class ReservationException extends RuntimeException{
    private final ErrorCode errorCode;
    private final String errorMessage;

    public ReservationException(ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getDescription();
    }
}
